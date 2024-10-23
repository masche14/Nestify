package kopo.poly.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import kopo.poly.service.IS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service implements IS3Service {

    private final AmazonS3Client s3Client;

    // S3 버킷 이름을 application.properties에서 가져옴
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 임시 폴더 경로 (S3 내 폴더처럼 경로를 사용)
    @Value("${s3.folder.temporary}")
    private String tempFolder;

    // 사용자 이미지 저장 폴더 경로
    @Value("${s3.folder.user-images}")
    private String userImagesFolder;

    // 생성된 이미지 저장 폴더 경로
    @Value("${s3.folder.generated-images}")
    private String generatedImagesFolder;

    // 사용자로부터 받은 이미지를 S3의 임시 폴더에 업로드
    @Override
    public String uploadUserImageToS3(MultipartFile file, String userId) throws Exception {
        log.info("uploadUserImageToS3 Start");

        String fileName = generateFileName(userId); // 파일명 생성
        String s3Key = tempFolder + fileName; // S3에 저장될 키 (경로)

        // 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType()); // 파일의 컨텐츠 타입 설정 (예: image/png)

        // S3에 파일 업로드
        PutObjectRequest request = new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata);
        s3Client.putObject(request);

        // 업로드된 파일의 URL 가져오기
        String userImgUrl = s3Client.getUrl(bucketName, s3Key).toString();

        log.info("userImgUrl: {}", userImgUrl);

        return userImgUrl; // 업로드된 파일의 URL 반환
    }

    // API에서 응답받은 이미지의 URL을 S3 임시 폴더에 업로드
    @Override
    public String uploadApiResponseImageToS3(String imageUrl, String userId) throws Exception {
        String fileName = "ai_"+generateFileName(userId); // 파일명 생성
        String s3Key = generatedImagesFolder + fileName; // S3에 저장될 키 (경로)

        // URL로부터 이미지를 S3에 업로드
        uploadImageFromUrlToS3(imageUrl, s3Key);

        String generatedImageUrl = s3Client.getUrl(bucketName, s3Key).toString();

        log.info("Generated image url: " + generatedImageUrl);

        return generatedImageUrl; // 업로드된 파일의 URL 반환
    }

    // 임시 폴더에 있는 이미지를 사용자 이미지 폴더로 이동
    @Override
    public String moveImageToUserImagesFolder(String fileName, String userId) throws Exception {
        String sourceKey = tempFolder + fileName; // 소스 파일의 S3 경로
        String destKey = userImagesFolder + fileName; // 이동할 대상 경로

        // S3 객체를 사용자 이미지 폴더로 이동
        moveS3Object(sourceKey, destKey);
        return s3Client.getUrl(bucketName, destKey).toString(); // 이동된 이미지의 URL 반환
    }

    // S3의 임시 폴더에 있는 모든 이미지 삭제
    @Override
    public void deleteTemporaryImages() throws Exception {
        deleteFolderContents(tempFolder); // 임시 폴더 내 파일 삭제
    }

    // 파일명을 생성하는 헬퍼 메서드 (유저ID와 현재 타임스탬프를 포함)
    private String generateFileName(String userId) {
        LocalDate localdate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = localdate.format(dateFormatter);

        LocalTime localtime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String time = localtime.format(timeFormatter);


        return userId+"_"+date+time+".png";
    }

    // URL로부터 이미지를 다운로드하여 S3에 업로드
    private void uploadImageFromUrlToS3(String imageUrl, String s3Key) throws Exception {
        InputStream in = new URL(imageUrl).openStream(); // URL로부터 InputStream 열기
        s3Client.putObject(new PutObjectRequest(bucketName, s3Key, in, null)); // S3에 업로드
        in.close(); // InputStream 닫기
    }

    // S3 내 객체 이동 (소스에서 대상 경로로 복사 후 소스 객체 삭제)
    private void moveS3Object(String sourceKey, String destKey) {
        s3Client.copyObject(bucketName, sourceKey, bucketName, destKey); // S3 객체 복사
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, sourceKey)); // 원본 삭제
    }

    // S3 폴더 내 모든 객체 삭제 (임시 폴더 비우기)
    private void deleteFolderContents(String folderKey) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(folderKey);
        ListObjectsV2Result objects = s3Client.listObjectsV2(listObjectsRequest); // 폴더 내 모든 객체 조회

        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
            s3Client.deleteObject(bucketName, objectSummary.getKey()); // 각 객체 삭제
        }
    }
}
