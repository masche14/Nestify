package kopo.poly.service.impl;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.mapper.IUserInfoMapper;
import kopo.poly.service.IUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {
    private final IUserInfoMapper userInfoMapper;

    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {
        return null;
    }

    @Override
    public UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception {
        return null;
    }

    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {
        return 0;
    }
}
