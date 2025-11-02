package application;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import java.util.*;
import java.sql.SQLException;
import databasePart1.DatabaseHelper;

/**
 * UserEditDialog allows admins to edit user information and manage roles.
 * Features clean, professional interface inspired by LinkedIn's design patterns.
 * Proper loading and saving of user information including firstName, lastName, and email.
 */
public class UserEditDialog extends Dialog<User> {
    
    private final User originalUser;
    private final DatabaseHelper databaseHelper;
    private final User currentAdmin;
    
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private CheckBox adminCheckBox;
    private CheckBox studentCheckBox;
    private CheckBox instructorCheckBox;
    private CheckBox staffCheckBox;
    private CheckBox reviewerCheckBox;
    
    private Label errorLabel;

    public UserEditDialog(User user, DatabaseHelper databaseHelper, User currentAdmin) {
        this.originalUser = user;
        this.databaseHelper = databaseHelper;
        this.currentAdmin = currentAdmin;
        
        setTitle("Edit User - " + user.getUserName());
        setHeaderText("Edit user information and roles");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the content
        GridPane grid = createFormContent();
        getDialogPane().setContent(grid);
        
        // Enable/disable save button based on validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume(); // Prevent dialog from closing
            }
        });
        
        // Convert the result when save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createUpdatedUser();
            }
            return null;
        });
        
        // Load current values
        loadUserData();
    }
    
    private GridPane createFormContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.setStyle("-fx-background-color: white;");
        
        // User information section
        Label infoLabel = new Label("User Information");
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        grid.add(infoLabel, 0, 0, 2, 1);
        
        // Username (read-only)
        grid.add(new Label("Username:"), 0, 1);
        Label usernameLabel = new Label(originalUser.getUserName());
        usernameLabel.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4;");
        grid.add(usernameLabel, 1, 1);
        
        // First name
        grid.add(new Label("First Name:"), 0, 2);
        firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        firstNameField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4;");
        grid.add(firstNameField, 1, 2);
        
        // Last name
        grid.add(new Label("Last Name:"), 0, 3);
        lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        lastNameField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4;");
        grid.add(lastNameField, 1, 3);
        
        // Email
        grid.add(new Label("Email:"), 0, 4);
        emailField = new TextField();
        emailField.setPromptText("Enter email address");
        emailField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4;");
        grid.add(emailField, 1, 4);
        
        // Roles section
        Label rolesLabel = new Label("User Roles");
        rolesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-padding: 20 0 0 0;");
        grid.add(rolesLabel, 0, 5, 2, 1);
        
        // Role checkboxes
        VBox rolesBox = new VBox(8);
        rolesBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");
        
        adminCheckBox = new CheckBox("Administrator");
        studentCheckBox = new CheckBox("Student");
        instructorCheckBox = new CheckBox("Instructor");
        staffCheckBox = new CheckBox("Staff");
        reviewerCheckBox = new CheckBox("Reviewer");
        
        // Style checkboxes
        String checkboxStyle = "-fx-font-size: 14px; -fx-text-fill: #333333;";
        adminCheckBox.setStyle(checkboxStyle);
        studentCheckBox.setStyle(checkboxStyle);
        instructorCheckBox.setStyle(checkboxStyle);
        staffCheckBox.setStyle(checkboxStyle);
        reviewerCheckBox.setStyle(checkboxStyle);
        
        rolesBox.getChildren().addAll(adminCheckBox, studentCheckBox, instructorCheckBox, staffCheckBox, reviewerCheckBox);
        grid.add(rolesBox, 0, 6, 2, 1);
        
        // Admin protection note
        if (originalUser.getUserName().equals(currentAdmin.getUserName())) {
            Label adminNote = new Label("Note: You cannot remove your own admin privileges");
            adminNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff6b35; -fx-font-style: italic;");
            grid.add(adminNote, 0, 7, 2, 1);
        }
        
        // Error label
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        grid.add(errorLabel, 0, 8, 2, 1);
        
        return grid;
    }
    
    private void loadUserData() {
        // Properly load user data, handling empty fields
        System.out.println("Loading user data for: " + originalUser.getUserName());
        System.out.println("Original firstName: '" + originalUser.getFirstName() + "'");
        System.out.println("Original lastName: '" + originalUser.getLastName() + "'");
        System.out.println("Original email: '" + originalUser.getEmail() + "'");
        
        // Load name fields - use empty string if null, and don't use username as fallback
        String firstName = originalUser.getFirstName();
        String lastName = originalUser.getLastName();
        String email = originalUser.getEmail();
        
        firstNameField.setText(firstName != null ? firstName : "");
        lastNameField.setText(lastName != null ? lastName : "");
        emailField.setText(email != null ? email : "");
        
        // Load roles
        Set<String> roles = originalUser.getRoles();
        adminCheckBox.setSelected(roles.contains("admin"));
        studentCheckBox.setSelected(roles.contains("student"));
        instructorCheckBox.setSelected(roles.contains("instructor"));
        staffCheckBox.setSelected(roles.contains("staff"));
        reviewerCheckBox.setSelected(roles.contains("reviewer"));
        
        // Disable admin checkbox for current admin if they're trying to edit themselves
        if (originalUser.getUserName().equals(currentAdmin.getUserName())) {
            adminCheckBox.setDisable(true);
        }
        
        System.out.println("Loaded into fields - firstName: '" + firstNameField.getText() + 
                          "', lastName: '" + lastNameField.getText() + 
                          "', email: '" + emailField.getText() + "'");
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        // Validate email format if provided
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            errors.append("Please enter a valid email address.\n");
        }
        
        // Validate at least one role is selected
        if (!adminCheckBox.isSelected() && !studentCheckBox.isSelected() && 
            !instructorCheckBox.isSelected() && !staffCheckBox.isSelected() && 
            !reviewerCheckBox.isSelected()) {
            errors.append("Please select at least one role.\n");
        }
        
        // Check if trying to remove admin role from last admin
        if (originalUser.hasRole("admin") && !adminCheckBox.isSelected()) {
            try {
                if (isLastAdmin()) {
                    errors.append("Cannot remove admin role - this is the last admin account.\n");
                }
            } catch (SQLException e) {
                errors.append("Error checking admin status.\n");
            }
        }
        
        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return false;
        } else {
            errorLabel.setText("");
            return true;
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
    
    private boolean isLastAdmin() throws SQLException {
        List<User> allUsers = databaseHelper.getAllUsers();
        long adminCount = allUsers.stream()
                .mapToLong(user -> user.hasRole("admin") ? 1 : 0)
                .sum();
        return adminCount <= 1;
    }
    
    private User createUpdatedUser() {
        Set<String> roles = new HashSet<>();
        
        if (adminCheckBox.isSelected()) roles.add("admin");
        if (studentCheckBox.isSelected()) roles.add("student");
        if (instructorCheckBox.isSelected()) roles.add("instructor");
        if (staffCheckBox.isSelected()) roles.add("staff");
        if (reviewerCheckBox.isSelected()) roles.add("reviewer");
        
        // Properly get the trimmed values from text fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        System.out.println("Creating updated user with:");
        System.out.println("  firstName: '" + firstName + "'");
        System.out.println("  lastName: '" + lastName + "'");
        System.out.println("  email: '" + email + "'");
        
        User updatedUser = new User(
            originalUser.getUserName(),
            originalUser.getPassword(),
            firstName,
            lastName, 
            email,
            roles
        );
        
        // Preserve password-related fields
        updatedUser.setOneTimePassword(originalUser.getOneTimePassword());
        updatedUser.setMustChangePassword(originalUser.getMustChangePassword());
        
        return updatedUser;
    }
}