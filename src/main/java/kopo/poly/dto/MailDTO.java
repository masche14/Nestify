package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MailDTO {
//
    String seq;
    String toMail;
    String title;
    String contents;
    String fromMail;
    String sendDT;
}
