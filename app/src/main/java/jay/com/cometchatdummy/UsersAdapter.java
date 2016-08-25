package jay.com.cometchatdummy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jay on 23-04-2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private List<User> usersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, username, status;

        public MyViewHolder(View view) {
            super(view);
            //id = (TextView) view.findViewById(R.id.id);
            username = (TextView) view.findViewById(R.id.username);
            status = (TextView) view.findViewById(R.id.status);
        }
    }


    public UsersAdapter(List<User> moviesList) {
        this.usersList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = usersList.get(position);
        //holder.id.setText(user.getId());
        holder.username.setText(user.getUsername());
        holder.status.setText(user.getStatus());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}