package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.mapper.IInteriorMapper;
import kopo.poly.service.IInteriorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteriorService implements IInteriorService {
    private final IInteriorMapper interiorMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // API 이미지 생성 요청
    @Override
    public String generateImg(String imageUrl, String prompt) throws Exception {
        log.info("이미지 생성 시작");

        String url = "http://198.19.183.123:8000/myGenerateImageAPI";
        // 198.19.183.123
        // 127.0.0.1

        // Map을 사용하여 데이터를 저장 (이미지 경로와 DetailDTO 리스트)
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("prompt", prompt); // resp는 이미 List<DetailDTO>로 되어 있음

        // Gson을 사용하여 Map을 JSON 문자열로 변환
        Gson gson = new Gson();
        String json = gson.toJson(data);
        String resp = "";

        // POST 요청을 위한 URL 연결 생성
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST"); // POST 요청 설정
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // 요청 본문에 JSON 데이터 전송
        log.info("이미지 생성 요청 전달");

        con.setDoOutput(true); // 출력 스트림 사용 가능 설정
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // 응답 코드 확인
        log.info("응답확인");
        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        // 응답 데이터 처리 (응답이 필요하면 여기서 처리)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 응답을 읽고 RecommendDTO 리스트로 변환하는 로직 추가
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                resp = response.toString();
            }

        } else {
            log.error("응답 오류: " + responseCode);
        }

        return resp;
    }

    @Override
    public List<DetailDTO> runImgAnalysisPython(String imageUrl) {
        log.info("이미지 분석 시작");

        String url = "http://198.19.183.123:8000/myImageAnalysisAPI";
        String pname = "image_url";
        String ptext = imageUrl;

        List<DetailDTO> detailList;

        try {
            // POST 요청을 위한 URL 연결 생성
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST"); // POST 요청 설정
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            // 전송할 데이터를 URL 인코딩
            String postParams = pname + "=" + URLEncoder.encode(ptext, "UTF-8");

            // 요청 본문에 데이터 전송
            log.info("분석 요청 전달");

            con.setDoOutput(true); // 출력 스트림 사용 가능 설정
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = postParams.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            // 응답 데이터 처리
            log.info("응답 확인");

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    // 응답 내용을 라인별로 읽음
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // 응답 내용을 JSON으로 파싱하여 List<DetailDTO>로 변환
                    String jsonResponse = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<DetailDTO>>() {
                    }.getType();
                    detailList = gson.fromJson(jsonResponse, listType);
                }
            } else {
                System.out.println("POST 요청 실패");
                detailList = Collections.emptyList(); // 오류 시 빈 리스트 반환
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            detailList = Collections.emptyList(); // 오류 시 빈 리스트 반환
        }

        return detailList;
    }

    @Override
    public GRecordDTO getGenerateSeq(GRecordDTO pDTO) throws Exception {
        log.info("{}.getGenerateSeq Start", this.getClass().getName());
        log.info("generatedImgName : {}", pDTO.getGeneratedImgName());
        GRecordDTO rDTO = interiorMapper.getGenerateSeq(pDTO);
        log.info("getGenerateSeq : {}", rDTO.getGenerateSeq());
        log.info("{}.getGenerateSeq End", this.getClass().getName());
        return rDTO;
    }

    @Transactional
    @Override
    public int insertDetail(DetailDTO detailDTO) throws Exception {
        log.info("{}.insertDetail Start", this.getClass().getName());

        int res;

        int success = interiorMapper.insertDetail(detailDTO);

        if (success>0) {
            res = 1;
        } else {
            res = 0;
        }

        log.info("{}.insertDetail End", this.getClass().getName());

        return res;
    }

    @Override
    public List<DetailDTO> getDetail(DetailDTO paramDTO) throws Exception {
        List<DetailDTO> rList = interiorMapper.getDetail(paramDTO);
        return rList;
    }

    @Transactional
    @Override
    public int deleteRecord(GRecordDTO pDTO) throws Exception {
        log.info("삭제 시작");
        int res;

        int success = interiorMapper.deleteRecord(pDTO);

        if (success > 0) {
            res = 1;
        }
        else {
            res = 0;
        }

        return res;
    }

    @Override
    public List<RecommendDTO> getRecommend(String imageUrl, List<DetailDTO> resp) throws Exception {
        log.info("제품 추천 시작");
        // 요청을 보낼 Python 서버의 URL
        String url = "http://198.19.183.123:8000/myRecommendAPI";

        // Map을 사용하여 데이터를 저장 (이미지 경로와 DetailDTO 리스트)
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("detailList", resp); // resp는 이미 List<DetailDTO>로 되어 있음

        // Gson을 사용하여 Map을 JSON 문자열로 변환
        Gson gson = new Gson();
        String json = gson.toJson(data);

        // POST 요청을 위한 URL 연결 생성
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST"); // POST 요청 설정
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // 요청 본문에 JSON 데이터 전송
        log.info("분석 요청 전달");

        con.setDoOutput(true); // 출력 스트림 사용 가능 설정
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // 응답 코드 확인
        log.info("응답확인");
        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        List<RecommendDTO> recommendList;

        // 응답 데이터 처리 (응답이 필요하면 여기서 처리)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 응답을 읽고 RecommendDTO 리스트로 변환하는 로직 추가
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // 응답 문자열을 List<RecommendDTO>로 변환
                Type listType = new TypeToken<List<RecommendDTO>>() {}.getType();
                recommendList = gson.fromJson(response.toString(), listType);
            }
        } else {
            log.error("응답 오류: " + responseCode);
            recommendList = Collections.emptyList(); // 빈 리스트 반환
        }

        return recommendList;
    }
}
