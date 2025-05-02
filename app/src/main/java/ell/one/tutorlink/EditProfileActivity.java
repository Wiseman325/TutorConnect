package ell.one.tutorlink;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editEmailText, editPhoneNo, editUsername, editBio, editSpecialization, editRate;
    Button saveButton, cancelButton;

    String nameUser, emailUser, usernameUser, phoneUser, bioUser, specializationUser, rateUser;

    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind views
        editName = findViewById(R.id.editName);
        editEmailText = findViewById(R.id.editEmail);
        editPhoneNo = findViewById(R.id.editPhone);
        editBio = findViewById(R.id.editBio);
        editSpecialization = findViewById(R.id.editSpecialization);
        editRate = findViewById(R.id.editRate);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        showData(firebaseUser);

        saveButton.setOnClickListener(v -> updateUserProfile(firebaseUser));
        cancelButton.setOnClickListener(v -> finish());
    }

    private void updateUserProfile(FirebaseUser firebaseUser) {
        nameUser = editName.getText().toString().trim();
        emailUser = editEmailText.getText().toString().trim();
        usernameUser = editUsername.getText().toString().trim();
        phoneUser = editPhoneNo.getText().toString().trim();
        bioUser = editBio.getText().toString().trim();
        specializationUser = editSpecialization.getText().toString().trim();
        rateUser = editRate.getText().toString().trim();

        // Validation
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

        HelperClass helperClass = new HelperClass(nameUser, emailUser, phoneUser, bioUser, specializationUser, rateUser);

        reference = FirebaseDatabase.getInstance().getReference("users");
        String userID = firebaseUser.getUid();

        reference.child(userID).setValue(helperClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameUser)
                        .build();
                firebaseUser.updateProfile(userProfileChangeRequest);

                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Could not update profile", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showData(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClass helperClass = snapshot.getValue(HelperClass.class);
                if (helperClass != null) {
                    nameUser = helperClass.getName();
                    emailUser = firebaseUser.getEmail(); // always use auth email
                    phoneUser = helperClass.getPhoneNo();
                    bioUser = helperClass.getBio();
                    specializationUser = helperClass.getSpecialization();
                    rateUser = helperClass.getRate();

                    editName.setText(nameUser);
                    editEmailText.setText(emailUser);
                    editPhoneNo.setText(phoneUser);
                    editUsername.setText(usernameUser);
                    editBio.setText(bioUser);
                    editSpecialization.setText(specializationUser);
                    editRate.setText(rateUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
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
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (itemID == R.id.nav_prof) {
            startActivity(new Intent(getIntent()));
        } else if (itemID == R.id.nav_comment) {
            startActivity(new Intent(EditProfileActivity.this, CommentActivity.class));
        } else if (itemID == R.id.nav_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
