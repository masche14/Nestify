package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GRecordDTO {
    private int generateSeq;
    private String userId;
    private String inputImg;
    private String generatedImg;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;
}
