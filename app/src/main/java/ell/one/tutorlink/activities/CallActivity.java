package ell.one.tutorlink.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // Display current logged-in user info
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            heyUserTextView.setText("Hey, " + currentUser.getDisplayName());
        } else {
            heyUserTextView.setText("Hey, Guest");
        }

        // Set initial Call Buttons Configuration
        setCallButtons(targetUserID);

        // Listen for changes in Target User ID field
        userIdEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newTargetUserID = userIdEditText.getText().toString().trim();
                setCallButtons(newTargetUserID);
            }
        });
    }

    private void setCallButtons(String targetUserID) {
        if (targetUserID.isEmpty()) return;

        // Configure Voice Call Button two
        voiceCallButton.setIsVideoCall(false);
        voiceCallButton.setResourceID("zego_uikit_call"); // Ensure this matches ZegoCloud Console
        voiceCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));

        // Configure Video Call Button
        videoCallButton.setIsVideoCall(true);
        videoCallButton.setResourceID("zego_uikit_call"); // Ensure this matches ZegoCloud Console
        videoCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));
    }
}
