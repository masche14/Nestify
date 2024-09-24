package kopo.poly.mapper;

import kopo.poly.dto.MailDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMailMapper {
    int insertMail(MailDTO pDTO) throws Exception;
    List<MailDTO> getMailList()throws Exception;

}
