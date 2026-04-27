package progresapp.model;

import java.io.Serializable;

public class Administrator implements Serializable {
    private String id;
    private String fullName;
    private String password;

    public Administrator(String id, String fullName, String password) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
