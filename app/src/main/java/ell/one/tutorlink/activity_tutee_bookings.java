package ell.one.tutorlink;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_tutee_bookings extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListView bookingsListView;
    private List<String> bookingDetails = new ArrayList<>();
    private List<DocumentSnapshot> bookingDocs = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutee_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bookingsListView = findViewById(R.id.tuteeBookingsListView);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookingDetails);
        bookingsListView.setAdapter(adapter);

        loadTuteeBookings();

        bookingsListView.setOnItemClickListener((parent, view, position, id) -> {
            DocumentSnapshot bookingDoc = bookingDocs.get(position);
            cancelBooking(bookingDoc);
        });
    }

    private void loadTuteeBookings() {
        if (currentUser == null) return;

        db.collection("bookings")
                .whereEqualTo("tuteeId", currentUser.getUid())
                .get()
                .addOnSuccessListener(query -> {
                    bookingDetails.clear();
                    bookingDocs.clear();
                    for (DocumentSnapshot doc : query) {
                        String tutorId = doc.getString("tutorId");
                        String date = doc.getString("date");
                        String start = doc.getString("startTime");
                        String end = doc.getString("endTime");
                        String status = doc.getString("status");

                        bookingDetails.add("Tutor ID: " + tutorId + "\n" + date + " - " + start + " to " + end + "\nStatus: " + status);
                        bookingDocs.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Bookings", "Error loading", e);
                    Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                });
    }

    private void cancelBooking(DocumentSnapshot bookingDoc) {
        String docId = bookingDoc.getId();
        String tutorId = bookingDoc.getString("tutorId");
        String date = bookingDoc.getString("date");

        db.collection("bookings").document(docId).delete()
                .addOnSuccessListener(unused -> {
                    // Restore availability
                    Map<String, Object> restoredSlot = new HashMap<>();
                    restoredSlot.put("date", bookingDoc.getString("date"));
                    restoredSlot.put("startTime", bookingDoc.getString("startTime"));
                    restoredSlot.put("endTime", bookingDoc.getString("endTime"));
                    restoredSlot.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("users").document(tutorId)
                            .collection("availability").document(date)
                            .set(restoredSlot)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Booking cancelled and slot restored", Toast.LENGTH_SHORT).show();
                                loadTuteeBookings();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Cancelled but failed to restore slot", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show());
    }
}