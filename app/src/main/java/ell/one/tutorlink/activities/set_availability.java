package ell.one.tutorlink.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ell.one.tutorlink.R;
import ell.one.tutorlink.database_handlers.FirebaseManager;

public class set_availability extends AppCompatActivity {

    private Button btnPickDate, btnPickStartTime, btnPickEndTime, btnSubmit;
    private String selectedDate = null, startTime = null, endTime = null;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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

        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickStartTime = findViewById(R.id.btnPickStartTime);
        btnPickEndTime = findViewById(R.id.btnPickEndTime);
        btnSubmit = findViewById(R.id.btnSubmitSchedule);

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickStartTime.setOnClickListener(v -> showTimePicker(true));
        btnPickEndTime.setOnClickListener(v -> showTimePicker(false));
        btnSubmit.setOnClickListener(v -> saveAvailability());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Date selected = calendar.getTime();
            Date today = new Date();

            if (selected.before(today)) {
                Toast.makeText(this, "Cannot select a past date", Toast.LENGTH_SHORT).show();
            } else if (dayOfWeek == Calendar.SUNDAY) {
                Toast.makeText(this, "Bookings are not allowed on Sundays", Toast.LENGTH_SHORT).show();
            } else {
                selectedDate = dateFormat.format(selected); // Now formatted as yyyy-MM-dd
                btnPickDate.setText("Date: " + selectedDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        picker.getDatePicker().setMinDate(System.currentTimeMillis()); // Prevent selecting past dates directly
        picker.show();
    }

    private void showTimePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            if (isStart) {
                startTime = time;
                btnPickStartTime.setText("Start: " + time);
            } else {
                endTime = time;
                btnPickEndTime.setText("End: " + time);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        picker.show();
    }

    private void saveAvailability() {
        if (selectedDate == null || startTime == null || endTime == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date start = timeFormat.parse(startTime);
            Date end = timeFormat.parse(endTime);

            if (start != null && end != null && !end.after(start)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String dateKey = selectedDate; // Already formatted as yyyy-MM-dd, no need to replace characters

        firebaseManager.saveAvailabilityForDay(dateKey, startTime, endTime, new FirebaseManager.AvailabilitySaveListener() {
            @Override
            public void onSuccess() {
                Log.d("Availability", "Saved for " + selectedDate);
                Toast.makeText(set_availability.this, "Availability saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Availability", "Failed to save", e);
                Toast.makeText(set_availability.this, "Error saving availability", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
