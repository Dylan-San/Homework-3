package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 * Modified to include username and password validation using FSM recognizers.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        userNameField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        inviteCodeField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        // Labels for validation feedback
        Label userNameError = new Label();
        userNameError.setTextFill(Color.RED);
        userNameError.setWrapText(true);
        userNameError.setMaxWidth(400);
        
        Label passwordError = new Label();
        passwordError.setTextFill(Color.RED);
        passwordError.setWrapText(true);
        passwordError.setMaxWidth(400);
        
        // Label to show password requirements status
        Label passwordStatus = new Label();
        passwordStatus.setWrapText(true);
        passwordStatus.setMaxWidth(400);
        
        // Label to display general error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Add real-time validation for username
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                String error = UserNameRecognizer.checkForValidUserName(newValue);
                if (!error.isEmpty()) {
                    userNameError.setText(error);
                    userNameError.setTextFill(Color.RED);
                } else {
                    userNameError.setText("✓ Username is valid");
                    userNameError.setTextFill(Color.GREEN);
                }
            } else {
                userNameError.setText("");
            }
        });
        
        // Add real-time validation for password
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                String error = PasswordRecognizer.checkForValidPassword(newValue);
                
                // Build status message showing requirements
                StringBuilder status = new StringBuilder("Password Requirements:\n");
                status.append(PasswordRecognizer.foundUpperCase ? "✓" : "✗").append(" Uppercase letter\n");
                status.append(PasswordRecognizer.foundLowerCase ? "✓" : "✗").append(" Lowercase letter\n");
                status.append(PasswordRecognizer.foundNumericDigit ? "✓" : "✗").append(" Numeric digit\n");
                status.append(PasswordRecognizer.foundSpecialChar ? "✓" : "✗").append(" Special character\n");
                status.append(PasswordRecognizer.foundLongEnough ? "✓" : "✗").append(" At least 8 characters");
                
                passwordStatus.setText(status.toString());
                
                if (!error.isEmpty()) {
                    passwordError.setText(error);
                    passwordError.setTextFill(Color.RED);
                    passwordStatus.setTextFill(Color.DARKRED);
                } else {
                    passwordError.setText("✓ Password is strong");
                    passwordError.setTextFill(Color.GREEN);
                    passwordStatus.setTextFill(Color.DARKGREEN);
                }
            } else {
                passwordError.setText("");
                passwordStatus.setText("");
            }
        });
        

        Button setupButton = new Button("Setup");
        setupButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText().trim();
            String password = passwordField.getText();
            String code = inviteCodeField.getText().trim();
            
            // Clear previous general errors
            errorLabel.setText("");
            
            // Validate username using FSM
            String userNameValidation = UserNameRecognizer.checkForValidUserName(userName);
            if (!userNameValidation.isEmpty()) {
                userNameError.setText(userNameValidation);
                userNameError.setTextFill(Color.RED);
                return; // Don't proceed if username is invalid
            }
            
            // Validate password using FSM
            String passwordValidation = PasswordRecognizer.checkForValidPassword(password);
            if (!passwordValidation.isEmpty()) {
                passwordError.setText(passwordValidation);
                passwordError.setTextFill(Color.RED);
                return; // Don't proceed if password is invalid
            }
            
            // Validate invitation code is not empty
            if (code.isEmpty()) {
                errorLabel.setText("Please enter an invitation code");
                return;
            }
            
            try {
            	// Check if the user already exists
            	if(databaseHelper.doesUserExist(userName)) {
            		errorLabel.setText("This username is taken! Please use another to setup an account");
            		return;
            	}
            	
            	// Validate the invitation code FIRST
            	if(!databaseHelper.validateInvitationCode(code)) {
            		errorLabel.setText("Please enter a valid invitation code");
            		return;
            	}
            	
            	// If we get here, everything is valid, so proceed with registration
        		// Create a new user and register them in the database
            	User user = new User(userName, password, "student"); // Default role for new users
                databaseHelper.register(user);
                
                // IMPORTANT: Mark the invitation code as used AFTER successful registration
                databaseHelper.useInvitationCode(code, userName);
                
                // Store user and database helper in stage properties
                primaryStage.getProperties().put("currentUser", user);
                primaryStage.getProperties().put("databaseHelper", databaseHelper);
                
                // Navigate to the Welcome Login Page
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
                
                // Show user-friendly error message instead of technical SQL error
                String userFriendlyMessage = "Failed to create account. ";
                
                if (e.getMessage().contains("DUPLICATE") || e.getMessage().contains("duplicate")) {
                    userFriendlyMessage += "The username is already taken.";
                } else if (e.getMessage().contains("constraint") || e.getMessage().contains("violation")) {
                    userFriendlyMessage += "Please check your input and try again.";
                } else {
                    userFriendlyMessage += "Please try again or contact support.";
                }
                
                errorLabel.setText(userFriendlyMessage);
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                errorLabel.setText("An unexpected error occurred. Please try again.");
            }
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #f3f2ef;");
        
        // Title
        Label titleLabel = new Label("User Account Setup");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label subtitleLabel = new Label("Create your account with a valid invitation code");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 30; " +
                              "-fx-background-radius: 8; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1);");
        formContainer.setMaxWidth(500);
        
        // Add all components including validation labels
        formContainer.getChildren().addAll(
            userNameField, 
            userNameError,
            passwordField, 
            passwordError,
            passwordStatus,
            inviteCodeField, 
            setupButton, 
            errorLabel
        );
        
        layout.getChildren().addAll(titleLabel, subtitleLabel, formContainer);

        primaryStage.setScene(new Scene(layout, 800, 700));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}