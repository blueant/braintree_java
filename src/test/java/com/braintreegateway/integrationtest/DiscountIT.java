package com.braintreegateway.integrationtest;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Discount;
import com.braintreegateway.Environment;
import com.braintreegateway.util.Http;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class DiscountIT {
    private BraintreeGateway gateway;
    private Http http;

    @Before
    public void createGateway() {
        this.gateway = new BraintreeGateway(Environment.DEVELOPMENT, "integration_merchant_id", "integration_public_key", "integration_private_key");
        http = new Http(gateway.getAuthorizationHeader(), gateway.baseMerchantURL(), Environment.DEVELOPMENT.certificateFilenames, BraintreeGateway.VERSION);
    }

    @Test
    public void returnsAllDiscounts() {
        String discountId = "a_discount_id" + String.valueOf(new Random().nextInt());
        FakeModificationRequest discountRequest = new FakeModificationRequest()
                .amount(new BigDecimal("100.00"))
                .description("java test discount description")
                .id(discountId)
                .kind("discount")
                .name("java test discount name")
                .neverExpires(false)
                .numberOfBillingCycles(12);
        http.post("/modifications/create_modification_for_tests", discountRequest);

        List<Discount> discounts = gateway.discount().all();
        Discount actualDiscount = null;
        for (Discount discount : discounts) {
            if (discount.getId().equals(discountId)) {
                actualDiscount = discount;
                break;
            }
        }

        assertEquals(new BigDecimal("100.00"), actualDiscount.getAmount());
        assertEquals("java test discount description", actualDiscount.getDescription());
        assertEquals("discount", actualDiscount.getKind());
        assertEquals("java test discount name", actualDiscount.getName());
        assertEquals(false, actualDiscount.neverExpires());
        assertEquals(new Integer("12"), actualDiscount.getNumberOfBillingCycles());
    }
}
