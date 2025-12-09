package com.ssafy.backend.auth.service;

import com.ssafy.backend.auth.model.*;
import com.ssafy.backend.auth.service.jwt.JwtTokenProvider;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.user.model.AnimeSelectionDto;
import com.ssafy.backend.user.model.User;
import com.ssafy.backend.user.model.UserMapper;
import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final EmailVerificationService emailVerificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WalletService walletService;

    /**
     * 인증코드 생성 & 발송
     */
    @Override
    public void sendEmailVerificationCode(EmailSendRequestDto request) {
        if (authMapper.existsByEmail(request.getEmail())) {
            throw new CustomException("이미 가입된 이메일입니다.", HttpStatus.CONFLICT);
        }

        emailVerificationService.sendVerificationCode(request.getEmail());
    }

    /**
     * 인증코드 검증
     */
    @Override
    public void verifyEmailCode(EmailVerifyRequestDto request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
    }

    /**
     * 회원가입
     */
    @Override
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto request) {

        // 1) 이메일 중복 체크
        if (authMapper.existsByEmail(request.getEmail())) {
            throw new CustomException("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);
        }

        // 2) 이메일 인증 여부 체크
        if (!emailVerificationService.isVerified(request.getEmail())) {
            throw new CustomException("이메일 인증을 먼저 완료해주세요.", HttpStatus.FORBIDDEN);
        }

        // 3) 관심 애니 3개 
        List<Long> animeIds = validateAnimeSelection(request.getLikedAnimeIds());
        ensureAnimeExists(animeIds);

        // 4) 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 5) User 객체 생성
        User newUser = User.builder()
                .nickname(request.getNickname())
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .likedAnimeId1(animeIds.get(0))
                .likedAnimeId2(animeIds.get(1))
                .likedAnimeId3(animeIds.get(2))
                .accountNum(request.getAccountNum())
                .accountBank(request.getAccountBank())
                .isAuth(true)
                .createdAt(LocalDateTime.now())
                .build();

        // 6) DB insert
        authMapper.insertUser(newUser);


        // 7) 새로 등록된 유저 ID 조회
        Long userId = authMapper.findIdByEmail(request.getEmail());
        if (userId == null) {
            throw new CustomException("회원가입 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 7-1) 회원가입 시 5만원 코인 충전 (+add!!)
        walletService.createCoinWallet(userId);

        // 8) 관심 애니 정보 조회
        List<AnimeSelectionDto> animes = userMapper.findUserLikedAnimes(userId);

        // 9) email 인증 코드 제거
        emailVerificationService.consume(request.getEmail());

        return UserResponseDto.builder()
                .id(userId)
                .nickname(newUser.getNickname())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .accountNum(newUser.getAccountNum())
                .accountBank(newUser.getAccountBank())
                .likedAnimes(animes)
                .build();
    }

    /**
     * 로그인
     */
    @Override
    public LoginResponseDto login(LoginRequestDto request) {

        // 1) 이메일로 유저 조회
        User user = authMapper.findActiveUserByEmail(request.getEmail());
        if (user == null) {
            throw new CustomException("존재하지 않는 이메일입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 2) 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        Long userId = user.getId();

        // 3) JWT 발급
        String token = jwtTokenProvider.generateToken(userId, user.getEmail());

        // 4) 관심 애니 조회
        List<AnimeSelectionDto> likedAnimes = userMapper.findUserLikedAnimes(userId);

        // 5) 클라이언트로 응답
        return LoginResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresAt(jwtTokenProvider.getExpirationInstant())
                .user(UserResponseDto.builder()
                        .id(userId)
                        .nickname(user.getNickname())
                        .name(user.getName())
                        .email(user.getEmail())
                        .accountNum(user.getAccountNum())
                        .accountBank(user.getAccountBank())
                        .likedAnimes(likedAnimes)
                        .build())
                .build();
    }


    /**
     * 관심 애니 유효성 검사
     */
    private List<Long> validateAnimeSelection(List<Long> ids) {
        if (ids == null || ids.size() != 3) {
            throw new CustomException("관심 애니 3개는 필수입니다.", HttpStatus.BAD_REQUEST);
        }

        Set<Long> set = new HashSet<>(ids);
        if (set.size() != 3) {
            throw new CustomException("중복되지 않은 3개의 애니를 선택해주세요.", HttpStatus.BAD_REQUEST);
        }
        return new ArrayList<>(set);
    }

    /**
     * 관심 애니 존재함 ?
     */
    private void ensureAnimeExists(List<Long> ids) {
        int count = authMapper.countAnimeByIds(ids);
        if (count != ids.size()) {
            throw new CustomException("존재하지 않는 애니메이션 ID가 포함되어 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}

