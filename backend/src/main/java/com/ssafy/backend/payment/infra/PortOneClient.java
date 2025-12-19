package com.ssafy.backend.payment.infra;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


//PortOne V1 API 호출 !!
@Component
public class PortOneClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final PortOneAuthTokenProvider tokenProvider;

    public PortOneClient(PortOneAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // 결제 단 건 조회
    // GET /payments/{imp_uid}
    // 헤더에 Authorization : access_token
    public Map<String, Object> getPaymentByImpUid(String impUid) {

        // 1) 토큰 가져오기
        String token = tokenProvider.getAccessToken();

        // 2) 요청 url
        String url = "https://api.iamport.kr/payments/" + impUid;

        // 3) 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // 4) GET 요청
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // 5) 응답 반환
        return response.getBody();
    }

    // 결제 금액 사전 등록
    // POST /payments/prepare
    // 프론트에서 결제 금액 조작하는 것을 막기 위해서 !!
    public void preparePaymentAmount(String merchantUid, int amount) {

        // 1) 토큰
        String token = tokenProvider.getAccessToken();

        // 2) 요청 url
        String url = "https://api.iamport.kr/payments/prepare";

        // 3) 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        // 4) 바디
        Map<String, Object> body = Map.of(
                "merchant_uid", merchantUid,
                "amount", amount
        );

        // 5) POST 요청
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
    }
}
