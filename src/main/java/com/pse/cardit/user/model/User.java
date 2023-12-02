package com.pse.cardit.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Die Klasse stellt User Statistiken dar.
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
public class User implements IUser {
    /**
     * The constant MISSING_FEATURE_ERROR.
     */
    public static final String MISSING_FEATURE_ERROR = "Error, this method is part of the %s feature which is yet to "
            + "be implemented!";
    @Id
    @GeneratedValue
    private long userId;
    private String username;
    private String password;
    private UserType userType;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     */
    public User(String username) {
        this();
        this.username = username;
    }

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this(username);
        this.password = password;
        this.userType = UserType.REGISTERED;
    }

    /**
     * Instantiates a new User.
     */
    protected User() {
        this.password = "";
        this.userType = UserType.GUEST;
    }

    //TODO This constructor is only for testing. Can you ensure that?
    public User(int id) {
        this(id + "");
        this.userId = id;
    }


    @Override
    public long getUserId() {
        return this.userId;
    }

    @Override
    public int getImageId() {
        //TODO MISSING_FEATURE_ERROR Shop
        return 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + this.getUserId()
                + ", username='" + username + '\''
                + ", password='" + password + '\''
                + ", userType=" + userType + System.lineSeparator()
                + '}';
    }
}
