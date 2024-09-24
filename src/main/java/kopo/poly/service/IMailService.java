package kopo.poly.service;

import kopo.poly.dto.MailDTO;

import java.util.List;

public interface IMailService {
    //
    int doSendMail(MailDTO pDTO);

    List<MailDTO> getMailList() throws Exception;
}
