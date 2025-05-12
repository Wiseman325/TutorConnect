package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import ell.one.tutorlink.R;

public class MettingsActivity extends AppCompatActivity {

    private EditText targetUserIdEditText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mettings);

        targetUserIdEditText = findViewById(R.id.user_id_edit_text);
        startButton = findViewById(R.id.start_btn);

        startButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentUserId = currentUser.getUid();
            String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown User";
            String targetUserId = targetUserIdEditText.getText().toString().trim();

            if (targetUserId.isEmpty()) {
                Toast.makeText(this, "Please enter the target User ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Starting call with User ID: " + targetUserId, Toast.LENGTH_SHORT).show();

            startService(currentUserId, userName);

            Intent intent = new Intent(MettingsActivity.this, CallActivity.class);
            intent.putExtra("USER_ID", targetUserId); // Correct Target User ID
            startActivity(intent);
        });
    }

    private void startService(String userID, String userName) {
        long appID = 2032483569L;
        String appSign = "2c601d30cecb65516e0b2b5e605ea73523335c0f993b57d532450ff58535662c";

        ZegoUIKitPrebuiltCallInvitationConfig config = new ZegoUIKitPrebuiltCallInvitationConfig();
        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallService.unInit();
    }
}
