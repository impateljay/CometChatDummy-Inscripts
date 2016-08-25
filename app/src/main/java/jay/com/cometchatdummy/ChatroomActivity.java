package jay.com.cometchatdummy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.callbacks.SubscribeChatroomCallbacks;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.enums.ChatroomType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatroomActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private CometChatroom cometChatroom;
    private String chatroomId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Button buttonCreateChatroom = (Button) findViewById(R.id.btn_Chatroom);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        usersAdapter = new UsersAdapter(userList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(usersAdapter);

        setTitle("Chatroom");

        buttonCreateChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(getApplicationContext());
                edittext.setTextColor(Color.BLACK);
                alert.setMessage("Create New ChatRoom");
                alert.setTitle("Enter Chatroom Name");

                alert.setView(edittext);

                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cometChatroom.createChatroom(edittext.getText().toString(), "", ChatroomType.PUBLIC_CHATROOM, new Callbacks() {
                            @Override
                            public void successCallback(JSONObject response) {
                                try {
                                    chatroomId = response.getString("chatroom_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                cometChatroom.joinChatroom(chatroomId,edittext.getText().toString(),"", new Callbacks(){

                                    @Override
                                    public void successCallback(JSONObject jsonObject) {
                                        Intent intent = new Intent(getApplicationContext(), ChatRoomChatActivity.class);
                                        intent.putExtra("userId", chatroomId);
                                        intent.putExtra("chatroomName",edittext.getText().toString());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void failCallback(JSONObject jsonObject) {
                                        Toast.makeText(getApplicationContext(),String.valueOf(jsonObject),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Toast.makeText(getApplicationContext(), String.valueOf(response), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

        cometChatroom = CometChatroom.getInstance(getApplicationContext());
        cometChatroom.subscribe(true, new SubscribeChatroomCallbacks() {
            @Override
            public void onMessageReceived(JSONObject jsonObject) {
            }

            @Override
            public void onError(JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(), "onError" + String.valueOf(jsonObject), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLeaveChatroom(JSONObject jsonObject) {
            }

            @Override
            public void gotChatroomList(JSONObject jsonObject) {
                try {
                    if (jsonObject != null) {
                        Iterator<String> it = jsonObject.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            try {
                                JSONObject object = jsonObject.getJSONObject(key);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String online = object.getString("online");
                                String type = object.getString("type");
                                String i = object.getString("i");
                                String s = object.getString("s");
                                String createdby = object.getString("createdby");
                                userList.add(new User(id, name, online + " online"));
                            } catch (Throwable e) {
                                try {
                                    Toast.makeText(getApplicationContext(), "json parsing error", Toast.LENGTH_LONG).show();
                                    System.out.println(key + ":" + jsonObject.getString(key));
                                } catch (Exception ee) {
                                    Toast.makeText(getApplicationContext(), "json parsing error", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Parsing Failed", Toast.LENGTH_LONG).show();
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void gotChatroomMembers(JSONObject jsonObject) {
            }

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {
            }

            @Override
            public void onActionMessageReceived(JSONObject jsonObject) {
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new OneOnOneActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                User user = userList.get(position);
                final String userid = user.getId();
                final String chatroomName = user.getUsername();
                cometChatroom.joinChatroom(user.getId(),user.getUsername(),"", new Callbacks(){

                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        Intent intent = new Intent(getApplicationContext(), ChatRoomChatActivity.class);
                        intent.putExtra(String.valueOf("userId"), userid);
                        intent.putExtra(String.valueOf("chatroomName"), chatroomName);
                        startActivity(intent);
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(),String.valueOf(jsonObject),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private OneOnOneActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OneOnOneActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}