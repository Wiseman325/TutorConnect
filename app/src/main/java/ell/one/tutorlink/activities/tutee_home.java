package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import ell.one.tutorlink.ProfileActivity;
import ell.one.tutorlink.R;
import ell.one.tutorlink.database_handlers.FirebaseManager;

public class tutee_home extends AppCompatActivity {

    private LinearLayout btnSearchTutors, btnProfile, btnChatbot, btnLogout;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dash);


        firebaseManager = new FirebaseManager(this);

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
    }
}
