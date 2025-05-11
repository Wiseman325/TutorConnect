package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ell.one.tutorlink.ProfileActivity;
import ell.one.tutorlink.R;
import ell.one.tutorlink.database_handlers.FirebaseManager;

public class tutee_home extends AppCompatActivity {

    private LinearLayout btnSearchTutors, btnProfile, btnChatbot, btnLogout;
    private FirebaseManager firebaseManager;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dash);


        firebaseManager = new FirebaseManager(this);

        welcomeMessage = findViewById(R.id.welcomeMessage);
        btnSearchTutors = findViewById(R.id.btnSearchTutors);
        btnProfile = findViewById(R.id.btnProfile);
        btnChatbot = findViewById(R.id.btnChatbot);
        btnLogout = findViewById(R.id.btnLogout);

        LinearLayout btnMyBookings = findViewById(R.id.btnMyBookings);
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(tutee_home.this, activity_tutee_bookings.class);
            startActivity(intent);
        });


        btnSearchTutors.setOnClickListener(v -> {
            startActivity(new Intent(tutee_home.this, activity_search_tutors.class));
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(tutee_home.this, EditProfileActivity.class));
        });

        btnChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(tutee_home.this, MettingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            firebaseManager.signOut();
            Intent intent = new Intent(tutee_home.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setWelcomeMessage();
    }

    private void setWelcomeMessage() {
        firebaseManager.getUserProfile(profile -> {
            if (profile != null) {
                String name = profile.getName();
                String role = profile.getSpecialization() != null ? "Tutor" : "Tutee"; // Simple role check

                if (name == null || name.isEmpty()) {
                    welcomeMessage.setText("Hello " + role + "!");
                } else {
                    welcomeMessage.setText("Hello " + name + "!");
                }
            } else {
                welcomeMessage.setText("Hello Tutee!");
            }
        });
    }
}
