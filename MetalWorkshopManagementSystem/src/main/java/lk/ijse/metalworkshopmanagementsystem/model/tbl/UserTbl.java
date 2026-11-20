package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import lk.ijse.metalworkshopmanagementsystem.model.Role;
import lk.ijse.metalworkshopmanagementsystem.model.UserStatus;

public class UserTbl {
    
    private String userId;
    private String username;
    private String fullName;
    private Role role;
    private UserStatus status;

    public UserTbl() {
    }

    public UserTbl(String userId, String username, String fullName, Role role, UserStatus status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus isStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserTbl{" + "userId=" + userId + ", username=" + username + ", fullName=" + fullName + ", role=" + role + ", Status=" + status + '}';
    }
}
