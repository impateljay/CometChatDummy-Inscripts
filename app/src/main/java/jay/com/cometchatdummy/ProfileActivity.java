package jay.com.cometchatdummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.inscripts.callbacks.SubscribeCallbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");

        final CircleImageView profileImage = (CircleImageView) findViewById(R.id.profile_image);
        final TextView username = (TextView) findViewById(R.id.profile_username_txt);
        final TextView status = (TextView) findViewById(R.id.profile_status_txt);
        final TextView statusMessage = (TextView) findViewById(R.id.profile_status_message_txt);

        CometChat cometchat = CometChat.getInstance(getApplicationContext(),"10415x77177883eedf5255554e825180e563c1");
        cometchat.subscribe(true, new SubscribeCallbacks() {
            @Override
            public void gotOnlineList(JSONObject jsonObject) {

            }

            @Override
            public void onError(JSONObject jsonObject) {

            }

            @Override
            public void onMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void gotProfileInfo(JSONObject jsonObject) {
                try {
                    profileImage.setImageResource(R.drawable.profile);
                    username.setText(jsonObject.getString("n"));
                    status.setText(jsonObject.getString("s"));
                    statusMessage.setText(jsonObject.getString("m"));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void gotAnnouncement(JSONObject jsonObject) {

            }

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void onActionMessageReceived(JSONObject jsonObject) {

            }
        });
    }
}
