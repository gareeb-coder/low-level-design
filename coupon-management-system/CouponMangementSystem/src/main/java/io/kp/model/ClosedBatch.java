package io.kp.model;

import io.kp.exceptions.BatchExhaustedException;
import io.kp.exceptions.BatchNotActiveException;
import io.kp.exceptions.InvalidTransitionException;
import io.kp.repository.CouponRepository;
import io.kp.util.Transitions;
import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Batch class representing a particular batch.
 */
@Getter
public class ClosedBatch implements Batch {

    private final String batchId;
    private BatchState batchState;
    private Period validityPeriod;
    private String distributor;
    protected CouponType couponType;


    protected Set<String> availableCouponCodes;
    protected Set<String> grantedCouponCodes;

    public ClosedBatch(String id, Period validityPeriod, CouponType couponType, String distributor) {
        this.batchId = id;
        this.batchState = BatchState.CREATED;
        this.couponType = couponType;
        this.distributor = distributor;
        this.validityPeriod = validityPeriod;
        availableCouponCodes = new HashSet<>();
        grantedCouponCodes = new HashSet<>();
    }

    public void updateState(BatchState state) throws InvalidTransitionException {
        if (isTransitionAllowed(this.batchState, state)) {
            this.batchState = state;
            return;
        }
        throw new InvalidTransitionException();
    }

    private boolean isTransitionAllowed(BatchState initial, BatchState finalState) {
        return Transitions.transitionMap.containsKey(initial) &&
                Transitions.transitionMap.get(initial).contains(finalState);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Batch ID: " + this.batchId + "\n")
                .append("Current State: " + this.batchState + "\n")
                .append("Expires on: " + this.validityPeriod.getEndDate().toString() + "\n")
                .append("Available coupons: " + this.availableCouponCodes.size() + "\n");
        return sb.toString();
    }

    @Override
    public void addCoupons(Set<String> couponIds) {
        this.availableCouponCodes.addAll(couponIds);
    }

    synchronized public Coupon grantCoupon() throws BatchNotActiveException, BatchExhaustedException {
        // check if batch is active
        if (this.batchState != BatchState.ACTIVE) {
            throw new BatchNotActiveException();
        }
        // check if any coupons are available to grant
        if (isCouponAvailable()) {
            // Create a new coupon and assign to the userid
            Coupon coupon = createNewCoupon();
            grantedCouponCodes.add(coupon.getCode());
            availableCouponCodes.remove(coupon.getCode());
            CouponRepository.grantedCoupons.put(coupon.getCode(), coupon);
            return coupon;
        }

        throw new BatchExhaustedException();
    }

    protected Coupon createNewCoupon() {
        // Get the first coupon id
        String id = availableCouponCodes.iterator().next();
        Coupon coupon = new Coupon(id, CouponStatus.GRANTED);
        return coupon;
    }

    protected boolean isCouponAvailable() {
        return availableCouponCodes.size() > 0;
    }

    public int getAvailableCouponCounts() {
        return availableCouponCodes.size();
    }

    public boolean isValid() {
        Instant now = Instant.now();
        return this.validityPeriod.getStartDate().toInstant().isBefore(now)
                && this.validityPeriod.getEndDate().toInstant().isAfter(now) ;
    }
}
