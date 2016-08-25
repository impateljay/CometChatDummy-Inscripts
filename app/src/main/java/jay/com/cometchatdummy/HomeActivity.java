package jay.com.cometchatdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Home");

        Button oneOnOne = (Button) findViewById(R.id.btn_OneOnOne);
        Button chatRoom = (Button) findViewById(R.id.btn_Chatroom);
        Button profile = (Button) findViewById(R.id.btn_Profile);
        Button logout = (Button) findViewById(R.id.btn_logout);

        oneOnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),OneOnOneActivity.class);
                startActivity(intent);
            }
        });

        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatroomActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CometChat cometChat = CometChat.getInstance(getApplicationContext(),"10415x77177883eedf5255554e825180e563c1");
                cometChat.logout(new Callbacks() {
                    @Override
                    public void successCallback(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void failCallback(JSONObject response) {
                        Toast.makeText(getApplicationContext(),String.valueOf(response),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}