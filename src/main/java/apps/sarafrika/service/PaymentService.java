package apps.sarafrika.service;

import apps.sarafrika.dto.StkPushRequest;
import apps.sarafrika.dto.StkPushResponse;
import apps.sarafrika.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    @Inject
    @RestClient
    CheddarApi cheddarApi;

    @ConfigProperty(name = "cheddar.paybill", defaultValue = "4953892")
    String paybill;

    public void initiateStkPush(Order order, String phoneNumber) {
        if (order == null) {
            LOG.error("Cannot initiate STK push for a null order.");
            return;
        }

        String normalizedPhoneNumber = normalizePhoneNumber(phoneNumber);

        StkPushRequest stkPushRequest = new StkPushRequest(
            normalizedPhoneNumber,
            order.orderAmount,
            paybill,
            order.referenceCode
        );

        try {
            StkPushResponse response = cheddarApi.requestPayment(stkPushRequest);
            if (response != null && response.data != null && "0".equals(response.data.responseCode)) {
                LOG.infof("STK Push initiated successfully for order %s. CheckoutRequestID: %s", 
                    order.referenceCode, response.data.checkoutRequestId);
            } else {
                String errorMessage = response != null ? response.error : "No response from payment gateway";
                if (response != null && response.data != null) {
                    errorMessage = response.data.responseDescription;
                }
                LOG.errorf("Failed to initiate STK push for order %s: %s", order.referenceCode, errorMessage);
            }
        } catch (Exception e) {
            LOG.errorf(e, "Exception while initiating STK push for order %s", order.referenceCode);
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String cleaned = phoneNumber.trim().replace("+", "");
        if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1);
        }
        return cleaned;
    }
}
