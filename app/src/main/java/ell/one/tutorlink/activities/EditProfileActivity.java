package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import ell.one.tutorlink.HelperClass;
import ell.one.tutorlink.ProfileActivity;
import ell.one.tutorlink.R;
import ell.one.tutorlink.database_handlers.FirebaseManager;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editEmailText, editPhoneNo, editBio, editRate, editAge, editInterest;
    Spinner specializationSpinner, genderSpinner;
    Button saveButton, cancelButton;

    FirebaseAuth firebaseAuth;
    FirebaseManager firebaseManager;
    ArrayAdapter<CharSequence> specializationAdapter, genderAdapter;
    String role = "tutee"; // Default to tutee if role can't be fetched

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind views
        editName = findViewById(R.id.editName);
        editEmailText = findViewById(R.id.editEmail);
        editPhoneNo = findViewById(R.id.editPhone);
        editBio = findViewById(R.id.editBio);
        specializationSpinner = findViewById(R.id.specializationSpinner);
        editRate = findViewById(R.id.editRate);
        editAge = findViewById(R.id.editAge);
        editInterest = findViewById(R.id.editInterest);
        genderSpinner = findViewById(R.id.genderSpinner);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseManager = new FirebaseManager(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Set up specialization spinner
        specializationAdapter = ArrayAdapter.createFromResource(
                this, R.array.specializations_array, android.R.layout.simple_spinner_item
        );
        specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specializationSpinner.setAdapter(specializationAdapter);

        // Set up gender spinner
        genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        if (firebaseUser != null) {
            showData(firebaseUser);
        }

        saveButton.setOnClickListener(v -> updateUserProfile(firebaseUser));
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showData(FirebaseUser firebaseUser) {
        firebaseManager.getUserProfile(profile -> {
            if (profile != null) {
                editName.setText(profile.getName());
                editEmailText.setText(firebaseUser.getEmail());
                editPhoneNo.setText(profile.getPhoneNo());
                editBio.setText(profile.getBio());

                // Determine role (assume role is stored in Firestore)
                if (profile.getSpecialization() != null && profile.getRate() != null) {
                    role = "tutor";
                } else {
                    role = "tutee";
                }

                if (role.equals("tutor")) {
                    specializationSpinner.setVisibility(View.VISIBLE);
                    editRate.setVisibility(View.VISIBLE);
                    editAge.setVisibility(View.GONE);
                    genderSpinner.setVisibility(View.GONE);
                    editInterest.setVisibility(View.GONE);

                    editRate.setText(profile.getRate());
                    String currentSpecialization = profile.getSpecialization();
                    int position = specializationAdapter.getPosition(currentSpecialization);
                    if (position >= 0) {
                        specializationSpinner.setSelection(position);
                    }

                } else { // Student fields
                    specializationSpinner.setVisibility(View.GONE);
                    editRate.setVisibility(View.GONE);
                    editAge.setVisibility(View.VISIBLE);
                    genderSpinner.setVisibility(View.VISIBLE);
                    editInterest.setVisibility(View.VISIBLE);

                    editAge.setText(profile.getAge());
                    editInterest.setText(profile.getInterest());

                    if (profile.getGender() != null) {
                        int genderPosition = genderAdapter.getPosition(profile.getGender());
                        if (genderPosition >= 0) {
                            genderSpinner.setSelection(genderPosition);
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile(FirebaseUser firebaseUser) {
        String nameUser = editName.getText().toString().trim();
        String emailUser = editEmailText.getText().toString().trim();
        String phoneUser = editPhoneNo.getText().toString().trim();
        String bioUser = editBio.getText().toString().trim();

        // Validation (common)
        if (TextUtils.isEmpty(nameUser)) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(emailUser)) {
            editEmailText.setError("Email is required");
            editEmailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            editEmailText.setError("Invalid email format");
            editEmailText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phoneUser)) {
            editPhoneNo.setError("Phone number is required");
            editPhoneNo.requestFocus();
            return;
        }
        if (phoneUser.length() != 10) {
            editPhoneNo.setError("Phone number must be 10 digits");
            editPhoneNo.requestFocus();
            return;
        }

        if (role.equals("tutor")) {
            String specializationUser = specializationSpinner.getSelectedItem().toString();
            String rateUser = editRate.getText().toString().trim();

            HelperClass updatedProfile = new HelperClass(nameUser, emailUser, phoneUser, bioUser, specializationUser, rateUser);

            firebaseManager.updateUserProfile(updatedProfile, new FirebaseManager.OnProfileUpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    applyProfileChangeAndRedirect(nameUser, firebaseUser);
                }

                @Override
                public void onUpdateFailure(Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Could not update profile", Toast.LENGTH_LONG).show();
                }
            });

        } else { // Student logic
            String ageUser = editAge.getText().toString().trim();
            String genderUser = genderSpinner.getSelectedItem().toString();
            String interestUser = editInterest.getText().toString().trim();

            if (TextUtils.isEmpty(ageUser)) {
                editAge.setError("Age is required");
                editAge.requestFocus();
                return;
            }

            HelperClass updatedProfile = new HelperClass(nameUser, emailUser, phoneUser, bioUser, ageUser, genderUser, interestUser);

            firebaseManager.updateUserProfile(updatedProfile, new FirebaseManager.OnProfileUpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    applyProfileChangeAndRedirect(nameUser, firebaseUser);
                }

                @Override
                public void onUpdateFailure(Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Could not update profile", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void applyProfileChangeAndRedirect(String nameUser, FirebaseUser firebaseUser) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameUser)
                .build();
        firebaseUser.updateProfile(userProfileChangeRequest);

        // Fetch role from Firestore and redirect accordingly
        FirebaseFirestore.getInstance().collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        Intent intent;
                        if ("tutor".equalsIgnoreCase(role)) {
                            intent = new Intent(EditProfileActivity.this, tutor_home.class); // Tutor profile screen
                        } else if ("tutee".equalsIgnoreCase(role)) {
                            intent = new Intent(EditProfileActivity.this, tutee_home.class); // Student dashboard/home
                        } else {
                            // Default fallback (optional)
                            intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditProfileActivity.this, "Failed to retrieve role.", Toast.LENGTH_SHORT).show()
                );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.nav_home) {
            Intent intent = new Intent(EditProfileActivity.this, tutor_home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (itemID == R.id.nav_prof) {
            startActivity(new Intent(getIntent()));
        } else if (itemID == R.id.nav_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditProfileActivity.this, tutor_home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
