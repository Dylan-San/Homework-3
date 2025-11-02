package application;

import java.util.*;

/**
 * The User class represents a user entity in the system.
 * Enhanced to support multiple roles, name, and email information.
 * It allows Proper handling of empty name fields and display logic.
 */
public class User {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private String oneTimePassword;
    private boolean mustChangePassword;

    // Constructor for basic user creation
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.roles = new HashSet<>();
        if (role != null && !role.trim().isEmpty()) {
            this.roles.add(role.toLowerCase());
        }
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.oneTimePassword = null;
        this.mustChangePassword = false;
    }
    
    // Enhanced constructor with all user information
    public User(String userName, String password, String firstName, String lastName, String email, Set<String> roles) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName != null ? firstName : "";
        this.lastName = lastName != null ? lastName : "";
        this.email = email != null ? email : "";
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.oneTimePassword = null;
        this.mustChangePassword = false;
    }

    // Getters and Setters
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName != null ? firstName : ""; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName != null ? lastName : ""; }
    
    // Improved getFullName() method to handle empty fields properly
    public String getFullName() { 
        // Ensure firstName and lastName are not null
        String first = (firstName != null) ? firstName.trim() : "";
        String last = (lastName != null) ? lastName.trim() : "";
        
        // If both names are empty, return empty string (not username)
        if (first.isEmpty() && last.isEmpty()) {
            return "";
        }
        
        // If only first name exists
        if (!first.isEmpty() && last.isEmpty()) {
            return first;
        }
        
        // If only last name exists
        if (first.isEmpty() && !last.isEmpty()) {
            return last;
        }
        
        // If both names exist
        return first + " " + last;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email : ""; }
    
    public Set<String> getRoles() { return new HashSet<>(roles); }
    public void setRoles(Set<String> roles) { 
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>(); 
    }
    
    // Legacy method for compatibility
    public String getRole() {
        if (roles.isEmpty()) return "";
        return roles.iterator().next();
    }
    
    // Legacy method for compatibility
    public void setRole(String role) {
        this.roles.clear();
        if (role != null && !role.trim().isEmpty()) {
            this.roles.add(role.toLowerCase());
        }
    }
    
    public String getOneTimePassword() { return oneTimePassword; }
    public void setOneTimePassword(String oneTimePassword) { 
        this.oneTimePassword = oneTimePassword;
        this.mustChangePassword = (oneTimePassword != null);
    }
    
    public boolean getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { 
        this.mustChangePassword = mustChangePassword; 
    }

    // Role management methods
    public void addRole(String role) {
        if (role != null && !role.trim().isEmpty()) {
            this.roles.add(role.toLowerCase());
        }
    }
    
    public void removeRole(String role) {
        if (role != null) {
            this.roles.remove(role.toLowerCase());
        }
    }
    
    public boolean hasRole(String role) {
        return role != null && this.roles.contains(role.toLowerCase());
    }
    
    public boolean hasMultipleRoles() {
        return this.roles.size() > 1;
    }
    
    public String getRolesAsString() {
        if (roles.isEmpty()) return "";
        List<String> sortedRoles = new ArrayList<>(roles);
        Collections.sort(sortedRoles);
        return String.join(", ", sortedRoles);
    }
    
    // Clear one-time password after use
    public void clearOneTimePassword() {
        this.oneTimePassword = null;
        this.mustChangePassword = false;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}