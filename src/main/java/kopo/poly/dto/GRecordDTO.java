package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GRecordDTO {
    private String userId;
    private int generateSeq;
    private String rootPath;
    private String inputImgDir;
    private String inputImgName;
    private String generatedImgDir;
    private String generatedImgName;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;
}
