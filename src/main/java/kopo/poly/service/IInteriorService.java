package kopo.poly.service;

import kopo.poly.dto.GRecordDTO;

import java.io.IOException;

public interface IInteriorService {
    int insertRecord(GRecordDTO pDTO) throws Exception;
    String saveImageToServer(String imageUrl) throws IOException;
}
