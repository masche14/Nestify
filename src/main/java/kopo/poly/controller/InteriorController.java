package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            @RequestParam("prompt") String prompt
    ) throws Exception {
        log.info("{} 시작", this.getClass().getName());

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
        session.removeAttribute("imageCount");

        String imageUrl = data.get("imageUrl");

        // 이미지 URL을 이용해 실제 이미지 저장 로직 수행
        try {
            // 서버에 이미지 저장 로직 (예: 이미지 다운로드 후 저장)
            saveImageToServer(imageUrl); // 실제 저장 로직은 구현 필요
            return new ResponseEntity<>("Image saved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 이미지 저장 메서드 (예시) -> 서비스 클래스로 분리예정
    private void saveImageToServer(String imageUrl) throws IOException {
        // imageUrl을 이용해 이미지 다운로드 후 저장하는 로직 구현
        // 예: 서버의 디렉토리에 이미지 저장
        log.info(imageUrl);

        File dir = new File(generatedImgDir);

        // URL로부터 InputStream 가져오기
        URL url = new URL(imageUrl);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = url.openStream(); // 이미지 데이터를 가져옴

            // 이미지 파일명을 지정할 때 원본 URL에서 파일명 추출 가능
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // 저장할 경로와 파일 이름 설정
            Path outputPath = Paths.get(generatedImgDir + File.separator + fileName);

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

        } catch (IOException e) {
            log.error("이미지 저장 중 오류 발생: ", e);
            throw e;
        } finally {
            // InputStream과 OutputStream 닫기
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }
    }

    @GetMapping("/result")
    public  String showResult(HttpSession session){
        return "/Interior/result";
    }
}
