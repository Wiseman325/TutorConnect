package ell.one.tutorlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

import ell.one.tutorlink.data_adapters.BookingAdapter;
import ell.one.tutorlink.models.BookingModel;

public class TutorBookings extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<BookingModel> bookings = new ArrayList<>();
    private Button btnBackToTutorHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_bookings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.tutorBookingsRecycler);
        btnBackToTutorHome = findViewById(R.id.btnBackToTutorHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(bookings, null); // No cancel listener for tutors
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadTutorBookings();

        btnBackToTutorHome.setOnClickListener(v -> {
            Intent intent = new Intent(TutorBookings.this, tutor_home.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadTutorBookings() {
        if (currentUser == null) return;

        db.collection("bookings")
                .whereEqualTo("tutorId", currentUser.getUid())
                .get()
                .addOnSuccessListener(query -> {
                    bookings.clear();
                    for (QueryDocumentSnapshot bookingDoc : query) {
                        String tuteeId = bookingDoc.getString("tuteeId");
                        String date = bookingDoc.getString("date");
                        String startTime = bookingDoc.getString("startTime");
                        String endTime = bookingDoc.getString("endTime");
                        String status = bookingDoc.getString("status");

                        if (tuteeId != null) {
                            db.collection("users").document(tuteeId).get()
                                    .addOnSuccessListener(tuteeDoc -> {
                                        String tuteeName = tuteeDoc.getString("name");

                                        bookings.add(new BookingModel(
                                                bookingDoc.getId(),
                                                currentUser.getUid(),
                                                null, // tutorName not needed here
                                                tuteeId,
                                                tuteeName != null ? tuteeName : "Tutee",
                                                date,
                                                startTime,
                                                endTime,
                                                status
                                        ));
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TutorBookings", "Error fetching bookings", e);
                    Toast.makeText(this, "Failed to load tutor bookings", Toast.LENGTH_SHORT).show();
                });
    }
}
