package LIBRO.libro.Gateway;


import LIBRO.libro.Domain.PaymentType;
import LIBRO.libro.Entities.Payment;
import LIBRO.libro.Entities.SubscriptionPlan;
import LIBRO.libro.Entities.User;
import LIBRO.libro.Payload.Response.PaymentLinkResponse;
import LIBRO.libro.Service.SubscriptionPlanService;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final SubscriptionPlanService subscriptionPlanService;
    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Value("${razorpay.callback.base-url:http://localhost:5173}")
    private String callbackBaseUrl;

    public PaymentLinkResponse createPayementLink(User user , Payment payment ) throws Exception {


        try{

            RazorpayClient razorpayClient=new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            Long amountinpaisa=payment.getAmount()*(new java.math.BigDecimal("100")).intValue();

            JSONObject paymentlinkreq=new JSONObject();
            paymentlinkreq.put("amount",amountinpaisa);
            paymentlinkreq.put("currency","INR");
            paymentlinkreq.put("description",payment.getDescription());

            JSONObject customer=new JSONObject();
            customer.put("name",user.getFullName());
            customer.put("email",user.getEmail());

            if(user.getPhone()!=null){
                customer.put("contact",user.getPhone());
            }
            paymentlinkreq.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            notify.put("sms", user.getPhone() != null);
            paymentlinkreq.put("notify", notify);

// Enable reminders
            paymentlinkreq.put("reminder_enable", true);


            // Callback configuration
            String successUrl = callbackBaseUrl + "/payment-success/" + payment.getId();


            paymentlinkreq.put("callback_url", successUrl);
            paymentlinkreq.put("callback_method", "get");


            JSONObject notes = new JSONObject();
            notes.put("user_id", user.getId());
            notes.put("payment_id", payment.getId());

            if (payment.getPaymentType() == PaymentType.MEMBERSHIP) {
                notes.put("subscription_id", payment.getSubscription().getId());
                notes.put("plan", payment.getSubscription().getSubscriptionPlan().getPlanCode());
                notes.put("type", PaymentType.MEMBERSHIP);
            } else if (payment.getPaymentType() == PaymentType.FINE ||
                       payment.getPaymentType() == PaymentType.LOST_BOOK_PENALTY ||
                       payment.getPaymentType() == PaymentType.DAMAGED_BOOK_PENALTY) {
                if (payment.getFineId() != null) {
                    notes.put("fine_id", payment.getFineId());
                }
                notes.put("type", payment.getPaymentType().toString());
            }

            paymentlinkreq.put("notes", notes);

            PaymentLink paymentLink=razorpayClient.paymentLink.create(paymentlinkreq);

            String paymentUrl=paymentLink.get("short_url").toString();
            String paymentLinkid=paymentLink.get("id").toString();

            PaymentLinkResponse paymentLinkResponse=new PaymentLinkResponse();

            paymentLinkResponse.setPayment_link_id(paymentLinkid);
            paymentLinkResponse.setPayment_link_url(paymentUrl);

            return paymentLinkResponse;


        }
        catch (RazorpayException e){
            throw new Exception(e);

        }


    }


    public JSONObject fetchPaymentDetails(String paymentId) throws Exception {

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            com.razorpay.Payment payment = razorpay.payments.fetch(paymentId);

            return payment.toJson();

        } catch (RazorpayException e) {

            throw new Exception("Failed to fetch payment details: " + e.getMessage(), e);
        }
    }

    public boolean isValidPayment(String paymentId) {

        try {

            JSONObject paymentDetails = fetchPaymentDetails(paymentId);

            String status = paymentDetails.optString("status");
            long amount = paymentDetails.optLong("amount");
            long amountInRupees = amount / 100;

            JSONObject notes = paymentDetails.getJSONObject("notes");

            String paymentType = notes.optString("type");

            // 1️⃣ Check status
            if (!"captured".equalsIgnoreCase(status)) {
                return false;
            }

            // 2️⃣ Check expected amount
            if (paymentType.equals(PaymentType.MEMBERSHIP.toString())) {

                String planCode = notes.optString("plan");

                SubscriptionPlan subscriptionPlan = subscriptionPlanService.getBySubscriptionplancode(planCode);
                return amountInRupees == subscriptionPlan.getPrice();

            } else if (paymentType.equals(PaymentType.FINE.toString()) ||
                       paymentType.equals(PaymentType.LOST_BOOK_PENALTY.toString()) ||
                       paymentType.equals(PaymentType.DAMAGED_BOOK_PENALTY.toString())) {
                return true; 
            }

            return false;

        } catch (Exception e) {

            return false;
        }
    }


}
