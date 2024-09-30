package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.service.IInteriorService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/Interior")
public class InteriorController {
    private final IInteriorService interiorService;

    @Value("${inputImgDir}")
    private String inputImgDir;

    @GetMapping("/makeNew")
    public  String showMakeNewPage(HttpSession session){
        String SS_USER_ID = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        return "/Interior/makeNew";
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> procUpload(
            HttpSession session,
            @RequestParam("image") MultipartFile image,
            @RequestParam("prompt") String prompt
    ) throws Exception {
        log.info("{}.procUpload 시작", this.getClass().getName());

        Integer count = (Integer) session.getAttribute("imageCount");
        if (count == null) {
            count = 0;
            session.setAttribute("imageCount", count);
        }

        log.info(String.valueOf(count));

        // 카운트가 0일 때만 서버에 이미지 저장
        if (count == 0 && !image.isEmpty()) {
            // 서비스 클래스로 분리 후 코드 간소화 예정

            File dir = new File(inputImgDir);

            // 디렉토리가 존재하지 않으면 생성
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = image.getOriginalFilename();
            File dest = new File(inputImgDir + File.separator + fileName);

            log.info("사용자 첨부 이미지 저장 : {}", dest.getAbsolutePath());

            try {
                image.transferTo(dest);
                session.setAttribute("inputImgPath", dest.getAbsolutePath());
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생: ", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 오류 발생 시 500 응답
            }

            session.setAttribute("uploadedImage", fileName);
        }

        log.info("프롬프트 내용 : {}", prompt);

        // API 호출로 생성된 이미지를 세션에 임시 저장
        String generatedImageUrl = callImageGenerationApi(image, prompt); // API에서 생성된 이미지 URL

        // 세션에 API로 생성된 이미지 저장
        session.setAttribute("generatedImageUrl", generatedImageUrl);
        session.setAttribute("imageCount", ++count); // 카운트 증가

        // JSON 형식으로 응답을 반환
        Map<String, String> response = new HashMap<>();
        response.put("generatedImageUrl", generatedImageUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // API로 이미지와 프롬프트를 전송하여 이미지 생성 URL을 반환하는 메서드 (예시) -> 서비스 클래스로 분리예정
    private String callImageGenerationApi(MultipartFile file, String prompt) {
        // 실제 API 호출 로직 (여기서는 예시로 하드코딩된 URL 반환)
        return "/myPageImg.webp";
    }

    @PostMapping("/saveGeneratedImage")
    public ResponseEntity<String> saveGeneratedImage(@RequestBody Map<String, String> data, HttpSession session) {
        log.info("{}.saveGeneratedImage 시작", this.getClass().getName());

        int res = 0;

        session.removeAttribute("imageCount");

        String imageUrl = data.get("imageUrl");

        // 이미지 URL을 이용해 실제 이미지 저장 로직 수행
        try {
            log.info("디버그 1");
            // 서버에 이미지 저장 로직 (예: 이미지 다운로드 후 저장)
            String userId = (String) session.getAttribute("SS_USER_ID");
            String inputImgPath = (String) session.getAttribute("inputImgPath");
            String inputImg = inputImgPath.split("static")[1];
            String generatedImgPath = interiorService.saveImageToServer(imageUrl); // 실제 저장 로직은 구현 필요
            String generatedImg = generatedImgPath.split("static")[1];

            log.info("userId : {}", userId);
            log.info("inputImg : {}", inputImg);
            log.info("generatedImg : {}", generatedImg);

            GRecordDTO pDTO = new GRecordDTO();

            pDTO.setUserId(userId);
            pDTO.setInputImg(inputImg);
            pDTO.setGeneratedImg(generatedImg);

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
            log.info(e.toString());
            return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/result")
    public  String showResult(HttpSession session){
        return "/Interior/result";
    }
}
