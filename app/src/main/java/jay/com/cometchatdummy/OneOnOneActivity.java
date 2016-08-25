package jay.com.cometchatdummy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.inscripts.callbacks.Callbacks;
import com.inscripts.callbacks.SubscribeCallbacks;
import com.inscripts.cometchat.sdk.CometChat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OneOnOneActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_on_one);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        usersAdapter = new UsersAdapter(userList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(usersAdapter);

        setTitle("One on One");
        CometChat cometChat = CometChat.getInstance(getApplicationContext(), String.valueOf("10415x77177883eedf5255554e825180e563c1"));

        cometChat.getOnlineUsers(new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                try {
                    if (jsonObject != null) {
                        Iterator<String> it = jsonObject.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            try {
                                JSONObject object = jsonObject.getJSONObject(key);
                                String id = object.getString("id");
                                String name = object.getString("n");
                                String link = object.getString("l");
                                String status = object.getString("s");
                                userList.add(new User(id,name,status));
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
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject),Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                User user = userList.get(position);
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra(String.valueOf("userId"),user.getId());
                intent.putExtra(String.valueOf("userName"),user.getUsername());
                startActivity(intent);
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
