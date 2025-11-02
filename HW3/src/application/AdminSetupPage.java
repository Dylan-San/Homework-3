package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The AdminSetupPage class handles the setup process for creating the first administrator account.
 * This is used by the first user to initialize the system with admin credentials.
 * Enhanced to collect username, password, and email as required account information.
 * Forces user to log in again after account creation as per user stories.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and email
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin username");
        userNameField.setMaxWidth(300);
        userNameField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(300);
        emailField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        // Add labels for validation feedback
        Label userNameError = new Label();
        userNameError.setTextFill(Color.RED);
        userNameError.setWrapText(true);
        userNameError.setMaxWidth(450);
        
        Label passwordError = new Label();
        passwordError.setTextFill(Color.RED);
        passwordError.setWrapText(true);
        passwordError.setMaxWidth(450);
        
        Label emailError = new Label();
        emailError.setTextFill(Color.RED);
        emailError.setWrapText(true);
        emailError.setMaxWidth(450);
        
        // Label to show password requirements status
        Label passwordStatus = new Label();
        passwordStatus.setWrapText(true);
        passwordStatus.setMaxWidth(450);

        Button setupButton = new Button("Create Administrator Account");
        setupButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
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
        
        // Add real-time validation for email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                if (isValidEmail(newValue.trim())) {
                    emailError.setText("✓ Email is valid");
                    emailError.setTextFill(Color.GREEN);
                } else {
                    emailError.setText("Please enter a valid email address");
                    emailError.setTextFill(Color.RED);
                }
            } else {
                emailError.setText("");
            }
        });
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText().trim();
            String password = passwordField.getText();
            String email = emailField.getText().trim();
            
            // Clear previous errors
            passwordError.setText("");
            userNameError.setText("");
            emailError.setText("");
            
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
            
            // Validate email
            if (email.isEmpty()) {
                emailError.setText("Email address is required");
                emailError.setTextFill(Color.RED);
                return;
            }
            
            if (!isValidEmail(email)) {
                emailError.setText("Please enter a valid email address");
                emailError.setTextFill(Color.RED);
                return;
            }
            
            try {
            	// Check if user already exists
            	if (databaseHelper.doesUserExist(userName)) {
            		userNameError.setText("This username is already taken. Please choose a different one.");
            		userNameError.setTextFill(Color.RED);
            		return;
            	}
            	
            	// Create a new User object with admin role and all account information
            	User user = new User(userName, password, "", "", email, java.util.Set.of("admin"));
                
                // Register the user in the database
                databaseHelper.register(user);
                
                System.out.println("Administrator setup completed successfully for: " + userName);
                
                // Show success message and explain next step
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Administrator Account Created");
                successAlert.setHeaderText("Account setup completed successfully!");
                successAlert.setContentText("Your administrator account has been created.\n\n" +
                                           "You will now be taken to the login page to sign in with your new credentials.");
                successAlert.showAndWait();
                
                // FORCE RE-LOGIN: Navigate to the login selection page as required by user stories
                new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
                
            } catch (SQLException e) {
                System.err.println("Database error during admin setup: " + e.getMessage());
                e.printStackTrace();
                
                // Show user-friendly error message instead of technical SQL error
                String userFriendlyMessage = "Failed to create administrator account. ";
                
                if (e.getMessage().contains("DUPLICATE") || e.getMessage().contains("duplicate")) {
                    userFriendlyMessage += "The username is already taken.";
                    userNameError.setText(userFriendlyMessage);
                    userNameError.setTextFill(Color.RED);
                } else if (e.getMessage().contains("constraint") || e.getMessage().contains("violation")) {
                    userFriendlyMessage += "Please check your input and try again.";
                    emailError.setText(userFriendlyMessage);
                    emailError.setTextFill(Color.RED);
                } else {
                    userFriendlyMessage += "Please try again or contact support.";
                    emailError.setText(userFriendlyMessage);
                    emailError.setTextFill(Color.RED);
                }
            } catch (Exception e) {
                System.err.println("Unexpected error during admin setup: " + e.getMessage());
                e.printStackTrace();
                
                emailError.setText("An unexpected error occurred. Please try again.");
                emailError.setTextFill(Color.RED);
            }
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #f3f2ef;");
        
        // Title
        Label titleLabel = new Label("First User Setup");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label subtitleLabel = new Label("Create the first administrator account for this system");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        Label instructionLabel = new Label("Please provide your account information below:");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-padding: 10 0 0 0;");
        
        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 30; " +
                              "-fx-background-radius: 8; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1);");
        formContainer.setMaxWidth(500);
        
        // Add all components to layout
        formContainer.getChildren().addAll(
            new Label("Username:"),
            userNameField, 
            userNameError,
            new Label("Password:"),
            passwordField, 
            passwordError,
            passwordStatus,
            new Label("Email Address:"),
            emailField,
            emailError,
            setupButton
        );
        
        layout.getChildren().addAll(titleLabel, subtitleLabel, instructionLabel, formContainer);

        primaryStage.setScene(new Scene(layout, 800, 800));
        primaryStage.setTitle("First User Setup");
        primaryStage.show();
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}