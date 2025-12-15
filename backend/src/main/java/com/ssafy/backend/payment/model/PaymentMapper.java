package com.ssafy.backend.payment.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    //결제 준비 READY
    int insertReady(Payment payment);

    //merchanUid로 결제 조회
    Payment findByMerchantUid(@Param("merchantUid") String merchantUid);

    //결제 성공 PAID
    int updatePaid(@Param("merchantUid") String merchantUid,
                   @Param("impUid") String impUid,
                   @Param("amount") Integer amount);

    //결제 실패 FAILED
    int updateFailed(@Param("merchantUid") String merchantUid,
                     @Param("impUid") String impUid);
}
