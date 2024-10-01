package kopo.poly.service.impl;

import kopo.poly.dto.GRecordDTO;
import kopo.poly.mapper.IInteriorMapper;
import kopo.poly.service.IInteriorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class InteriorService implements IInteriorService {
    private final IInteriorMapper interiorMapper;

    @Value("${inputImgDir}")
    private String inputImgDir;

    @Value("${generatedImgDir}")
    private String generatedImgDir;

    @Transactional
    @Override
    public int insertRecord(GRecordDTO pDTO) throws Exception {
        log.info("{}.insertRecord Start", this.getClass().getName());

        int res;

        int success = interiorMapper.insertRecord(pDTO);

        if (success>0) {
            res = 1;
        } else {
            res = 0;
        }

        log.info("{}.insertRecord End", this.getClass().getName());

        return res;
    }

    @Override
    public String fileNameEncode(String userId){
        LocalDate localdate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = localdate.format(dateFormatter);
        System.out.println(date);

        LocalTime localtime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String time = localtime.format(timeFormatter);
        System.out.println(time);

        System.out.println(date+time);

        return userId+"_"+date+time;
    }
}
