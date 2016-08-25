package jay.com.cometchatdummy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.callbacks.SubscribeCallbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {

    private String userId,userName;
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private CometChat cometChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            userName = extras.getString("userName");
        }

        setTitle(userName);

        final long id = Long.parseLong(userId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                cometChat.sendMessage(String.valueOf(id), messageText, new Callbacks() {
                    @Override
                    public void successCallback(JSONObject response) {
                        messageET.setText("");
                        try {
                            adapter.add(new ChatMessage(response.getLong("id"),true,response.getString("m"),id, DateFormat.getDateTimeInstance().format(new Date())));
                            adapter.notifyDataSetChanged();
                            messagesContainer.setSelection(messagesContainer.getCount() - 1);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void failCallback(JSONObject response) {}
                });
            }
        });

        long messageId = -1;
        cometChat = CometChat.getInstance(getApplicationContext(),"10415x77177883eedf5255554e825180e563c1");
        cometChat.getChatHistory(id, messageId, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                try {
                    if (jsonObject != null) {
                        Iterator<String> it = jsonObject.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            try {
                                JSONArray array = jsonObject.getJSONArray(key);
                                for (int i=0;i<array.length();i++){
                                    JSONObject object = array.getJSONObject(i);
                                    String id = object.getString("id");
                                    String from = object.getString("from");
                                    String message = object.getString("message");
                                    String sent = object.getString("sent");
                                    adapter.add(new ChatMessage(Long.parseLong(id),stringToBool(from),message,Long.parseLong(id),sent));
                                }
                            } catch (Throwable e) {
                                try {
                                    Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                                    System.out.println(key + ":" + jsonObject.getString(key));
                                }
                                catch (Exception ee) {
                                    Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Parsing Failed",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject),Toast.LENGTH_LONG).show();
            }
        });
        adapter.notifyDataSetChanged();
        messagesContainer.setSelection(messagesContainer.getCount() - 1);

        cometChat.subscribe(true, new SubscribeCallbacks() {
            @Override
            public void gotOnlineList(JSONObject jsonObject) {}

            @Override
            public void onError(JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMessageReceived(JSONObject jsonObject) {
                try {
                    String id = jsonObject.getString("id");
                    String msg = jsonObject.getString("message");
                    String sent = jsonObject.getString("sent");
                    adapter.add(new ChatMessage(Long.parseLong(id),false,msg,Long.parseLong(id),sent));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void gotProfileInfo(JSONObject jsonObject) {}

            @Override
            public void gotAnnouncement(JSONObject jsonObject) {}

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {}

            @Override
            public void onActionMessageReceived(JSONObject jsonObject) {}
        });
        adapter.notifyDataSetChanged();
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private boolean stringToBool(String s) {
        if (s.equals("0"))
            return true;
        else
            return false;
    }
}
