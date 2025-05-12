package ell.one.tutorlink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

import ell.one.tutorlink.R;

public class CallActivity extends AppCompatActivity {

    private EditText userIdEditText;
    private TextView heyUserTextView;
    private ZegoSendCallInvitationButton voiceCallButton, videoCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        userIdEditText = findViewById(R.id.user_id_edit_text);
        heyUserTextView = findViewById(R.id.hey_user_text_view);
        voiceCallButton = findViewById(R.id.voice_call_btn);
        videoCallButton = findViewById(R.id.video_call_btn);

        // Get target user ID from Intent
        String targetUserID = getIntent().getStringExtra("USER_ID");
        userIdEditText.setText(targetUserID);

        // Start Zego Service
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            heyUserTextView.setText("Hey, " + currentUser.getDisplayName());
            startService(currentUser.getUid(), currentUser.getDisplayName());
        } else {
            heyUserTextView.setText("Hey, Guest");
        }

        // Set initial Call Buttons Configuration
        setCallButtons(targetUserID);
    }

    private void setCallButtons(String targetUserID) {
        if (targetUserID == null || targetUserID.isEmpty()) return;

        // Voice Call Button
        voiceCallButton.setIsVideoCall(false);
        voiceCallButton.setResourceID("zego_uikit_call"); // Ensure this exists in ZegoCloud
        voiceCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));

        // Video Call Button
        videoCallButton.setIsVideoCall(true);
        videoCallButton.setResourceID("zego_uikit_call");
        videoCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));
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
