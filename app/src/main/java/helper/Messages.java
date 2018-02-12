package helper;

/**
 * Created by franc on 10/02/2018.
 */

public class Messages {

    private String userId;
    private String message;
    private String created;

    public Messages(String userId, String message, String created) {
        this.userId = userId;
        this.message = message;
        this.created = created;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
