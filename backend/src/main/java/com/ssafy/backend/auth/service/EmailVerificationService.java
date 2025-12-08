package com.ssafy.backend.auth.service;

import com.ssafy.backend.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, VerificationCode> verificationStore = new ConcurrentHashMap<>();

    // 발신자 이메일 주소
    @Value("${spring.mail.username:no-reply@getcha.com}")
    private String fromAddress;

    // 인증코드 만료 시간
    @Value("${app.auth.email.code-expiration-minutes:5}")
    private long codeExpirationMinutes;

    /**
     * 인증 코드 생성 및 발송
     */
    public void sendVerificationCode(String email) {
        String code = generateCode();
        VerificationCode verificationCode = new VerificationCode(
                code,
                Instant.now().plus(Duration.ofMinutes(codeExpirationMinutes)),
                false
        );
        verificationStore.put(email, verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromAddress);
        message.setSubject("[겟챠] 이메일 인증번호");
        message.setText("""
                안녕하세요, 겟챠 인증번호를 안내드립니다.

                인증번호: %s
                유효시간: %d분

                본인이 요청하지 않았다면 본 메일을 무시해주세요.
                """.formatted(code, codeExpirationMinutes));
        mailSender.send(message);
    }

    /**
     * 인증 코드 일치 여부 체크
     */
    public void verifyCode(String email, String code) {
        VerificationCode target = verificationStore.get(email);
        if (target == null || target.isExpired()) {
            verificationStore.remove(email);
            throw new CustomException("인증번호가 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if (!target.code().equals(code)) {
            throw new CustomException("인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        verificationStore.put(email, target.markVerified());
    }

    /**
     * 인증 상태 확인
     */
    public boolean isVerified(String email) {
        VerificationCode code = verificationStore.get(email);
        return code != null && !code.isExpired() && code.verified();
    }

    /**
     * 인증 정보 제거
     */
    public void consume(String email) {
        verificationStore.remove(email);
    }

    /**
     * 6자리 랜덤 숫자 생성
     */
    private String generateCode() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    // 인증 코드 저장
    private record VerificationCode(String code, Instant expiresAt, boolean verified) {
        /**
         * 만료 시간 체크
         */
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        /**
         * 코드 검증 표시
         */
        VerificationCode markVerified() {
            return new VerificationCode(code, expiresAt, true);
        }
    }
}

