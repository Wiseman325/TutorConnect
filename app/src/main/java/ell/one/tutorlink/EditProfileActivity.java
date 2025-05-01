package ell.one.tutorlink;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editStudentNo, editPhoneNo, editUsername, editEmailText;
    Button saveButton, cancelButton;
    String nameUser, emailUser, usernameUser, phoneUser, studentNoUser;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editEmailText = findViewById(R.id.editEmailText);
        editStudentNo = findViewById(R.id.editStudentNo);
        editUsername = findViewById(R.id.editUsername);
        editPhoneNo = findViewById(R.id.editPhoneNo);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        showData(firebaseUser);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile(firebaseUser);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getIntent()));
            }
        });
    }

    private void updateUserProfile(FirebaseUser firebaseUser) {

        studentNoUser = editStudentNo.getText().toString();
        emailUser = editEmailText.getText().toString();
        nameUser = editName.getText().toString();
        phoneUser = editPhoneNo.getText().toString();
        usernameUser = editUsername.getText().toString();

        String error = "";
        if (TextUtils.isEmpty(nameUser)){
            error = "Name is required";
            editName.requestFocus();
        } else if (TextUtils.isEmpty(emailUser)) {
            error = "Email address is required";
            editEmailText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
            error = "Please enter a valid email address";
            editEmailText.requestFocus();
        } else if (TextUtils.isEmpty(usernameUser)) {
            error = "Username is required";
            editUsername.requestFocus();
        } else if (TextUtils.isEmpty(studentNoUser)) {
            error = "Student number is required";
            editStudentNo.requestFocus();
        } else if (TextUtils.isEmpty(phoneUser)) {
            error = "Phone number is required";
            editPhoneNo.requestFocus();
        } else if (phoneUser.length() != 10) {
            error = "Phone number must be 10 digits long";
            editPhoneNo.requestFocus();
        } else {

//            insert data into database
            HelperClass helperClass = new HelperClass(nameUser, emailUser, studentNoUser, phoneUser, usernameUser);

//            get the reference to users
            reference = FirebaseDatabase.getInstance().getReference("users");

            String userID = firebaseUser.getUid();

            reference.child(userID).setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nameUser).build();
                        firebaseUser.updateProfile(userProfileChangeRequest);
                        Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Could not update profile details", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        Toast.makeText(this, "" + error, Toast.LENGTH_LONG).show();
    }

    public void showData(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClass helperClass = snapshot.getValue(HelperClass.class);
                if (helperClass != null){
                    nameUser = helperClass.name;
                    emailUser = firebaseUser.getEmail();
                    usernameUser = helperClass.username;
                    studentNoUser = helperClass.studentNo;
                    phoneUser = helperClass.phoneNo;

                    editStudentNo.setText(studentNoUser);
                    editEmailText.setText(emailUser);
                    editName.setText(nameUser);
                    editPhoneNo.setText(phoneUser);
                    editUsername.setText(usernameUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        inflate menu items
        getMenuInflater().inflate(R.menu.nav_menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //    on item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemID = item.getItemId();

        if (itemID == R.id.nav_home){
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (itemID == R.id.nav_prof) {
            startActivity(new Intent(getIntent()));
        } else if (itemID == R.id.nav_comment) {
            startActivity(new Intent(EditProfileActivity.this, CommentActivity.class));
        } else if (itemID == R.id.nav_logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}