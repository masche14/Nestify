package kopo.poly.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.service.IInteriorService;
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

    @Value("${rootPath}")
    private String rootPath;

    @Value("${inputImgDir}")
    private String inputImgDir;

    @Value("${generatedImgDir}")
    private String generatedImgDir;

    @GetMapping("/makeNew")
    public  String showMakeNewPage(HttpSession session){
        String SS_USER_ID = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

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

        String saveDir = null;
        String outputPath = null;
        String fileName = image.getOriginalFilename();
        String newFileName = interiorService.fileNameEncode(userId)+".png";
        String fileUrl = null;

        // 카운트가 0일 때만 서버에 이미지 저장
        if (count == 0 && !image.isEmpty()) {
            // 서비스 클래스로 분리 후 코드 간소화 예정

            saveDir = rootPath+File.separator+inputImgDir;

            File saveDirectory = new File(saveDir);

            // 디렉토리가 존재하지 않으면 생성
            if (!saveDirectory.exists()) {
                saveDirectory.mkdirs();
            }

            File dest = new File(saveDir + File.separator + newFileName);
            outputPath=dest.getAbsolutePath();
            log.info("사용자 첨부 이미지 저장 : {}", outputPath);

            try {
                image.transferTo(dest);
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생: ", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 오류 발생 시 500 응답
            }

            session.setAttribute("inputImgName", fileName);
            session.setAttribute("newInputImgName", newFileName);
            fileUrl = File.separator+inputImgDir + File.separator + newFileName;

        } else if (count>0) {
            log.info("재생성 요청된 이미지 url : {}", imageUrl.toString());
            log.info("파일명 : {}", imageUrl.substring(imageUrl.lastIndexOf("/")+1));

            String saveYn = "n";

            outputPath = saveImageToServer(imageUrl, session, saveYn);

            log.info("재생성 이미지 임시 저장 완료 : {}", outputPath);

            fileUrl = outputPath.split(":")[1];
        }

        log.info("fileUrl : {}", fileUrl);

        String sourceUrl = fileUrl.replace("\\","/");

        String forApiUrl = "http://localhost:11000"+sourceUrl;

        log.info("forApiUrl : {}", forApiUrl);

        log.info("프롬프트 내용 : {}", prompt);

        File savedFile = new File(outputPath);

        // API 요청
//        String generatedImageUrl = interiorService.generateImg(savedFile, prompt, userId);
        String generatedImageUrl = "/myPageImg.webp";
        log.info("generatedImageUrl : {}",generatedImageUrl);

//        if (count > 0) {
//            log.info(savedFile.getAbsolutePath());
//            savedFile.delete();
//            log.info("임시파일 삭제 완료");
//        }

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

        int res = 0;
        GRecordDTO pDTO;

        session.removeAttribute("imageCount");

        String imageUrl = data.get("imageUrl");

        String saveYn = "y";

        // 이미지 URL을 이용해 실제 이미지 저장 로직 수행
        try {
            // 서버에 이미지 저장 로직 (예: 이미지 다운로드 후 저장)
            saveImageToServer(imageUrl, session, saveYn); // 실제 저장 로직은 구현 필요

            log.info("디버그 1");
            // 서버에 이미지 저장 로직 (예: 이미지 다운로드 후 저장)
            String userId = (String) session.getAttribute("SS_USER_ID");
            String inputImgName = (String) session.getAttribute("inputImgName");
            String generatedImgName = (String) session.getAttribute("generatedImgName");
            String regId = (String) session.getAttribute("SS_USER_ID");
            String chgId = (String) session.getAttribute("SS_USER_ID");
            String newInputImgName = interiorService.fileNameEncode(userId);

            log.info("userId : {}", userId);
            log.info("rootPath : {}", rootPath);
            log.info("inputImgDir : {}", inputImgDir);
            log.info("inputImgName : {}", inputImgName);
            log.info("newInputImgName : {}", newInputImgName);
            log.info("generatedImgDir : {}", generatedImgDir);
            log.info("generatedImgName : {}", generatedImgName);
            log.info("regId : {}", regId);
            log.info("chgId : {}", chgId);

            pDTO = new GRecordDTO();

            pDTO.setUserId(userId);
            pDTO.setRootPath(rootPath);
            pDTO.setInputImgDir(inputImgDir);
            pDTO.setInputImgName(inputImgName);
            pDTO.setNewInputImgName(newInputImgName);
            pDTO.setGeneratedImgDir(generatedImgDir);
            pDTO.setGeneratedImgName(generatedImgName);
            pDTO.setRegId(regId);
            pDTO.setChgId(chgId);

            res = interiorService.insertRecord(pDTO);

            log.info("레코드 저장 결과(res) : {}", res);

            if (res==1) {
                log.info("성공적으로 저장하였습니다.");
                return new ResponseEntity<>("Image saved successfully", HttpStatus.OK);
            } else {
                log.info("오류로 인해 실패하였습니다.");
                return new ResponseEntity<>("Image saved successfully", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 이미지 저장 메서드 (예시) -> 서비스 클래스로 분리예정
    private String saveImageToServer(String imageUrl, HttpSession session, String saveYn) throws IOException {
        // imageUrl을 이용해 이미지 다운로드 후 저장하는 로직 구현
        // 예: 서버의 디렉토리에 이미지 저장
        log.info(imageUrl);

        String userId = (String) session.getAttribute("SS_USER_ID");

        File dir = new File(rootPath+File.separator+generatedImgDir);

        // URL로부터 InputStream 가져오기
        URL url = new URL(imageUrl);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Path outputPath;

        try {
            inputStream = url.openStream(); // 이미지 데이터를 가져옴

            // 이미지 파일명을 지정할 때 원본 URL에서 파일명 추출 가능
            String fileName = "ai_"+interiorService.fileNameEncode(userId) + ".png";

            if (saveYn.equals("y")) {
                // 저장할 경로와 파일 이름 설정
                outputPath = Paths.get(dir + File.separator + fileName);
            } else {
                outputPath = Paths.get("C:/uploads" + File.separator + fileName);
            }

            // 디렉토리 생성 (존재하지 않으면)
            Files.createDirectories(outputPath.getParent());

            // OutputStream을 사용해 이미지 파일로 저장
            outputStream = new FileOutputStream(outputPath.toFile());

            // 버퍼를 사용해 이미지 데이터를 파일로 저장
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            log.info("이미지 저장 완료: " + outputPath.toString());
            session.setAttribute("generatedImgName", fileName);

        } catch (IOException e) {
            log.error("이미지 저장 중 오류 발생: ", e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // InputStream과 OutputStream 닫기
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }

        return outputPath.toString();
    }

    @GetMapping("/result")
    public  String showResult(HttpSession session){
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

        log.info(jsonRList);

        session.setAttribute("rList", rList);

        return "/Interior/records";
    }

    @GetMapping("/choose")
    public String showChoosePage(){
        return "/Interior/choose";
    }
}
