package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.sql.SQLException;

import databasePart1.*;

/**
 * UserProfilePage allows users to update their personal information including
 * first name, last name, email, and password. Accessible to all logged-in users.
 */
public class UserProfilePage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private final Scene previousScene;
    
    public UserProfilePage(DatabaseHelper databaseHelper, User currentUser, Stage previousStage) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        this.previousScene = previousStage.getScene(); // Store the current scene before we change it
    }

    public void show(Stage primaryStage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f3f2ef;");
        
        // Header
        VBox header = createHeader(primaryStage);
        layout.setTop(header);
        
        // Main content
        VBox mainContent = createMainContent(primaryStage);
        layout.setCenter(mainContent);
        
        Scene scene = new Scene(layout, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Settings - " + currentUser.getUserName());
    }
    
    private VBox createHeader(Stage primaryStage) {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Account Settings");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 8 16;");
        backButton.setOnAction(e -> {
            // Return to the previous scene
            primaryStage.setScene(previousScene);
        });
        
        headerContent.getChildren().addAll(titleLabel, spacer, backButton);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox createMainContent(Stage primaryStage) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        
        // Personal Information Section
        VBox personalInfoSection = createPersonalInfoSection();
        
        // Password Change Section
        VBox passwordSection = createPasswordSection();
        
        mainContent.getChildren().addAll(personalInfoSection, passwordSection);
        return mainContent;
    }
    
    private VBox createPersonalInfoSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 25; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label sectionTitle = new Label("Personal Information");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 0, 0, 0));
        
        // Username (read-only)
        formGrid.add(new Label("Username:"), 0, 0);
        Label usernameLabel = new Label(currentUser.getUserName());
        usernameLabel.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 8; " +
                              "-fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        formGrid.add(usernameLabel, 1, 0);
        
        // First Name
        formGrid.add(new Label("First Name:"), 0, 1);
        TextField firstNameField = new TextField(currentUser.getFirstName());
        firstNameField.setPromptText("Enter first name");
        firstNameField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        formGrid.add(firstNameField, 1, 1);
        
        // Last Name
        formGrid.add(new Label("Last Name:"), 0, 2);
        TextField lastNameField = new TextField(currentUser.getLastName());
        lastNameField.setPromptText("Enter last name");
        lastNameField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        formGrid.add(lastNameField, 1, 2);
        
        // Email
        formGrid.add(new Label("Email:"), 0, 3);
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Enter email address");
        emailField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        formGrid.add(emailField, 1, 3);
        
        // Validation labels
        Label personalInfoError = new Label();
        personalInfoError.setTextFill(Color.RED);
        personalInfoError.setWrapText(true);
        personalInfoError.setMaxWidth(400);
        
        // Email validation
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty() && !isValidEmail(newValue.trim())) {
                personalInfoError.setText("Please enter a valid email address");
                personalInfoError.setTextFill(Color.RED);
            } else {
                personalInfoError.setText("");
            }
        });
        
        // Save button for personal info
        Button savePersonalInfoButton = new Button("Save Personal Information");
        savePersonalInfoButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                                       "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        savePersonalInfoButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            
            personalInfoError.setText("");
            
            // Validate email if provided
            if (!email.isEmpty() && !isValidEmail(email)) {
                personalInfoError.setText("Please enter a valid email address");
                personalInfoError.setTextFill(Color.RED);
                return;
            }
            
            try {
                // Update user object
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setEmail(email);
                
                // Save to database
                databaseHelper.updateUser(currentUser);
                
                personalInfoError.setText("Personal information updated successfully!");
                personalInfoError.setTextFill(Color.GREEN);
                
            } catch (SQLException ex) {
                personalInfoError.setText("Error updating information: " + ex.getMessage());
                personalInfoError.setTextFill(Color.RED);
                ex.printStackTrace();
            }
        });
        
        section.getChildren().addAll(sectionTitle, formGrid, personalInfoError, savePersonalInfoButton);
        return section;
    }
    
    private VBox createPasswordSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 25; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label sectionTitle = new Label("Change Password");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        GridPane passwordGrid = new GridPane();
        passwordGrid.setHgap(15);
        passwordGrid.setVgap(15);
        passwordGrid.setPadding(new Insets(20, 0, 0, 0));
        
        // Current Password
        passwordGrid.add(new Label("Current Password:"), 0, 0);
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        currentPasswordField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        passwordGrid.add(currentPasswordField, 1, 0);
        
        // New Password
        passwordGrid.add(new Label("New Password:"), 0, 1);
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        passwordGrid.add(newPasswordField, 1, 1);
        
        // Confirm New Password
        passwordGrid.add(new Label("Confirm Password:"), 0, 2);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.setStyle("-fx-padding: 8; -fx-border-color: #cccccc; -fx-border-radius: 4; -fx-min-width: 200;");
        passwordGrid.add(confirmPasswordField, 1, 2);
        
        // Password validation labels
        Label passwordError = new Label();
        passwordError.setTextFill(Color.RED);
        passwordError.setWrapText(true);
        passwordError.setMaxWidth(500);
        
        Label passwordStatus = new Label();
        passwordStatus.setWrapText(true);
        passwordStatus.setMaxWidth(500);
        
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
        
        // Change password button
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; " +
                                     "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        changePasswordButton.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            passwordError.setText("");
            
            // Validate current password
            if (currentPassword.isEmpty()) {
                passwordError.setText("Please enter your current password");
                return;
            }
            
            if (!currentPassword.equals(currentUser.getPassword())) {
                passwordError.setText("Current password is incorrect");
                return;
            }
            
            // Validate new password
            String passwordValidation = PasswordRecognizer.checkForValidPassword(newPassword);
            if (!passwordValidation.isEmpty()) {
                passwordError.setText(passwordValidation);
                return;
            }
            
            // Check password confirmation
            if (!newPassword.equals(confirmPassword)) {
                passwordError.setText("New passwords do not match");
                return;
            }
            
            // Check if new password is different from current
            if (newPassword.equals(currentPassword)) {
                passwordError.setText("New password must be different from current password");
                return;
            }
            
            try {
                // Update password in database
                databaseHelper.updatePassword(currentUser.getUserName(), newPassword);
                
                // Update user object
                currentUser.setPassword(newPassword);
                
                // Clear fields
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                passwordStatus.setText("");
                
                passwordError.setText("Password changed successfully!");
                passwordError.setTextFill(Color.GREEN);
                
            } catch (SQLException ex) {
                passwordError.setText("Error changing password: " + ex.getMessage());
                passwordError.setTextFill(Color.RED);
                ex.printStackTrace();
            }
        });
        
        section.getChildren().addAll(sectionTitle, passwordGrid, passwordStatus, passwordError, changePasswordButton);
        return section;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}