package com.ssafy.backend.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileServie {

    /**
     * 실제 파일저장 및 유니크한 파일명 생성
     * @param file 파일 객체
     * @return 저장한 (유니크)파일명 반환
     */
    String saveFile(MultipartFile file);

    /**
     * 파일 삭제
     * @param storedFilename
     */
    void deleteFile(String storedFilename);
}
