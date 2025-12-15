package com.ssafy.backend.payment.infra;

import com.ssafy.backend.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
public class PortOneAuthTokenProvider {

    //포트원 REST API 키
    @Value("${portone.imp-key}")
    private String impKey;

    //포트원 REST API 시크릿 키
    @Value("${portone.imp-secret")
    private String impSecret;

    //토큰 캐시 값
    private String cachedToken;

    //대략적인 토큰 만료 시각
    private Instant cachedTokenExpireAt;

    private final RestTemplate restTemplate = new RestTemplate();


    //유효한 토큰 반환
    public synchronized String getAccessToken() {

        // 1) 이전에 발급 받은 토큰이 있고, 아직 만료전이면? 그대로 사용
                                                                 // 현재시간이 만료시간보다 이전이면
        if(cachedToken == null && cachedTokenExpireAt != null && Instant.now().isBefore(cachedTokenExpireAt)) {
            return cachedToken;
        }

        // 2) 토큰 발급 API 호출
        String url = "https://api.iamport.kr/users/getToken";

        // 3) 요청 body
        Map<String, String> body = Map.of(
                "imp_key", impKey,
                "imp_secret", impSecret
        );

        // 4) 응답을 Map 으로 파싱
        /*
        응답 구조 !
        {
            "code": 0, (0이 정상)
            "message": null,
            "response": {
                "access_token": "xxxx",
                "now" : 1234567890
                "expired_at": 1234567890
            }
        }
         */

        @SuppressWarnings("unchecked")
        //최상위 응답
        Map<String, Object> top = restTemplate.postForObject(url, body, Map.class);

        if(top == null || top.get("response") == null) {
            throw new CustomException("PortOne 토큰 발급 응답 값이 null 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) top.get("response");

        String token = (String) res.get("access_token");
        Object expiredAtObj = res.get("expired_at");

        if(token == null || expiredAtObj == null) {
            throw new CustomException("PortOne 토큰 정보(access_token, expired_at) 값이 null 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long expiredAtSec;
        if(expiredAtObj instanceof Number n) {
            expiredAtSec = n.longValue();
        } else {
            expiredAtSec = Long.parseLong(expiredAtObj.toString());
        }

        //만료일자, 60초 일찍 만료
        Instant expireAt = Instant.ofEpochSecond(expiredAtSec).minusSeconds(60);

        cachedToken = token;
        cachedTokenExpireAt = expireAt;

        return cachedToken;
    }
}
