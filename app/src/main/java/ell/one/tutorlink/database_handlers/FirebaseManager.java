package ell.one.tutorlink.database_handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import ell.one.tutorlink.HelperClass;
import ell.one.tutorlink.LoginActivity;
import ell.one.tutorlink.SignupActivity;
import ell.one.tutorlink.activities.GuestActivity;
import ell.one.tutorlink.activities.TuteeHomeActivity;
import ell.one.tutorlink.activities.TutorHomeActivity;
import ell.one.tutorlink.guest;
import ell.one.tutorlink.tutee_home;
import ell.one.tutorlink.tutor_home;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final Context context;

    public FirebaseManager(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void signOut() {
        Log.d(TAG, "signOut: Signing out user");
        mAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "getCurrentUser: " + (user != null ? user.getUid() : "null"));
        return user;
    }

    public void registerUser(String email, String password, String fullName, String role) {
        Log.d(TAG, "registerUser: Registering user with email: " + email + ", role: " + role);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "registerUser: Firebase user created: " + user.getUid());

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("fullName", fullName);
                            userData.put("role", role);

                            db.collection("users").document(user.getUid())
                                    .set(userData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "registerUser: User data saved to Firestore");
                                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                        ((SignupActivity) context).finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "registerUser: Failed to save user data", e);
                                        Toast.makeText(context, "Error saving user data", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e(TAG, "registerUser: FirebaseUser is null after registration");
                            Toast.makeText(context, "User is null.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "registerUser: Registration failed", task.getException());
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getUserData(String fieldName, OnDataRetrievedListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Log.d(TAG, "getUserData: Fetching '" + fieldName + "' for user: " + user.getUid());

            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String data = document.getString(fieldName);
                        Log.d(TAG, "getUserData: Retrieved data = " + data);
                        listener.onDataRetrieved(data != null ? data : "");
                    } else {
                        Log.w(TAG, "getUserData: Document does not exist");
                        listener.onDataRetrieved("");
                    }
                } else {
                    Log.e(TAG, "getUserData: Failed to fetch document", task.getException());
                    listener.onDataRetrieved("");
                }
            });
        } else {
            Log.w(TAG, "getUserData: No user signed in");
            listener.onDataRetrieved("");
        }
    }

    public interface OnDataRetrievedListener {
        void onDataRetrieved(String data);
    }


    public void navigateBasedOnRole(Context context) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            Log.d(TAG, "navigateBasedOnRole: User role = " + role);

                            Intent intent;
                            switch (role) {
                                case "tutor":
                                    intent = new Intent(context, tutor_home.class);
                                    break;
                                case "tutee":
                                    intent = new Intent(context, tutee_home.class);
                                    break;
                                case "guest":
                                default:
                                    intent = new Intent(context, guest.class);
                                    break;
                            }

                            context.startActivity(intent);
                        } else {
                            Log.w(TAG, "navigateBasedOnRole: No role found for user");
                            Toast.makeText(context, "User role not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "navigateBasedOnRole: Failed to get user role", e);
                        Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.w(TAG, "navigateBasedOnRole: No user is signed in");
            Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }


    public interface OnUserProfileRetrieved {
        void onUserProfileLoaded(HelperClass profile);
    }

    public void getUserProfile(OnUserProfileRetrieved callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            HelperClass profile = snapshot.toObject(HelperClass.class);
                            callback.onUserProfileLoaded(profile);
                        } else {
                            Log.w(TAG, "getUserProfile: Document does not exist");
                            callback.onUserProfileLoaded(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "getUserProfile: Failed to retrieve", e);
                        callback.onUserProfileLoaded(null);
                    });
        }
    }


    public interface OnProfileUpdateListener {
        void onUpdateSuccess();
        void onUpdateFailure(Exception e);
    }

    public void updateUserProfile(HelperClass profileData, OnProfileUpdateListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .set(profileData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "updateUserProfile: Update successful");
                        listener.onUpdateSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "updateUserProfile: Failed to update", e);
                        listener.onUpdateFailure(e);
                    });
        } else {
            listener.onUpdateFailure(new Exception("No user signed in"));
        }
    }


    public boolean isEmailVerified() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }


}
