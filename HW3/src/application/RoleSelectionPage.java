package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import databasePart1.*;

/**
 * RoleSelectionPage allows users with multiple roles to choose which role to play.
 * Inspired by LinkedIn's clean, professional interface design.
 */
public class RoleSelectionPage {
    
    private final DatabaseHelper databaseHelper;
    private final User user;

    public RoleSelectionPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 30; -fx-background-color: #f3f2ef;");
        
        // Header section - LinkedIn inspired
        Label titleLabel = new Label("Welcome, " + user.getFullName());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Label subtitleLabel = new Label("Select your role to continue");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-padding: 0 0 20 0;");
        
        // Role selection section
        VBox roleButtonsContainer = new VBox(15);
        roleButtonsContainer.setAlignment(Pos.CENTER);
        roleButtonsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 1); " +
                                     "-fx-padding: 30;");
        
        // Create role buttons
        for (String role : user.getRoles()) {
            Button roleButton = createRoleButton(role, primaryStage);
            roleButtonsContainer.getChildren().add(roleButton);
        }
        
        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0a66c2; " +
                             "-fx-font-size: 14px; -fx-cursor: hand; -fx-underline: true; " +
                             "-fx-border-color: transparent; -fx-padding: 10;");
        logoutButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, subtitleLabel, roleButtonsContainer, logoutButton);
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Role Selection");
    }
    
    private Button createRoleButton(String role, Stage primaryStage) {
        Button button = new Button(capitalizeRole(role));
        button.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                       "-fx-font-size: 16px; -fx-font-weight: bold; " +
                       "-fx-background-radius: 24; -fx-padding: 12 24; " +
                       "-fx-cursor: hand; -fx-min-width: 200;");
        
        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: #004182; -fx-text-fill: white; " +
                           "-fx-font-size: 16px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 24; -fx-padding: 12 24; " +
                           "-fx-cursor: hand; -fx-min-width: 200;")
        );
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                           "-fx-font-size: 16px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 24; -fx-padding: 12 24; " +
                           "-fx-cursor: hand; -fx-min-width: 200;")
        );
        
        button.setOnAction(e -> navigateToRolePage(role, primaryStage));
        
        return button;
    }
    
    private void navigateToRolePage(String role, Stage primaryStage) {
        switch (role.toLowerCase()) {
            case "admin":
                new AdminHomePage().show(primaryStage);
                break;
            case "student":
                new UserHomePage().show(primaryStage);
                break;
            case "instructor":
                // TODO: Implement InstructorHomePage in later phases
                new UserHomePage().show(primaryStage);
                break;
            case "staff":
                // TODO: Implement StaffHomePage in later phases
                new UserHomePage().show(primaryStage);
                break;
            case "reviewer":
                // TODO: Implement ReviewerHomePage in later phases
                new UserHomePage().show(primaryStage);
                break;
            default:
                new UserHomePage().show(primaryStage);
                break;
        }
    }
    
    private String capitalizeRole(String role) {
        if (role == null || role.isEmpty()) return role;
        return role.substring(0, 1).toUpperCase() + role.substring(1);
    }
}