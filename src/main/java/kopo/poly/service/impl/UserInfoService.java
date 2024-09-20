package kopo.poly.service.impl;

import kopo.poly.dto.MailDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.mapper.IUserInfoMapper;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {
    private final IUserInfoMapper userInfoMapper;
    private final IMailService mailService;

    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info("{}.getUserIdExists", this.getClass().getName());

        UserInfoDTO rDTO = userInfoMapper.getUserIdExists(pDTO);

        log.info("{}.getUserIdExists End", this.getClass().getName());
        return rDTO;
    }

    @Override
    public UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception {

        log.info("{}.getUserEmailExists Start!", this.getClass().getName());

        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getUserEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("rDTO : {}", rDTO);


        int authNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

        log.info("authNumber : {}", authNumber);

        MailDTO dto = new MailDTO();

        dto.setTitle("이메일 확인 인증번호 발송 메일");
        dto.setContents("인증번호는 " + authNumber + " 입니다.");
        dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getUserEmail())));

        mailService.doSendMail(dto);

        dto=null;

        rDTO.setAuthNumber(authNumber);

        log.info("{}.getUserEmailExists End", this.getClass().getName());
        return rDTO;
    }

    @Transactional
    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info("{}.insertUserInfo", this.getClass().getName());

        int res;

        int success = userInfoMapper.insertUserInfo(pDTO);

        if (success > 0) {
            res = 1;
            MailDTO mDTO = new MailDTO();

            mDTO.setToMail(EncryptUtil.decAES128CBC(pDTO.getUserEmail()));
            mDTO.setTitle("회원가입을 축하드립니다.");

            mDTO.setContents(CmmUtil.nvl(pDTO.getUserName())+"님의 회원가입을 진심으로 축하드립니다.");

            mailService.doSendMail(mDTO);

        } else {
            res = 0;
        }

        log.info("{}.insertUserInfo End", this.getClass().getName());

        return res;
    }

    @Override
    public UserInfoDTO getUserNicknameExists(UserInfoDTO pDTO) throws Exception {
        log.info("{}.getUserEmailExists Start!", this.getClass().getName());

        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getUserNicknameExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("rDTO : {}", rDTO);

        return rDTO;
    }

    @Override
    public UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception {
        log.info("{}.getLogin Start!", this.getClass().getName());
        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getLogin(pDTO)).orElseGet(UserInfoDTO::new);
        return rDTO;
    }

    @Transactional
    @Override
    public int updateUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info("{}.updateUserInfo", this.getClass().getName());

        int res;

        int success = userInfoMapper.updateUserInfo(pDTO);

        if (success > 0) {
            res = 1;
        } else {
            res = 0;
        }
        log.info("{}.updateUserInfo End", this.getClass().getName());

        return res;
    }
}
