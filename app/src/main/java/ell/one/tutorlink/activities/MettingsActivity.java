package ell.one.tutorlink.activities;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import ell.one.tutorlink.R;

public class MettingsActivity extends AppCompatActivity {

    private EditText userIdEditText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mettings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userIdEditText = findViewById(R.id.user_id_edit_text);
        startButton = findViewById(R.id.start_btn);

        startButton.setOnClickListener((v) -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userID = currentUser.getUid();
            String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown User";

            Toast.makeText(MettingsActivity.this, "Starting call for User ID: " + userID, Toast.LENGTH_SHORT).show();

            startService(userID, userName);

            Intent intent = new Intent(MettingsActivity.this, CallActivity.class);
            intent.putExtra("USER_ID", userID);
            startActivity(intent);
        });
    }

    void startService(String userID, String userName) {
        Application application = getApplication();
        long appID = 2032483569;
        String appSign = "2c601d30cecb65516e0b2b5e605ea73523335c0f993b57d532450ff58535662c";

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallService.init(application, appID, appSign, userID, userName, callInvitationConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallService.unInit();
    }
}
