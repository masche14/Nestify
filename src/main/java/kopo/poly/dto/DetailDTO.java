package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DetailDTO {
    private String userId;
    private int generateSeq;
    private int detailSeq;
    private String category;    // 카테고리
    private String productName; // 제품명
    private String color;       // 색상
    private String features;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;
}
