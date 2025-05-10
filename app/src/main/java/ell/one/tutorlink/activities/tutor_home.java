package ell.one.tutorlink.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import ell.one.tutorlink.ProfileActivity;
import ell.one.tutorlink.R;

public class tutor_home extends AppCompatActivity {

    private TextView ewalletBalance;
    private LinearLayout btnEditProfile, btnSetAvailability, btnSessionRequests, btnMyResources, btnLogout, btnScheduleSession, btnViewBookedSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tutor_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ewalletBalance = findViewById(R.id.ewalletBalance);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSetAvailability = findViewById(R.id.btnSetAvailability);
        btnSessionRequests = findViewById(R.id.btnSessionRequests);
        btnScheduleSession = findViewById(R.id.btnScheduleSession);
        btnMyResources = findViewById(R.id.btnMyResources);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewBookedSessions = findViewById(R.id.btnViewBookedSessions);

        btnViewBookedSessions.setOnClickListener(v -> {
            startActivity(new Intent(tutor_home.this, TutorBookings.class));
        });

        // TODO: Later - fetch balance from Firestore
        ewalletBalance.setText("E-Wallet Balance: R 0.00");

        // Click Listeners
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Replace with actual ProfileActivity
            startActivity(new Intent(tutor_home.this, ProfileActivity.class));
        });

        btnSetAvailability.setOnClickListener(v -> {
            // TODO: Replace with AvailabilityActivity
            startActivity(new Intent(tutor_home.this, set_availability.class));

        });

        btnScheduleSession.setOnClickListener(v -> {
            // TODO: Replace with AvailabilityActivity
            startActivity(new Intent(tutor_home.this, ViewScheduleActivity.class));

        });

        btnSessionRequests.setOnClickListener(v -> {
            // TODO: Replace with SessionRequestsActivity
            Toast.makeText(this, "Meeting Session Requests clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(tutor_home.this, MettingsActivity.class));
        });

        btnMyResources.setOnClickListener(v -> {
            // TODO: Replace with ResourcesActivity
            Toast.makeText(this, "View Resources clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(tutor_home.this, LoginActivity.class));
            finish();
        });
    }
}