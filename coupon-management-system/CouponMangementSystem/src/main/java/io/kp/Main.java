package io.kp;

import io.kp.controllers.BatchController;
import io.kp.model.Batch;
import io.kp.model.BatchState;
import io.kp.model.ClosedBatch;
import io.kp.model.CouponType;
import io.kp.model.Period;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {

        BatchController controller = new BatchController();

        Batch batch = new ClosedBatch("123", new Period(new Date(), new Date(System.currentTimeMillis() + 100000)), CouponType.CLOSED, "myDistributor");

        controller.createBatch(batch, -1);

        controller.updateState("123", BatchState.ACTIVE);

        controller.ingestCoupons("123", Set.of("1", "2", "3", "5"));

        controller.grantCoupon("123");
        System.out.println("Available coupons: " + controller.getCouponsCount("123"));

        controller.grantCoupon("123");
        System.out.println("Available coupons: " + controller.getCouponsCount("123"));
        controller.grantCoupon("123");
        System.out.println("Available coupons: " + controller.getCouponsCount("123"));
        controller.grantCoupon("123");
        System.out.println("Available coupons: " + controller.getCouponsCount("123"));
        try {
            controller.grantCoupon("123");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        controller.updateState("123", BatchState.SUSPENDED);
         System.out.println(controller.getBatch("123"));
        try {
            controller.grantCoupon("123");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Coupon 3: " + controller.getCoupon("3"));



    }
}
