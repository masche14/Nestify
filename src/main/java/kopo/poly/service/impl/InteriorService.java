package kopo.poly.service.impl;

import kopo.poly.dto.GRecordDTO;
import kopo.poly.mapper.IInteriorMapper;
import kopo.poly.service.IInteriorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteriorService implements IInteriorService {
    private final IInteriorMapper interiorMapper;

    @Value("${inputImgDir}")
    private String inputImgDir;

    @Value("${generatedImgDir}")
    private String generatedImgDir;

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

    @Override
    public String saveUploadedFile(MultipartFile image) throws IOException {
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
            throw e;
        }

        return dest.getAbsolutePath();
    }

    @Override
    public String saveImageToServer(String imageUrl) throws IOException {
        log.info(imageUrl);

        File dir = new File(generatedImgDir);

        // URL로부터 InputStream 가져오기
        URL url = new URL(imageUrl);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Path outputPath = null;

        try {
            inputStream = url.openStream(); // 이미지 데이터를 가져옴

            // 이미지 파일명을 지정할 때 원본 URL에서 파일명 추출 가능
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // 저장할 경로와 파일 이름 설정
            outputPath = Paths.get(generatedImgDir + File.separator + fileName);

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

        // 이미지가 저장된 파일 경로를 문자열로 반환
        return outputPath.toString();
    }
}
