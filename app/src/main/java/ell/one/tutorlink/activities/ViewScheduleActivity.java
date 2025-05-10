package ell.one.tutorlink.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import ell.one.tutorlink.R;

public class ViewScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<Map<String, Object>> scheduleList = new ArrayList<>();
    private List<String> docKeys = new ArrayList<>();
    private ell.one.tutorlink.data_adapters.AvailabilityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.availabilityRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        adapter = new ell.one.tutorlink.data_adapters.AvailabilityAdapter(scheduleList, docKeys, this::deleteAvailability);
        recyclerView.setAdapter(adapter);

        loadAvailability();
    }

    private void loadAvailability() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("availability")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    scheduleList.clear();
                    docKeys.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();

                        // Assuming 'date' field is stored as a string in "yyyy-MM-dd" format
                        String dateString = (String) data.get("date");

                        if (dateString != null && isFutureOrTodayDate(dateString)) {
                            scheduleList.add(data);
                            docKeys.add(doc.getId());
                        } else {
                            // Auto-delete expired slot
                            deleteAvailability(doc.getId());
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to load availability", e);
                    Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isFutureOrTodayDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date slotDate = sdf.parse(dateStr);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return slotDate != null && !slotDate.before(today.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // If parsing fails, treat it as expired
        }
    }

    private void deleteAvailability(String docId) {
        db.collection("users")
                .document(user.getUid())
                .collection("availability")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Deleted expired availability: " + docId);
                    loadAvailability();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to delete expired availability", e);
                });
    }
}
