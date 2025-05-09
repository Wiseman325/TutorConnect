package ell.one.tutorlink;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

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

        startButton.setOnClickListener((v) ->{
            String userID = userIdEditText.getText().toString().trim();
            if (userID.isEmpty()) {
                Toast.makeText(MettingsActivity.this, "Please enter User ID", Toast.LENGTH_SHORT).show();
                return;
            }
                // TODO: Call initiation logic goes here (Zego/Agora SDK)
                Toast.makeText(MettingsActivity.this, "Starting call for User ID: " + userID, Toast.LENGTH_SHORT).show();

                // Example of moving to a CallActivity (create this later)
                // Intent intent = new Intent(MeetingsActivity.this, CallActivity.class);
                // intent.putExtra("USER_ID", userId);
                // startActivity(intent);
            startService(userID);
            Intent intent = new Intent(MettingsActivity.this, CallActivity.class);
            intent.putExtra("USER_ID", userID);
            startActivity(intent);
        });
    }

        void startService(String userID) {
            Application application = getApplication(); // Android's application context
            long appID = 2032483569;   // yourAppID
            String appSign = "2c601d30cecb65516e0b2b5e605ea73523335c0f993b57d532450ff58535662c";  // yourAppSign
            String userName = userID;   // yourUserName

            ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

            ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallService.unInit();
    }
}