package kopo.poly.service;

import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.GRecordDTO;
import kopo.poly.dto.RecommendDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IInteriorService {
    int insertRecord(GRecordDTO pDTO) throws Exception;

    String generateImg(String imageUrl, String prompt) throws Exception;

    List<GRecordDTO> getRecords(GRecordDTO pDTO) throws Exception;

    List<DetailDTO> runImgAnalysisPython(String imagePath);

    GRecordDTO getGenerateSeq(GRecordDTO pDTO) throws Exception;

    int insertDetail(DetailDTO detailDTO) throws Exception;

    List<DetailDTO> getDetail(DetailDTO paramDTO) throws Exception;

    int deleteRecord(GRecordDTO pDTO) throws Exception;

    List<RecommendDTO> getRecommend(String imageUrl, List<DetailDTO> resp) throws Exception;

}
