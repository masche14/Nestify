package kopo.poly.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getUserNicknameExists(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getUserEmailExists(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    int updateUserInfo(UserInfoDTO pDTO) throws Exception;

    int deleteUserInfo(UserInfoDTO pDTO) throws Exception;
}
