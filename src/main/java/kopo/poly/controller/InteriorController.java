package kopo.poly.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.service.IInteriorService;
import kopo.poly.service.IS3Service;
import kopo.poly.service.impl.S3Service;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/Interior")
public class InteriorController {
    private final IInteriorService interiorService;
    private final IS3Service s3Service;

    @Value("${upload.dir.rootPath}")
    private String rootPath;

    // 사용자 이미지 저장 폴더 경로
    @Value("${s3.folder.user-images}")
    private String userImagesFolder;

    // 생성된 이미지 저장 폴더 경로
    @Value("${s3.folder.generated-images}")
    private String generatedImagesFolder;

    @GetMapping("/makeNew")
    public  String showMakeNewPage(HttpSession session){
        String SS_USER_ID = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        session.removeAttribute("imageCount");

        try {
            s3Service.deleteTemporaryImages();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "/Interior/makeNew";
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> procUpload(
            HttpSession session,
            @RequestParam("image") MultipartFile image,
            @RequestParam("prompt") String prompt,
            @RequestParam("imageUrl") String imageUrl
    ) throws Exception {
        log.info("{}.procUpload 시작", this.getClass().getName());

        Integer count = (Integer) session.getAttribute("imageCount");
        if (count == null) {
            count = 0;
            session.setAttribute("imageCount", count);
        }

        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info(String.valueOf(count));

        String fileName = image.getOriginalFilename();
        session.setAttribute("userOriginInput", fileName);

        // 카운트가 0일 때만 서버에 이미지 저장
        if (count == 0 && !image.isEmpty()) {
            // 서비스 클래스로 분리 후 코드 간소화 예정

            // S3
            imageUrl = s3Service.uploadUserImageToS3(image, userId);

            String newFileName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);

            session.setAttribute("newInputImgName", newFileName);

        } else if (count>0) {
            log.info("재생성 요청된 이미지 url : {}", imageUrl.toString());
            log.info("파일명 : {}", imageUrl.substring(imageUrl.lastIndexOf("/")+1));
        }


        log.info("imageUrl : {}", imageUrl);

        log.info("프롬프트 내용 : {}", prompt);

        // API 요청
        String generatedImageUrl = interiorService.generateImg(imageUrl, prompt);
        log.info("generatedImageUrl : {}",generatedImageUrl);

        // 세션에 API로 생성된 이미지 저장
        session.setAttribute("generatedImageUrl", generatedImageUrl);
        session.setAttribute("imageCount", ++count); // 카운트 증가

        // JSON 형식으로 응답을 반환
        Map<String, String> response = new HashMap<>();
        response.put("generatedImageUrl", generatedImageUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/saveGeneratedImage")
    public ResponseEntity<String> saveGeneratedImage(@RequestBody Map<String, String> data, HttpSession session) {
        log.info("{}.saveGeneratedImage 시작", this.getClass().getName());

        int generateSeq;

        int res = 0;
        GRecordDTO pDTO;

        session.removeAttribute("imageCount");

        String imageUrl = data.get("imageUrl");

        String fileName;

        try {

            // 서버에 이미지 저장 로직 (예: 이미지 다운로드 후 저장)
            String userId = (String) session.getAttribute("SS_USER_ID");

            String generatedImgUrl = s3Service.uploadApiResponseImageToS3(imageUrl, userId);

            String inputImgName = (String) session.getAttribute("userOriginInput");

            String generatedImgName = generatedImgUrl.substring(generatedImgUrl.lastIndexOf("/")+1);

            String regId = (String) session.getAttribute("SS_USER_ID");
            String chgId = (String) session.getAttribute("SS_USER_ID");
            String newInputImgName = (String) session.getAttribute("newInputImgName");

            fileName = newInputImgName;

            log.info("userId : {}", userId);
            log.info("rootPath : {}", rootPath);
            log.info("inputImgDir : {}", userImagesFolder);
            log.info("inputImgName : {}", inputImgName);
            log.info("newInputImgName : {}", newInputImgName);
            log.info("generatedImgDir : {}", generatedImagesFolder);
            log.info("generatedImgName : {}", generatedImgName);
            log.info("regId : {}", regId);
            log.info("chgId : {}", chgId);

            pDTO = new GRecordDTO();

            pDTO.setUserId(userId);
            pDTO.setRootPath(rootPath);
            pDTO.setInputImgDir(userImagesFolder);
            pDTO.setInputImgName(inputImgName);
            pDTO.setNewInputImgName(newInputImgName);
            pDTO.setGeneratedImgDir(generatedImagesFolder);
            pDTO.setGeneratedImgName(generatedImgName);
            pDTO.setRegId(regId);
            pDTO.setChgId(chgId);

            log.info("generatedImgUrl : {}", generatedImgUrl);

            res = interiorService.insertRecord(pDTO);

            log.info("레코드 저장 결과(res) : {}", res);

            if (res==1) {
                log.info("성공적으로 저장하였습니다.");

                int result;

                // generatedSeq 추출
                GRecordDTO rDTO = interiorService.getGenerateSeq(pDTO);
                generateSeq = rDTO.getGenerateSeq();

                List<DetailDTO> resp;

//                log.info("이미지 분석을 위한 이미지 경로 : {}", imagePath);
                try {
                    resp = interiorService.runImgAnalysisPython(imageUrl);

                    if (resp.isEmpty()){
                        log.info("이미지 분석 중 오류가 발생하였습니다.");

                        log.info("오류가 발생한 기록을 삭제합니다.");
                        pDTO.setGenerateSeq(generateSeq);
                        int deleteResult = interiorService.deleteRecord(pDTO);
                        if (deleteResult > 0) {
                            log.info("삭제 완료");
                            s3Service.deleteSpecificFile(generatedImagesFolder, generatedImgName);

                        } else {
                            log.info("삭제 실패");
                        }

                        log.info("저장된 AI 생성 이미지 삭제");

                        return new ResponseEntity<>("이미지 분석 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    else {
                        for (DetailDTO detailDTO : resp) {
                            detailDTO.setUserId(userId);
                            detailDTO.setGenerateSeq(generateSeq);
                            detailDTO.setRegId(regId);
                            detailDTO.setChgId(chgId);
                            log.info("userId : {} / generateSeq : {} / category : {} / productName : {} / color : {} / features : {}", detailDTO.getUserId(), detailDTO.getGenerateSeq(), detailDTO.getCategory(), detailDTO.getProductName(), detailDTO.getColor(), detailDTO.getFeatures());

                            // 디테일 테이블에 데이터 입력
                            result = interiorService.insertDetail(detailDTO);
                            if (result == 1) {
                                log.info("데이터를 성공적으로 저장하였습니다.");

                                session.setAttribute("analysisResult", resp);
                            } else {
                                log.info("데이터 저장에 실패하였습니다.");
                            }
                        }
                    }

                } catch (Exception e) {
                    return new ResponseEntity<>("이미지 분석에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                session.setAttribute("generatedImgUrl", generatedImgUrl);

                // 수정필요
                List<RecommendDTO> recommendList = interiorService.getRecommend(imageUrl, resp);

                if (!recommendList.isEmpty()) {
                    log.info("제품 추천 리스트 선정 완료");
                    String jsonRList = new Gson().toJson(recommendList);
                    log.info("jsonRList : {}", jsonRList);
                    session.setAttribute("recommendList", recommendList);

                    // 임시 저장 폴더에서 옮기기
                    String userImgUrl = s3Service.moveImageToUserImagesFolder(fileName, userId);
                    log.info("userImgUrl : {}", userImgUrl);

                    try {
                        s3Service.deleteTemporaryImages();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }


                } else {
                    log.info("제품 추천 중 오류가 발생하였습니다.");

                    log.info("generateSeq : {}", generateSeq);
                    pDTO.setGenerateSeq(generateSeq);

                    int deleteResult = interiorService.deleteRecord(pDTO);
                    if (deleteResult > 0) {
                        log.info("삭제 완료");
                        s3Service.deleteSpecificFile(generatedImagesFolder, generatedImgName);
                    } else {
                        log.info("삭제 실패");
                    }
                    return new ResponseEntity<>("오류로 인해 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity<>("Image saved successfully", HttpStatus.OK);
            } else {
                log.info("오류로 인해 실패하였습니다.");
                return new ResponseEntity<>("오류로 인해 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("이미지 저장에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/result")
    public  String showResult(HttpSession session){
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID == null){
            return "redirect:/User/index";
        }

        List<DetailDTO> rList = (List<DetailDTO>) session.getAttribute("analysisResult");
        if (rList == null) {
            return "redirect:/User/index";
        }

        List<RecommendDTO> recommendList = (List<RecommendDTO>) session.getAttribute("recommendList");
        if (recommendList == null) {
            return "redirect:/User/index";
        }

        return "/Interior/result";
    }

    @GetMapping("/records")
    public String showRecords(HttpSession session, Model model) throws Exception{
        String userId = (String) session.getAttribute("SS_USER_ID");
        GRecordDTO pDTO = new GRecordDTO();
        pDTO.setUserId(userId);

        List<GRecordDTO> rList = interiorService.getRecords(pDTO);

        String jsonRList = new Gson().toJson(rList);
        session.setAttribute("jsonRList", jsonRList);

        DetailDTO paramDTO = new DetailDTO();
        paramDTO.setUserId(userId);

        List<DetailDTO> resList = interiorService.getDetail(paramDTO);

        String jsonResList = new Gson().toJson(resList);
        session.setAttribute("jsonResList", jsonResList);

        log.info("jsonRList : {}", jsonRList);
        log.info("jsonResList : {}", jsonResList);

        session.setAttribute("rList", rList);
        session.setAttribute("resList", resList);

        return "/Interior/records";
    }

    @GetMapping("/choose")
    public String showChoosePage(){
        return "/Interior/choose";
    }
}
