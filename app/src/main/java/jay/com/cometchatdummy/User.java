package jay.com.cometchatdummy;

/**
 * Created by Jay on 23-04-2016.
 */
public class User {
    private String id, username, status;

    public User() {
    }

    public User(String id, String username, String status) {
        this.id = id;
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}