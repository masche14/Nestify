package kopo.poly;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kopo.poly.dto.DetailDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        String url = "http://127.0.0.1:8000/myImageAnalysisAPI";
        String pname = "image_path";
        String ptext = "C:/KPaaS/src/main/resources/static/generatedImages/ai_sample03_20241016004546.png";

        // POST 요청을 위한 URL 연결 생성
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST"); // POST 요청 설정
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // 전송할 데이터를 URL 인코딩
        String postParams = pname + "=" + URLEncoder.encode(ptext, "UTF-8");

        // 요청 본문에 데이터 전송
        con.setDoOutput(true); // 출력 스트림 사용 가능 설정
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = postParams.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // 응답 코드 확인
        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        // 응답 데이터 처리
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
                Type listType = new TypeToken<List<DetailDTO>>() {}.getType();
                List<DetailDTO> detailList = gson.fromJson(jsonResponse, listType);

                // 디코딩된 응답 결과 출력 (한글 포함)
                for (DetailDTO detail : detailList) {
                    System.out.println("카테고리: " + detail.getCategory());
                    System.out.println("색상: " + detail.getColor());
                    System.out.println("특징: " + detail.getFeatures());
                    System.out.println("상품명: " + detail.getProductName());
                }
            }
        } else {
            System.out.println("POST 요청 실패");
        }
    }
}
