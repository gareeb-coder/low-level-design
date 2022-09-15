package io.kp.model;

import io.kp.exceptions.BatchExhaustedException;
import io.kp.exceptions.BatchNotActiveException;
import io.kp.exceptions.InvalidTransitionException;

import java.util.Set;

public interface Batch {

    Coupon grantCoupon() throws BatchNotActiveException, BatchExhaustedException;
    void addCoupons(Set<String> couponIds);

    int getAvailableCouponCounts();
    String getBatchId();
    void updateState(BatchState state) throws InvalidTransitionException;
    Period getValidityPeriod();
}
