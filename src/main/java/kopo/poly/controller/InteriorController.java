package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/Interior")
public class InteriorController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/User/index";
    }

    @GetMapping("/makeNew")
    public  String showMakeNewPage(HttpSession session){

        return "/Interior/makeNew";
    }

    @PostMapping("/upload")
    public String procUpload(HttpSession session, HttpServletRequest request) throws Exception {
        log.info("시작");

        InputStream fileContent = request.getPart("image").getInputStream();  // "image"는 파일 input 필드의 name과 일치해야 함
        String fileName = request.getPart("image").getSubmittedFileName();

        if (fileContent != null) {
            // 파일을 저장할 경로 설정
            String uploadDir = "src/main/resources/static/responseImg"; // 서버에서 파일을 저장할 실제 경로

            // 파일 저장
            File dest = new File(uploadDir + File.separator + fileName);
            try (FileOutputStream out = new FileOutputStream(dest)) {
                IOUtils.copy(fileContent, out);  // Apache Commons IO 라이브러리를 사용하여 파일 복사
            }

            // 세션에 파일 이름 저장
            session.setAttribute("uploadedImage", fileName);

            log.info(session.getAttribute("uploadedImage").toString());

            // 성공 후 리다이렉트
            return "redirect:/Interior/makeNew";
        }
        return "redirect:/User/index";
    }
}
