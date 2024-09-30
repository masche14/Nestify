package kopo.poly.service;

import kopo.poly.dto.GRecordDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IInteriorService {
    int insertRecord(GRecordDTO pDTO) throws Exception;

    String saveUploadedFile(MultipartFile image) throws IOException;

    String saveImageToServer(String imageUrl) throws IOException;
}
