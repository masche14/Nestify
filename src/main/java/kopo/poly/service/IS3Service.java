package kopo.poly.service;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {
    String uploadUserImageToS3(MultipartFile file, String userId) throws Exception;

    // API에서 응답받은 이미지의 URL을 S3 임시 폴더에 업로드
    String uploadApiResponseImageToS3(String imageUrl, String userId) throws Exception;

    // 임시 폴더에 있는 이미지를 생성된 이미지 폴더로 이동
    String moveImageToGeneratedFolder(String fileName, String userId) throws Exception;

    // 임시 폴더에 있는 이미지를 사용자 이미지 폴더로 이동
    String moveImageToUserImagesFolder(String fileName, String userId) throws Exception;

    // S3의 임시 폴더에 있는 모든 이미지 삭제
    void deleteTemporaryImages() throws Exception;
}
