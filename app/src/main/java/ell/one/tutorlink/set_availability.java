package ell.one.tutorlink;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ell.one.tutorlink.database_handlers.FirebaseManager;

public class set_availability extends AppCompatActivity {

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final Map<String, EditText> timeSlotInputs = new HashMap<>();
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_availability);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseManager = new FirebaseManager(this);

        for (String day : DAYS) {
            int layoutId = getResources().getIdentifier(day.toLowerCase() + "Card", "id", getPackageName());
            View dayCard = findViewById(layoutId);
            if (dayCard != null) {
                TextView label = dayCard.findViewById(R.id.dayLabel);
                EditText timeSlotsInput = dayCard.findViewById(R.id.timeSlotsInput);
                label.setText(day);
                timeSlotInputs.put(day, timeSlotsInput);
            }
        }

        Button submitButton = findViewById(R.id.submitAvailability);
        submitButton.setOnClickListener(v -> saveAvailability());
    }

    private void saveAvailability() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String day : DAYS) {
            EditText input = timeSlotInputs.get(day);
            if (input != null) {
                String rawInput = input.getText().toString().trim();
                if (!rawInput.isEmpty()) {
                    String[] slots = rawInput.split(",");
                    List<String> slotsList = new ArrayList<>();
                    for (String slot : slots) {
                        slotsList.add(slot.trim());
                    }

                    firebaseManager.saveAvailabilityForDay(day, slotsList, new FirebaseManager.AvailabilitySaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("Availability", day + " saved successfully");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Availability", "Failed to save " + day, e);
                        }
                    });
                }
            }
        }

        Toast.makeText(this, "Availability saved successfully", Toast.LENGTH_SHORT).show();
    }

}
