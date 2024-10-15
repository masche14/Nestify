package kopo.poly.mapper;

import kopo.poly.dto.DetailDTO;
import kopo.poly.dto.GRecordDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IInteriorMapper {
    int insertRecord(GRecordDTO pDTO) throws Exception;

    List<GRecordDTO> getRecords(GRecordDTO pDTO) throws Exception;

    GRecordDTO getGenerateSeq(GRecordDTO pDTO) throws Exception;

    int insertDetail(DetailDTO detailDTO) throws Exception;

    List<DetailDTO> getDetail(DetailDTO paramDTO) throws Exception;

    int deleteRecord(GRecordDTO pDTO) throws Exception;
}
