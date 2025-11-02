package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;
import application.StudentDashboard;

public class UserHomePage {
    
    private DatabaseHelper databaseHelper;
    private User currentUser;

    public UserHomePage() {
    }
    
    public UserHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        System.out.println("=== UserHomePage.show() called ===");
        
        if (currentUser == null) {
            currentUser = (User) primaryStage.getProperties().get("currentUser");
            System.out.println("Loaded currentUser from stage properties: " + currentUser);
        }
        if (databaseHelper == null) {
            databaseHelper = (DatabaseHelper) primaryStage.getProperties().get("databaseHelper");
            System.out.println("Loaded databaseHelper from stage properties: " + databaseHelper);
        }
        
        System.out.println("Current user: " + (currentUser != null ? currentUser.getUserName() : "null"));
        if (currentUser != null) {
            System.out.println("User roles: " + currentUser.getRolesAsString());
            System.out.println("Has student role? " + currentUser.hasRole("student"));
        }
        
        if (currentUser != null && currentUser.hasRole("student")) {
            System.out.println("REDIRECTING TO STUDENT DASHBOARD");
            new StudentDashboard(databaseHelper, currentUser).show(primaryStage);
            return;
        }
        
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: #f3f2ef;");
        
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label infoLabel = new Label("Role-specific features coming soon!");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        layout.getChildren().addAll(userLabel, infoLabel);
        Scene userScene = new Scene(layout, 800, 400);

        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}