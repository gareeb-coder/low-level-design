package io.kp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Coupon {

    private String code;
    private CouponStatus couponStatus;

}
