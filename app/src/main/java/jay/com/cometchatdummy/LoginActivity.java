package jay.com.cometchatdummy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
        Button login = (Button) findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() > 0 && password.getText().length() > 0) {
                    CometChat cometchat = CometChat.getInstance(getApplicationContext(), "10415x77177883eedf5255554e825180e563c1");
                    cometchat.login(String.valueOf(username.getText()), String.valueOf(password.getText()), new Callbacks() {
                        String message = "";

                        @Override
                        public void successCallback(JSONObject jsonObject) {
                                try {
                                    message = jsonObject.getString("message");
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error while parsing json", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }

                        @Override

                        public void failCallback(JSONObject jsonObject) {
                            try {
                                message = jsonObject.getString("message");
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error while parsing json", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please enter username and password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}