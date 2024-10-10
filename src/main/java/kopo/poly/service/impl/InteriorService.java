package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.mapper.IInteriorMapper;
import kopo.poly.service.IInteriorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
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

    @Value("${IMAGEN_KEY}")
    private String imagenKey;


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

        // 파일 경로
        String imagePath = savedFile.getAbsolutePath();
        String fileName = "ai_"+fileNameEncode(userId)+".png";
        String outputPath = "C:/uploads/"+fileName;

        try {
            // 이미지 파일을 Base64로 인코딩
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            // API URL
            String apiUrl = "https://modelslab.com/api/v6/realtime/img2img";

            log.info("요청 데이터 생성");
            // 요청할 JSON 데이터 생성
            JSONObject payload = new JSONObject();
            payload.put("key", imagenKey);
            payload.put("prompt", prompt);
            payload.put("negative_prompt", "bad quality");
            payload.put("init_image", encodedImage);
            payload.put("width", "512");
            payload.put("height", "512");
            payload.put("samples", "1");
            payload.put("temp", false);
            payload.put("safety_checker", false);
            payload.put("strength", 0.5);
            payload.put("instant_response", false);
            payload.put("base64", true);
            payload.put("seed", JSONObject.NULL);
            payload.put("webhook", JSONObject.NULL);
            payload.put("track_id", JSONObject.NULL);

            log.info("요청데이터 생성 완료");

            // HTTP POST 요청 설정
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setInstanceFollowRedirects(false);  // 리디렉션을 따르지 않음
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            log.info("요청 전송");
            // JSON 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 받기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            log.info("응답 도착");
            // JSON 응답 처리
            Map<String, Object> responseJson = new ObjectMapper().readValue(response.toString(), LinkedHashMap.class);
            if ("success".equals(responseJson.get("status"))) {
                // Base64 데이터가 있는 링크를 가져오기
                List<String> output = (List<String>) responseJson.get("output");
                String base64DataUrl = output.get(0);

                log.info("base64DataUrl : {}", base64DataUrl);

                // base64 데이터 다운로드 및 이미지로 변환 후 저장
                String savedImagePath = downloadBase64Image(base64DataUrl, outputPath);

                // 이미지 파일 경로를 URL로 변환하여 반환
                return "http://localhost:11000"+savedImagePath;
            } else {
                System.out.println("API 요청 실패: " + responseJson.get("message"));
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String downloadBase64Image(String base64DataUrl, String outputPath) {
        int retryCount = 3; // 최대 재시도 횟수
        int delay = 5000;   // 5초 지연 (밀리초 단위)

        for (int i = 0; i < retryCount; i++) {
            try {
                // base64 인코딩된 데이터를 담고 있는 URL에서 데이터 가져오기
                URL url = new URL(base64DataUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                log.info("인코딩 페이지 로드");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 링크로부터 데이터를 읽어오기
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder base64Data = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        base64Data.append(line.trim());
                    }
                    log.info("데이터 읽어오기");

                    log.info("이미지 변환 시작");
                    // base64 문자열을 디코딩하여 이미지로 변환
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data.toString());
                    log.info("이미지 변환 완료");

                    // 파일로 저장
                    try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                        fos.write(imageBytes);
                        log.info("이미지 파일이 '" + outputPath + "'로 저장되었습니다.");
                    }

                    return outputPath.split(":")[1];
                } else {
                    log.error("HTTP 요청 실패: 응답 코드 " + responseCode);
                }

            } catch (Exception e) {
                log.error("오류 발생: ", e);
            }

            // 재시도 전에 일정 시간 대기
            try {
                log.info("재시도 대기 중: " + (i + 1) + "번째 시도");
                Thread.sleep(delay); // 5초 대기
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // 현재 스레드의 인터럽트를 복구
                break;
            }
        }

        return null;
    }

    public void delTempFolder() throws Exception {
        String folderPath = "C:/uploads/";
        File folder = new File(folderPath);

        // 폴더가 존재하고 디렉토리인지 확인
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // 폴더 내 파일 목록 가져오기

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // 파일 삭제
                        if (file.delete()) {
                            System.out.println(file.getName() + " 파일이 삭제되었습니다.");
                        } else {
                            System.out.println(file.getName() + " 파일을 삭제할 수 없습니다.");
                        }
                    }
                }
            }
        } else {
            System.out.println("해당 폴더가 존재하지 않거나 디렉토리가 아닙니다.");
        }
    }

    @Override
    public String runImgAnalysisPython(String imagePath) {
        try {
            // 파이썬 실행 명령어
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/python/image_analysis.py", imagePath);
            Process process = pb.start();

            // 프로세스의 출력 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            process.waitFor();
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Python 스크립트 실행 중 오류 발생";
        }
    }
}
