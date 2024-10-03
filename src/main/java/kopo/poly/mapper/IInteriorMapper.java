package kopo.poly.mapper;

import kopo.poly.dto.GRecordDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IInteriorMapper {
    int insertRecord(GRecordDTO pDTO) throws Exception;

    List<GRecordDTO> getRecords(GRecordDTO pDTO) throws Exception;
}
