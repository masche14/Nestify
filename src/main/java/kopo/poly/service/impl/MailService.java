package kopo.poly.service.impl;

import jakarta.mail.internet.MimeMessage;
import kopo.poly.dto.MailDTO;
import kopo.poly.mapper.IMailMapper;
import kopo.poly.service.IMailService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final IMailMapper mailMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {
//
        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info("{}.doSendMail start!", this.getClass().getName());

        // 메일 발송 성공여부 (발송성공 : 1 / 발송실패 : 0)
        int res = 1;

        // 전달 받은 DTO로부터 데이터 가져오기 (DTO 객체가 메모리에 올라가지 않아 Null이 발생할 수 있기 때문에 객체 생성)
        if (pDTO == null) {
            pDTO = new MailDTO();
        }

        String toMail = CmmUtil.nvl(pDTO.getToMail());  // 받는사람
        String title = CmmUtil.nvl(pDTO.getTitle());    // 메일제목
        String contents = CmmUtil.nvl(pDTO.getContents());  // 메일내용

        log.info("toMail : {} / title : {} / contents : {}", toMail, title, contents);

        // 메일 발송 메시지 구조 (파일 첨부 가능)
        MimeMessage message = mailSender.createMimeMessage();

        // 메일 발송 메시지 구조를 쉽게 생성하게 도와주는 객체
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

        try {
            messageHelper.setTo(toMail);           // 받는 사람
            messageHelper.setFrom(fromMail);       // 보내는 사람
            messageHelper.setSubject(title);       // 메일 제목
            messageHelper.setText(contents);       // 메일 내용

            mailSender.send(message);              // 메일 발송

        } catch (Exception e) {                    // 모든 에러 다 잡기
            res = 0;                               // 메일 발송이 실패했기 때문에 0으로 변경
            log.info("[ERROR] doSendMail : {}", e);
        } finally {
            log.info("insertMail Start");
            pDTO.setFromMail(fromMail);
            try{
                mailMapper.insertMail(pDTO);
            } catch (Exception e){
                log.info("[ERROR] insertMail : {}", e);
            } finally {
                log.info("insertMail End");
            }
        }

        // 로그 찍기 (추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info("{}.doSendMail end!", this.getClass().getName());

        return res;

    }

    @Override
    public List<MailDTO> getMailList() throws Exception {
        log.info("{}.getMailList Start", this.getClass().getName());

        return mailMapper.getMailList();
    }
}
