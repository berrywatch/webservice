package neu.coe.csye6225.Registration.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)  // ignore fields that doesn't exist
public class User {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount_created() {
        return account_created;
    }

    public void setAccount_created(String account_created) {
        this.account_created = account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(String account_updated) {
        this.account_updated = account_updated;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + username + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", password='" + password + '\'' +
                ", account_created='" + account_created + '\'' +
                ", account_updated='" + account_updated + '\'' +
                '}';
    }

    @Id
    private String username;
    private String first_name;
    private String last_name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(updatable = false)
    private String account_created;

    @Column()
    private String account_updated;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(updatable = false)
    private String uuid;
}

