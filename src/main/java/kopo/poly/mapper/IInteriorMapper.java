package kopo.poly.mapper;

import kopo.poly.dto.GRecordDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IInteriorMapper {
    int insertRecord(GRecordDTO pDTO) throws Exception;
}
