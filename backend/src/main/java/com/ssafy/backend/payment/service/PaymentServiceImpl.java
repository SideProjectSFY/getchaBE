package com.ssafy.backend.payment.service;

import com.ssafy.backend.common.enums.PaymentStatus;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.payment.infra.PortOneClient;
import com.ssafy.backend.payment.model.Payment;
import com.ssafy.backend.payment.model.PaymentMapper;
import com.ssafy.backend.payment.model.PaymentRequestDto;
import com.ssafy.backend.payment.model.PaymentResponseDto;
import com.ssafy.backend.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PortOneClient portOneClient;
    private final WalletService walletService;

    public PaymentServiceImpl(PaymentMapper paymentMapper, PortOneClient portOneClient, WalletService walletService) {
        this.paymentMapper = paymentMapper;
        this.portOneClient = portOneClient;
        this.walletService = walletService;
    }


    // 결제 준비 Ready
    @Override
    @Transactional
    public PaymentResponseDto.Prepare prepareWalletCharge(Long loginUserId, PaymentRequestDto.Prepare request) {

        // 1. amount 검증
        if(request == null || request.getAmount() == null) {
            throw new CustomException("충전 금액은 필수 값입니다.", HttpStatus.BAD_REQUEST);
        }

        int amount = request.getAmount();

        if(amount <= 0) {
            throw new CustomException("유효한 금액을 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        // 10,000 단위로만 결제 가능
        if(amount % 10000 != 0) {
            throw new CustomException("10,000 골드 단위로만 충전 가능합니다.", HttpStatus.BAD_REQUEST);
        }

        // merchantUid 생성 (40자 이내 !)
        String merchantUid = "pay_" + UUID.randomUUID();

        // paymentStatus Ready 저장
        Payment payment = Payment.builder()
                .userId(loginUserId)
                .merchantUid(merchantUid)
                .amount(amount)
                .status(PaymentStatus.READY)
                .build();

        paymentMapper.insertReady(payment);

        // 결제 금액 사전 등록
        portOneClient.preparePaymentAmount(merchantUid, amount);

        //프론트로 전달
        return PaymentResponseDto.Prepare.builder()
                .merchantUid(merchantUid)
                .amount(amount)
                .build();
    }

    // 결제 완료
    @Override
    @Transactional
    public PaymentResponseDto.Complete completeWalletCharge(Long loginUserId, PaymentRequestDto.Complete request) {

        // 요청 값 검증
        if(request == null || request.getMerchantUid() == null || request.getImpUid() == null) {
            throw new CustomException("merchantUid, impUid 값은 필수입니다.", HttpStatus.BAD_REQUEST);
        }

        String merchantUid = request.getMerchantUid();
        String impUid = request.getImpUid();

        // DB에서 payment READY 확인
        Payment payment = paymentMapper.findByMerchantUid(merchantUid);
        if(payment == null) {
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("결제 준비 건이 없습니다.")
                    .build();
        }

        // 로그인한 유저와 결제 유저 일치 여부 확인
        if(!payment.getUserId().equals(loginUserId)) {
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("결제 사용자 정보가 일치하지 않습니다.")
                    .build();
        }

        // 이미 PAID 처리 된 건인지 확인 (중복 여부)
        if(payment.getStatus() == PaymentStatus.PAID) {
            return PaymentResponseDto.Complete.builder()
                    .status("PAID")
                    .merchantUid(merchantUid)
                    .amount(payment.getAmount())
                    .failReason("이미 처리 된 결제입니다.")
                    .build();
        }

        // portOne 실제 결제 정보 가져오기
        Map<String, Object> raw = portOneClient.getPaymentByImpUid(impUid);
        if(raw == null) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("PortOne 응답이 비어있습니다.")
                    .build();
        }

        Number codeNum = (Number) raw.get("code");
        int code = codeNum == null ? -1 : codeNum.intValue();
        if(code != 0) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("PortOne 결제 조회 실패(code=" + code + ")")
                    .build();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) raw.get("response");
        if(res == null) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("PortOne 응답에 결제 정보가 없습니다.")
                    .build();
        }

        // portOne 응답에서 merchant_uid, amount, status 추출
        String portOneMerchantUid = (String) res.get("merchant_uid");

        Number portOneAmountNum = (Number) res.get("amount");
        if(portOneAmountNum == null) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("PortOne 금액 정보가 없습니다.")
                    .build();
        }
        int portOneAmount = portOneAmountNum.intValue();

        String portOneStatus = (String) res.get("status");
        if(portOneStatus == null) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("PortOne 결제 상태가 없습니다.")
                    .build();
        }

        if(!merchantUid.equals(portOneMerchantUid)) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("merchant_uid 값이 불일치 합니다.")
                    .build();
        }

        // 결제 금액 검증
        if(payment.getAmount() != portOneAmount) {
            paymentMapper.updateFailed(merchantUid, impUid);
            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .failReason("결제 금액 불일치로 결제 실패되었습니다.")
                    .build();
        }

        // portOne 에서 내려주는 상태값
        if("paid".equalsIgnoreCase(portOneStatus)) {
            int updated = paymentMapper.updatePaid(merchantUid, impUid, portOneAmount);
            if(updated != 1) {
                return PaymentResponseDto.Complete.builder()
                        .status("FAILED")
                        .merchantUid(merchantUid)
                        .amount(portOneAmount)
                        .failReason("결제 상태 갱신에 실패했습니다.")
                        .build();
            }

            // wallet 잔액 증가 + wallet_history 기록
            int walletBalance = walletService.chargeByPayment(loginUserId, portOneAmount, merchantUid);

            return PaymentResponseDto.Complete.builder()
                    .status("PAID")
                    .merchantUid(merchantUid)
                    .amount(portOneAmount)
                    .walletBalance(walletBalance)
                    .build();
        }else {
            // 실패는 wallet_history 에 기록 X
            // payment에만 기록
            paymentMapper.updateFailed(merchantUid, impUid);

            return PaymentResponseDto.Complete.builder()
                    .status("FAILED")
                    .merchantUid(merchantUid)
                    .amount(portOneAmount)
                    .failReason("결제가 실패되었습니다.")
                    .build();
        }
    }

    // 웹훅 처리
    @Override
    @Transactional
    public void handlePortOneWebhook(PaymentRequestDto.Webhook webhook) {

        // null 일 경우
        if(webhook == null || webhook.getMerchantUid() == null || webhook.getImpUid() == null) {

            log.warn("[Webhook] invalid payload: {}", webhook);
            return;
        }

        String merchantUid = webhook.getMerchantUid();
        String impUid = webhook.getImpUid();

        // merchantUid 기준으로 payment 조회
        Payment payment = paymentMapper.findByMerchantUid(merchantUid);
        if(payment == null) {
            log.warn("[Webhook] payment not found - merchantUid={}",  merchantUid);
            return;
        }

        // PAID 일 경우
        if(payment.getStatus() == PaymentStatus.PAID) {
            log.info("[Webhook] already paid - merchantUid={}", merchantUid);
            return;
        }

        log.info("[Webhook] process start - merchantUid={}", merchantUid);

        // portOne 결제 조회로 검증 후 확정
        PaymentRequestDto.Complete complete = PaymentRequestDto.Complete.builder()
                .merchantUid(merchantUid)
                .impUid(impUid)
                .build();

        //실제 처리
        completeWalletCharge(payment.getUserId(), complete);
        log.info("[Webhook] process end - merchantUid={}", merchantUid);
    }
}
