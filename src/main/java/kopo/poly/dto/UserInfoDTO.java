package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserInfoDTO {
    private String userId;
    private String userName;
    private String password;
    private String userEmail;
    private String userNickname;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;
    private String gender;
    private String existsYn;
    private int authNumber;

}
