package com.ssafy.backend.common.service.impl;

import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.common.service.FileServie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileServie {

    // 실제 서버 저장 상대경로 filePath
    @Value("${file.upload.path}")
    private String filePath;


    @Override
    public String saveFile(MultipartFile file) {

        // 로컬 서버 저장용 파일명 생성
        String originFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID() + "_" + originFilename;

        // 물리 경로에 굿즈이미지를 저장할 폴더가 없다면 만들기
        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdirs();

        File savedFile = new File(dir.getAbsolutePath(), storedFilename);

        try {
            // MultipartFile 의 내용을 지정된 경로로 그대로 복사/저장함
            file.transferTo(savedFile);
            log.info("File saved: {}, size: {}", storedFilename, file.getSize());
        } catch (IOException e) {
            log.error("File upload failed: {}", originFilename, e);
            throw new CustomException("파일 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return storedFilename;
    }

    @Override
    public void deleteFile(String storedFilename) {
        File file = new File(filePath, storedFilename);

        // 존재하지 않는 파일인 경우, 로그만 남기고 메서드 종료
        if (!file.exists()) {
            log.warn("File not found for delete: {}", storedFilename);
            return;
        }

        boolean isDeleted = file.delete();

        // 삭제 실패 시
        if (!isDeleted) {
            throw new CustomException("파일 삭제 실패: " + storedFilename, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 잘 삭제됐다면 로그 남기기
        log.info("Deleted file: {}", storedFilename);
    }
}
