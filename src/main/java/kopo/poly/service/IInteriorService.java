package kopo.poly.service;

import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.GRecordDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IInteriorService {
    int insertRecord(GRecordDTO pDTO) throws Exception;

    String fileNameEncode(String userId) throws Exception;

    String generateImg(File savedFile, String prompt, String userId) throws Exception;

    String downloadBase64Image(String base64DataUrl, String outputPath);

    void delTempFolder() throws Exception;

    List<GRecordDTO> getRecords(GRecordDTO pDTO) throws Exception;

    List<DetailDTO> runImgAnalysisPython(String imagePath);

}
