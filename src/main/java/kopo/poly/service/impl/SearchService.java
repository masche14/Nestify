package kopo.poly.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService implements ISearchService {
    @Override
    public List<RecommendDTO> searchResult(String query) throws Exception {

        log.info("제품 검색 시작");
        // 요청을 보낼 Python 서버의 URL
        String url = "http://127.0.0.1:8000/mySearchAPI";
        String pname = "query";
        String ptext = query;

        List<RecommendDTO> rList;

        try {
            // POST 요청을 위한 URL 연결 생성
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST"); // POST 요청 설정
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            // 전송할 데이터를 URL 인코딩
            String postParams = pname + "=" + URLEncoder.encode(ptext, "UTF-8");

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
                    Type listType = new TypeToken<List<RecommendDTO>>() {
                    }.getType();
                    rList = gson.fromJson(jsonResponse, listType);
                }
            } else {
                System.out.println("POST 요청 실패");
                rList = Collections.emptyList(); // 오류 시 빈 리스트 반환
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            rList = Collections.emptyList(); // 오류 시 빈 리스트 반환
        }

        return rList;
    }
}
