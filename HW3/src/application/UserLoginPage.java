package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * Enhanced UserLoginPage that works with the new multiple role system
 * and integrates with the enhanced User and DatabaseHelper classes.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter username");
        userNameField.setMaxWidth(250);
        userNameField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(400);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        // Back button to go to setup/login selection
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; " +
                           "-fx-font-size: 12px; -fx-underline: true;");
        backButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        loginButton.setOnAction(e -> {
        	// Retrieve user inputs
            String userName = userNameField.getText().trim();
            String password = passwordField.getText();
            
            // Clear previous errors
            errorLabel.setText("");
            
            // Basic validation
            if (userName.isEmpty()) {
                errorLabel.setText("Please enter your username");
                return;
            }
            
            if (password.isEmpty()) {
                errorLabel.setText("Please enter your password");
                return;
            }
            
            try {
            	// Get full user information
            	User user = databaseHelper.getUser(userName);
            	
            	if (user == null) {
            		errorLabel.setText("User account doesn't exist");
            		return;
            	}
            	
            	// Check if user has any roles
            	if (user.getRoles().isEmpty()) {
            		errorLabel.setText("Your account has no roles assigned. Please contact an administrator.");
            		return;
            	}
            	
            	// Try to authenticate with regular password first
            	boolean authenticated = false;
            	if (user.getPassword() != null && user.getPassword().equals(password)) {
            		authenticated = true;
            	}
            	// If regular password fails, check one-time password
            	else if (user.getOneTimePassword() != null && user.getOneTimePassword().equals(password)) {
            		authenticated = true;
            	}
            	
            	if (authenticated) {
            		// Store user and database helper in stage properties for other pages to access
            		primaryStage.getProperties().put("currentUser", user);
            		primaryStage.getProperties().put("databaseHelper", databaseHelper);
            		
            		// Navigate to welcome page which will handle role selection
            		WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            		welcomeLoginPage.show(primaryStage, user);
            	} else {
            		errorLabel.setText("Invalid username or password");
            	}
            	
            } catch (SQLException ex) {
                System.err.println("Database error: " + ex.getMessage());
                errorLabel.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
            	System.err.println("Unexpected error: " + ex.getMessage());
                errorLabel.setText("An unexpected error occurred. Please try again.");
                ex.printStackTrace();
            }
        });
        
        // Enter key support for easier login
        passwordField.setOnAction(e -> loginButton.fire());
        userNameField.setOnAction(e -> passwordField.requestFocus());

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #f3f2ef;");
        
        // Title
        Label titleLabel = new Label("Sign In");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        // Login form container
        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 30; " +
                              "-fx-background-radius: 8; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1);");
        formContainer.setMaxWidth(400);
        formContainer.getChildren().addAll(userNameField, passwordField, errorLabel, loginButton);
        
        layout.getChildren().addAll(titleLabel, formContainer, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}