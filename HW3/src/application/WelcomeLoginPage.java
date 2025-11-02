package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import databasePart1.*;

/**
 * Enhanced WelcomeLoginPage that handles users with single or multiple roles
 * according to the user stories requirements:
 * - Single role users go directly to their role's page
 * - Multiple role users go to role selection
 * - No role users go to "No Roles Assigned" page
 * - Handles password change requirements
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    	// Check if user needs to change password (one-time password used)
        if (user.getMustChangePassword() || user.getOneTimePassword() != null) {
            showPasswordChangeScreen(primaryStage, user);
            return;
        }
        
        // Store user and database helper in stage properties for access by other pages
        primaryStage.getProperties().put("currentUser", user);
        primaryStage.getProperties().put("databaseHelper", databaseHelper);
        
        // Handle navigation based on number of roles (as per user stories)
        if (user.getRoles().isEmpty()) {
            // No roles assigned - show "No Roles Assigned" page
            showNoRolesScreen(primaryStage, user);
        } else if (user.hasMultipleRoles()) {
            // Multiple roles - show role selection screen
            new RoleSelectionPage(databaseHelper, user).show(primaryStage);
        } else {
            // Single role - navigate directly to that role's home page
            String role = user.getRoles().iterator().next();
            System.out.println("User " + user.getUserName() + " has single role: " + role + " - navigating directly");
            navigateToSingleRolePage(primaryStage, role, user);
        }
    }
    
    private void navigateToSingleRolePage(Stage primaryStage, String role, User user) {
        switch (role.toLowerCase()) {
            case "admin":
                // If user is admin and it's their only role, go directly to admin dashboard
                new AdminHomePage().show(primaryStage, databaseHelper, user);
                break;
            case "student":
                new StudentDashboard(databaseHelper, user).show(primaryStage);
                break;
            case "instructor":
                showSingleRoleWelcomePage(primaryStage, "Instructor", user);
                break;
            case "staff":
                showSingleRoleWelcomePage(primaryStage, "Staff", user);
                break;
            case "reviewer":
                showSingleRoleWelcomePage(primaryStage, "Reviewer", user);
                break;
            default:
                showSingleRoleWelcomePage(primaryStage, role, user);
                break;
        }
    }
    
    private void showSingleRoleWelcomePage(Stage primaryStage, String roleName, User user) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 30; -fx-background-color: #f3f2ef;");
        
        // Capitalize role name for display
        String displayRoleName = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
        
        Label welcomeLabel = new Label("Hello, " + displayRoleName + "!");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0a66c2;");
        
        String displayName = user.getFullName().isEmpty() ? user.getUserName() : user.getFullName();
        Label userLabel = new Label("Welcome, " + displayName);
        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
        
        Label roleLabel = new Label("You are logged in as: " + displayRoleName);
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        // Future functionality note
        Label futureNote = new Label("Additional " + displayRoleName.toLowerCase() + " features will be available in future updates.");
        futureNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999; -fx-font-style: italic;");
        
        // Button container
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button accountSettingsButton = new Button("Account Settings");
        accountSettingsButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                                      "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        accountSettingsButton.setOnAction(e -> {
            new UserProfilePage(databaseHelper, user, primaryStage).show(primaryStage);
        });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        logoutButton.setOnAction(e -> {
            primaryStage.getProperties().remove("currentUser");
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        buttonContainer.getChildren().addAll(accountSettingsButton, logoutButton);
        
        layout.getChildren().addAll(welcomeLabel, userLabel, roleLabel, futureNote, buttonContainer);
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle(displayRoleName + " Dashboard");
    }
    
    private void showPasswordChangeScreen(Stage primaryStage, User user) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 30; -fx-background-color: #f3f2ef;");
        
        Label titleLabel = new Label("Password Change Required");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
        
        Label instructionLabel = new Label("You must change your password before continuing.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setMaxWidth(300);
        newPasswordField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.setMaxWidth(300);
        confirmPasswordField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(400);
        
        // Password status label
        Label passwordStatus = new Label();
        passwordStatus.setWrapText(true);
        passwordStatus.setMaxWidth(400);
        
        // Real-time password validation
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                String error = PasswordRecognizer.checkForValidPassword(newValue);
                
                StringBuilder status = new StringBuilder("Password Requirements:\n");
                status.append(PasswordRecognizer.foundUpperCase ? "✓" : "✗").append(" Uppercase letter\n");
                status.append(PasswordRecognizer.foundLowerCase ? "✓" : "✗").append(" Lowercase letter\n");
                status.append(PasswordRecognizer.foundNumericDigit ? "✓" : "✗").append(" Numeric digit\n");
                status.append(PasswordRecognizer.foundSpecialChar ? "✓" : "✗").append(" Special character\n");
                status.append(PasswordRecognizer.foundLongEnough ? "✓" : "✗").append(" At least 8 characters");
                
                passwordStatus.setText(status.toString());
                
                if (!error.isEmpty()) {
                    passwordStatus.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
                } else {
                    passwordStatus.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px;");
                }
            } else {
                passwordStatus.setText("");
            }
        });
        
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                                     "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        changePasswordButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            errorLabel.setText("");
            
            // Validate new password
            String passwordValidation = PasswordRecognizer.checkForValidPassword(newPassword);
            if (!passwordValidation.isEmpty()) {
                errorLabel.setText(passwordValidation);
                return;
            }
            
            // Check password confirmation
            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                return;
            }
            
            try {
                databaseHelper.updatePassword(user.getUserName(), newPassword);
                user.setPassword(newPassword);
                user.clearOneTimePassword();
                
                // Show success and continue to role-based navigation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password updated successfully!");
                alert.showAndWait();
                
                // Continue to main application with updated user
                show(primaryStage, user);
                
            } catch (Exception ex) {
                errorLabel.setText("Error updating password: " + ex.getMessage());
            }
        });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; " +
                             "-fx-font-size: 12px; -fx-underline: true;");
        logoutButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 30; " +
                              "-fx-background-radius: 8; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1);");
        formContainer.setMaxWidth(500);
        formContainer.getChildren().addAll(newPasswordField, confirmPasswordField, passwordStatus, 
                                          errorLabel, changePasswordButton);
        
        layout.getChildren().addAll(titleLabel, instructionLabel, formContainer, logoutButton);
        
        Scene scene = new Scene(layout, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Password Change Required");
    }
    
    private void showNoRolesScreen(Stage primaryStage, User user) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 30; -fx-background-color: #f3f2ef;");
        
        Label titleLabel = new Label("No Roles Assigned");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
        
        String displayName = user.getFullName().isEmpty() ? user.getUserName() : user.getFullName();
        Label welcomeLabel = new Label("Welcome, " + displayName);
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
        
        Label messageLabel = new Label("Your account has no roles assigned.");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        
        Label instructionLabel = new Label("Please contact an administrator to assign roles to your account.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        // Button container
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button accountSettingsButton = new Button("Account Settings");
        accountSettingsButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                                      "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        accountSettingsButton.setOnAction(e -> {
            new UserProfilePage(databaseHelper, user, primaryStage).show(primaryStage);
        });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        logoutButton.setOnAction(e -> {
            primaryStage.getProperties().remove("currentUser");
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        buttonContainer.getChildren().addAll(accountSettingsButton, logoutButton);
        
        // Container for the message
        VBox messageContainer = new VBox(15);
        messageContainer.setStyle("-fx-background-color: white; -fx-padding: 30; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1);");
        messageContainer.setMaxWidth(500);
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.getChildren().addAll(welcomeLabel, messageLabel, instructionLabel);
        
        layout.getChildren().addAll(titleLabel, messageContainer, buttonContainer);
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("No Access");
    }
}