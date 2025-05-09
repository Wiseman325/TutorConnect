package ell.one.tutorlink;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class CallActivity extends AppCompatActivity {


    private EditText userIdEditText;
    private TextView hey_user_text_view;
    private ZegoSendCallInvitationButton voiceCallButton, videoCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userIdEditText = findViewById(R.id.user_id_edit_text);
        hey_user_text_view = findViewById(R.id.hey_user_text_view);
        voiceCallButton = findViewById(R.id.voice_call_btn);
        videoCallButton = findViewById(R.id.video_call_btn);

        String userID = getIntent().getStringExtra("userID");
        hey_user_text_view.setText("Hey, " + userID);

        userIdEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String targetUserID = userIdEditText.getText().toString().trim();
                setVoiceCall(targetUserID);
                setVideoCall(targetUserID);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void setVoiceCall(String targetUserID) {
        voiceCallButton.setIsVideoCall(false);
        voiceCallButton.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        voiceCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserID)));
    }

    void setVideoCall(String targetUserID) {
        videoCallButton.setIsVideoCall(true);
        videoCallButton.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        videoCallButton.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserID)));
    }
}