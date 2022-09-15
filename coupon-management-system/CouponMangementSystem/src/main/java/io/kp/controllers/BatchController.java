package io.kp.controllers;

import io.kp.exceptions.BatchExhaustedException;
import io.kp.exceptions.BatchNotActiveException;
import io.kp.exceptions.BatchNotFoundException;
import io.kp.exceptions.CouponNotFoundException;
import io.kp.exceptions.InvalidRequest;
import io.kp.exceptions.InvalidTransitionException;
import io.kp.model.Batch;
import io.kp.model.BatchState;
import io.kp.model.Coupon;
import io.kp.repository.BatchRepository;
import io.kp.repository.CouponRepository;

import java.util.Set;

/**
 * The API class that provides various APIs for Coupon Management system
 */
public class BatchController {

    public void createBatch(Batch batch, int maxAllowedGrants) throws InvalidRequest {
        if (BatchRepository.batches.containsKey(batch.getBatchId())) {
            throw new InvalidRequest();
        }
        if (isValidBatch(batch)) {
            BatchRepository.batches.put(batch.getBatchId(), batch);
            return;
        }
        throw new InvalidRequest();
    }

    public void updateState(String batchId, BatchState state)
            throws InvalidTransitionException, BatchNotFoundException {
        if (!BatchRepository.batches.containsKey(batchId)) {
            throw new BatchNotFoundException();
        }
        Batch batch = BatchRepository.batches.get(batchId);
        batch.updateState(state);
    }

    public Batch getBatch(String batchId) throws BatchNotFoundException {
        if (!BatchRepository.batches.containsKey(batchId)) {
            throw new BatchNotFoundException();
        }
        Batch batch = BatchRepository.batches.get(batchId);
        return batch;
    }

    public void ingestCoupons(String batchId, Set<String> couponCodes) throws BatchNotFoundException {
        if (!BatchRepository.batches.containsKey(batchId)) {
            throw new BatchNotFoundException();
        }
        Batch batch = BatchRepository.batches.get(batchId);
        batch.addCoupons(couponCodes);
    }

    public Coupon grantCoupon(String batchId) throws BatchNotFoundException, BatchNotActiveException, BatchExhaustedException {
        if (!BatchRepository.batches.containsKey(batchId)) {
            throw new BatchNotFoundException();
        }
        Batch batch = BatchRepository.batches.get(batchId);
        return batch.grantCoupon();
    }

    public Coupon getCoupon(String couponId) throws CouponNotFoundException {
        if (!CouponRepository.grantedCoupons.containsKey(couponId)) {
            throw new CouponNotFoundException();
        }
        return CouponRepository.grantedCoupons.get(couponId);
    }

    public int getCouponsCount(String batchId) throws BatchNotFoundException {
        if (!BatchRepository.batches.containsKey(batchId)) {
            throw new BatchNotFoundException();
        }
        Batch batch = BatchRepository.batches.get(batchId);
        return batch.getAvailableCouponCounts();
    }

    private boolean isValidBatch(Batch batch) {
        if (BatchRepository.batches.containsKey(batch.getBatchId())) {
            return false;
        } else return !batch.getValidityPeriod().getStartDate().after(batch.getValidityPeriod().getEndDate());
    }

}
