package ell.one.tutorlink.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;

import ell.one.tutorlink.R;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PayHere";
    private Button payButton;
    private TextView paymentStatusText;

    // Intent Extras
    private String tutorId, date, startTime, endTime, docId;
    private String studentName, studentEmail, studentPhone;
    private double sessionPrice;

    private final ActivityResultLauncher<Intent> paymentResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            String msg = response.isSuccess()
                                    ? "✅ Payment Success\nRef: " + response.getData()
                                    : "❌ Payment Failed\n" + response.toString();
                            Log.d(TAG, msg);
                            paymentStatusText.setText(msg);
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    paymentStatusText.setText("❌ User canceled the payment.");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Bindings
        payButton = findViewById(R.id.payButton);
        paymentStatusText = findViewById(R.id.paymentStatusText);

        // Retrieve Intent Data
        tutorId = getIntent().getStringExtra("tutorId");
        date = getIntent().getStringExtra("date");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        docId = getIntent().getStringExtra("docId");
        studentName = getIntent().getStringExtra("studentName");
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentPhone = getIntent().getStringExtra("studentPhone");
        sessionPrice = getIntent().getDoubleExtra("price", 0.0);

        // Log intent data for debugging
        Log.d(TAG, "Received Payment Data -> studentName: " + studentName + ", studentEmail: " + studentEmail +
                ", studentPhone: " + studentPhone + ", sessionPrice: " + sessionPrice);

        payButton.setOnClickListener(v -> initiatePayment());
    }

    private void initiatePayment() {
        try {
            // Validation and Default Values
            if (studentName == null || studentName.isEmpty()) studentName = "Student";
            if (studentEmail == null || studentEmail.isEmpty()) studentEmail = "student@example.com";
            if (studentPhone == null || studentPhone.isEmpty()) studentPhone = "0000000000";

            if (sessionPrice <= 0.0) {
                paymentStatusText.setText("❌ Invalid session price.");
                Log.e(TAG, "Invalid session price: " + sessionPrice);
                return;
            }

            InitRequest req = new InitRequest();
            req.setMerchantId("1230393"); // ✅ Replace with your valid Sandbox Merchant ID
            req.setCurrency("LKR");
            req.setAmount(sessionPrice);
            req.setOrderId("ORDER_" + System.currentTimeMillis());
            req.setItemsDescription("Tutor Booking for " + date + " at " + startTime);

            // Optional: Custom fields
            req.setCustom1(tutorId != null ? tutorId : "unknown_tutor");
            req.setCustom2(docId != null ? docId : "unknown_doc");

            // Handle Customer Info Safely
            String[] nameParts = studentName.split(" ", 2);
            req.getCustomer().setFirstName(nameParts[0]);
            req.getCustomer().setLastName(nameParts.length > 1 ? nameParts[1] : "Student");
            req.getCustomer().setEmail(studentEmail);
            req.getCustomer().setPhone(studentPhone);
            req.getCustomer().getAddress().setAddress("123 School Lane");
            req.getCustomer().getAddress().setCity("Durban");
            req.getCustomer().getAddress().setCountry("South Africa");

            req.getCustomer().getDeliveryAddress().setAddress("123 School Lane");
            req.getCustomer().getDeliveryAddress().setCity("Durban");
            req.getCustomer().getDeliveryAddress().setCountry("South Africa");

            // Item Breakdown
            req.getItems().add(new Item(null, "Tutoring Session", 1, sessionPrice));

            // ✅ Use a valid Notify URL even in Sandbox Mode
            req.setNotifyUrl("https://sandbox.payhere.lk/notify");

            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

            // Force Sandbox Mode
            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

            // Final Check Before Launch
            Log.d(TAG, "Launching Payment: " + req.getAmount() + " ZAR for " + studentName);

            paymentResultLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Payment launch error", e);
            paymentStatusText.setText("❌ Error launching payment: " + e.getMessage());
        }
    }
}
