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
import com.inscripts.callbacks.SubscribeChatroomCallbacks;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat.sdk.CometChatroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ChatRoomChatActivity extends AppCompatActivity {

    private String userId;
    private String chatroomName;
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private CometChatroom cometChatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_chat);

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        adapter = new ChatAdapter(ChatRoomChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            chatroomName = extras.getString("chatroomName");
        }

        setTitle(chatroomName);

        final long id = Long.parseLong(userId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                cometChatroom.sendMessage(messageText, new Callbacks() {
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
                    public void failCallback(JSONObject response) {
                        Toast.makeText(getApplicationContext(),String.valueOf(response),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        long messageId = -1;
        cometChatroom = CometChatroom.getInstance(getApplicationContext());

        cometChatroom.subscribe(true, new SubscribeChatroomCallbacks() {
            @Override
            public void onMessageReceived(JSONObject receivedMessage) {
                try {
                    String id = receivedMessage.getString("id");
                    String message = receivedMessage.getString("message");
                    String sent = receivedMessage.getString("sent");
                    String from = receivedMessage.getString("from");
                    if (from.equals("Me"))
                        adapter.add(new ChatMessage(Long.parseLong(id),true,message,Long.parseLong(id),sent));
                    else
                        adapter.add(new ChatMessage(Long.parseLong(id),false,from+"\n"+message,Long.parseLong(id),sent));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"json parsing error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onLeaveChatroom(JSONObject leaveResponse) {}
            @Override
            public void onError(JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(),String.valueOf(errorResponse),Toast.LENGTH_LONG).show();
            }
            @Override
            public void gotChatroomMembers(JSONObject chatroomMembers) {}
            @Override
            public void gotChatroomList(JSONObject chatroomList) {}
            @Override
            public void onAVChatMessageReceived(JSONObject response){};
            @Override
            public void onActionMessageReceived(JSONObject response){};
        });
        adapter.notifyDataSetChanged();
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }
}
