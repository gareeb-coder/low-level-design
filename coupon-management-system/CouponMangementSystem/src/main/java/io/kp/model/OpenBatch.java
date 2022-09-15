package io.kp.model;

import io.kp.exceptions.BatchExhaustedException;
import io.kp.exceptions.BatchNotActiveException;
import io.kp.repository.CouponRepository;
import lombok.Getter;

@Getter
public class OpenBatch extends ClosedBatch {

    private final int maxAllowedGrants;
    private int grantedCoupons = 0;

    public OpenBatch(String id, Period validityPeriod, CouponType couponType, String distributor,
                     int maxAllowedGrants) {
        super(id, validityPeriod, couponType, distributor);
        this.maxAllowedGrants = maxAllowedGrants;
    }


    @Override
    public synchronized Coupon grantCoupon() throws BatchNotActiveException, BatchExhaustedException {

        if (this.getBatchState() != BatchState.ACTIVE) {
            throw new BatchNotActiveException();
        }
        // The case where coupon type is open
        if (grantedCoupons >= maxAllowedGrants) {
            throw new BatchExhaustedException();
        }
        Coupon coupon = createNewCoupon();
        CouponRepository.grantedCoupons.put(coupon.getCode(), coupon);
        grantedCoupons += 1;
        return coupon;
    }

    @Override
    public int getAvailableCouponCounts() {
        return maxAllowedGrants - grantedCoupons;
    }


}
