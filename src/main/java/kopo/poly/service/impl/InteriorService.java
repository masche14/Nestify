package kopo.poly.service.impl;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.mapper.IInteriorMapper;
import kopo.poly.service.IInteriorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteriorService implements IInteriorService {
    private final IInteriorMapper interiorMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${inputImgDir}")
    private String inputImgDir;

    @Value("${generatedImgDir}")
    private String generatedImgDir;

    @Value("${OpenAi_Key}")
    private String openAiKey;


    // G_RECORD 데이터베이스 추가
    @Transactional
    @Override
    public int insertRecord(GRecordDTO pDTO) throws Exception {
        log.info("{}.insertRecord Start", this.getClass().getName());

        int res;

        int success = interiorMapper.insertRecord(pDTO);

        if (success>0) {
            res = 1;
        } else {
            res = 0;
        }

        log.info("{}.insertRecord End", this.getClass().getName());

        return res;
    }

    // G_RECORD 유저 기록 조회
    @Override
    public List<GRecordDTO> getRecords(GRecordDTO pDTO) throws Exception {
        log.info("{}.getRecords Start", this.getClass().getName());
        List<GRecordDTO> rList = interiorMapper.getRecords(pDTO);
        log.info("{}.getRecords End", this.getClass().getName());
        return rList;
    }

    // 이미지 이름 재생성
    @Override
    public String fileNameEncode(String userId){
        LocalDate localdate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = localdate.format(dateFormatter);

        LocalTime localtime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String time = localtime.format(timeFormatter);


        return userId+"_"+date+time;
    }

    // API 이미지 생성 요청
    @Override
    public String generateImg(File savedFile, String prompt, String userId) throws Exception {
        log.info("이미지 생성 시작");

        if (!savedFile.exists()) {
            log.error("저장된 파일을 찾을 수 없습니다.");
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

//        // 이미지 파일을 읽어 BufferedImage로 변환
//        BufferedImage bufferedImage = ImageIO.read(savedFile);
//
//        // 이미지 포맷이 RGB이면 RGBA로 변환
//        BufferedImage rgbaImage = new BufferedImage(
//                bufferedImage.getWidth(),
//                bufferedImage.getHeight(),
//                BufferedImage.TYPE_INT_ARGB // RGBA 포맷으로 변환
//        );
//
//        // 기존 이미지의 픽셀을 RGBA 이미지에 복사
//        Graphics2D g2d = rgbaImage.createGraphics();
//        g2d.drawImage(bufferedImage, 0, 0, null);
//        g2d.dispose();
//
//        // PNG로 변환 후 저장
//        String newFileName = fileNameEncode(userId) + ".png";
//        log.info("새로운 파일명 : {}", newFileName);
//
//        String saveDir = "C:/uploads";
//        File saveDirectory = new File(saveDir);
//        if (!saveDirectory.exists()) {
//            saveDirectory.mkdirs();  // 디렉토리가 없으면 생성
//        }
//
//        File convertedFile = new File(saveDirectory, newFileName);
//        ImageIO.write(rgbaImage, "png", convertedFile);
//
//        log.info("파일 저장 경로: {}", convertedFile.getAbsolutePath());
//
//        // -------------------------------------------------------------
//
//        // 2. 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.set("Authorization", "Bearer " + openAiKey);
//        log.info("헤더 설정 완료");
//
//        // 3. 파일과 프롬프트를 전송할 Multipart 요청 바디 구성
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("image", new FileSystemResource(convertedFile));  // 변환된 PNG 파일 전송
//        body.add("prompt", prompt);
//        body.add("n", 1);
//        body.add("size", "512x512");
//        log.info("바디 생성 완료");
//
//        // 4. 요청 엔티티 생성
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        log.info("엔티티 생성 완료");
//
//        // 5. OpenAI API 호출
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://api.openai.com/v1/images/edits",
//                HttpMethod.POST,
//                requestEntity,
//                String.class
//        );
//        log.info("API 호출 완료");
//
//        log.info("API 응답 내용: {}", response.toString());
//        log.info("API 응답 본문(JSON): {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody()));
//
//        // 6. 응답 처리
//        if (response.getStatusCode() == HttpStatus.OK) {
//
//            JsonNode rootNode = objectMapper.readTree(response.getBody());
//            JsonNode dataNode = rootNode.path("data");
//
//            if (dataNode.isArray() && dataNode.size() > 0) {
//                String generatedImageUrl = dataNode.get(0).path("url").asText();
//                convertedFile.delete();
//                return generatedImageUrl;
//            } else {
//                throw new RuntimeException("이미지 URL을 찾을 수 없습니다.");
//            }
//        } else {
//            throw new RuntimeException("API 요청 실패: " + response.getStatusCode());
//        }
        // 파일 경로
        String imagePath = savedFile.getAbsolutePath();

        // Base64 인코딩
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // API URL
        String apiUrl = "https://modelslab.com/api/v6/realtime/img2img";


        return  null;
    }
}
