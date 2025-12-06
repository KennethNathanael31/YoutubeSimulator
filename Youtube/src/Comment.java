import java.util.ArrayList;
import java.util.List;

public class Comment {
    private User user;
    private String comment;

    public Comment(User user, String comment) {
        this.user = user;
        this.comment = comment;
    }

    public User getUser() {
        return this.user;
    }

    public String getComment() {
        return this.comment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toString() {
        String ret = String.format("%s : %s", user.namaPengguna, comment);
        return ret;
    }
}
