package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ell.one.tutorlink.ProfileActivity;
import ell.one.tutorlink.R;

public class tutor_home extends AppCompatActivity {

    private TextView ewalletBalance, welcomeText;
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
        welcomeText = findViewById(R.id.welcomeText);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSetAvailability = findViewById(R.id.btnSetAvailability);
        btnSessionRequests = findViewById(R.id.btnSessionRequests);
        btnScheduleSession = findViewById(R.id.btnScheduleSession);
        btnMyResources = findViewById(R.id.btnMyResources);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewBookedSessions = findViewById(R.id.btnViewBookedSessions);

        btnViewBookedSessions.setOnClickListener(v ->
                startActivity(new Intent(tutor_home.this, TutorBookings.class))
        );

        ewalletBalance.setText("E-Wallet Balance: R 0.00"); // TODO: Later fetch from Firestore

        // Set Dynamic Welcome Message
        setWelcomeMessage();

        // Button Click Listeners
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(tutor_home.this, ProfileActivity.class))
        );

        btnSetAvailability.setOnClickListener(v ->
                startActivity(new Intent(tutor_home.this, set_availability.class))
        );

        btnScheduleSession.setOnClickListener(v ->
                startActivity(new Intent(tutor_home.this, ViewScheduleActivity.class))
        );

        btnSessionRequests.setOnClickListener(v -> {
            Toast.makeText(this, "Meeting Session Requests clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(tutor_home.this, MettingsActivity.class));
        });

        btnMyResources.setOnClickListener(v ->
                Toast.makeText(this, "View Resources clicked", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(tutor_home.this, LoginActivity.class));
            finish();
        });
    }

    private void setWelcomeMessage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                welcomeText.setText("Welcome, " + displayName + "!");
            } else if (email != null) {
                welcomeText.setText("Welcome, " + email + "!");
            } else {
                welcomeText.setText("Welcome, Tutor!");
            }
        } else {
            welcomeText.setText("Welcome, Guest!");
        }
    }
}
