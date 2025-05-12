package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ell.one.tutorlink.R;

public class activity_book_session extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView timeSlotRecyclerView;
    private List<String> availableSlots = new ArrayList<>();
    private List<DocumentSnapshot> slotDocuments = new ArrayList<>();
    private String tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_session);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        timeSlotRecyclerView = findViewById(R.id.timeSlotRecyclerView);

        tutorId = getIntent().getStringExtra("tutorId");
        if (tutorId == null || tutorId.trim().isEmpty()) {
            Toast.makeText(this, "Tutor ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAvailableSlots();
    }

    private void loadAvailableSlots() {
        db.collection("users")
                .document(tutorId)
                .collection("availability")
                .get()
                .addOnSuccessListener(query -> {
                    availableSlots.clear();
                    slotDocuments.clear();

                    for (DocumentSnapshot doc : query) {
                        String date = doc.getString("date");
                        String start = doc.getString("startTime");
                        String end = doc.getString("endTime");

                        if (date != null && start != null && end != null) {
                            String slotDisplay = date + " - " + start + " to " + end;
                            availableSlots.add(slotDisplay);
                            slotDocuments.add(doc);
                        }
                    }

                    if (availableSlots.isEmpty()) {
                        Toast.makeText(this, "No available slots. Please check again later.", Toast.LENGTH_LONG).show();
                    }

                    timeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    ell.one.tutorlink.adapters.SlotAdapter adapter = new ell.one.tutorlink.adapters.SlotAdapter(availableSlots, position ->
                            bookSession(slotDocuments.get(position))
                    );
                    timeSlotRecyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("BookSession", "Error loading availability", e);
                    Toast.makeText(this, "Failed to load slots", Toast.LENGTH_SHORT).show();
                });
    }

    private void bookSession(DocumentSnapshot slotDoc) {
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to book", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = slotDoc.getString("date");
        String start = slotDoc.getString("startTime");
        String end = slotDoc.getString("endTime");
        String docId = slotDoc.getId();

        // ✅ Retrieve price from Firestore (default to 100 if not found)
        double price = slotDoc.getDouble("price") != null ? slotDoc.getDouble("price") : 100.0;

        Map<String, Object> booking = new HashMap<>();
        booking.put("tuteeId", currentUser.getUid());
        booking.put("tutorId", tutorId);
        booking.put("date", date);
        booking.put("startTime", start);
        booking.put("endTime", end);
        booking.put("status", "pending");
        booking.put("price", price);

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(docRef -> {
                    // Delete the availability slot after booking
                    db.collection("users").document(tutorId)
                            .collection("availability").document(docId)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Session booked successfully!", Toast.LENGTH_SHORT).show();

                                // ✅ Pass all required values to PaymentActivity
                                Intent intent = new Intent(activity_book_session.this, PaymentActivity.class);
                                intent.putExtra("tutorId", tutorId);
                                intent.putExtra("date", date);
                                intent.putExtra("startTime", start);
                                intent.putExtra("endTime", end);
                                intent.putExtra("docId", docId);
                                intent.putExtra("price", price);

                                // Student Info
                                String studentName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Student";
                                String studentEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "student@example.com";
                                String studentPhone = "0000000000"; // You can collect this from user profile if available.

                                intent.putExtra("studentName", studentName);
                                intent.putExtra("studentEmail", studentEmail);
                                intent.putExtra("studentPhone", studentPhone);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Booked, but failed to remove availability.", Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e -> {
                    Log.e("BookSession", "Booking failed", e);
                    Toast.makeText(this, "Failed to book session", Toast.LENGTH_SHORT).show();
                });
    }
}
