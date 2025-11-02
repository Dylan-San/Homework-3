package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
import databasePart1.*;

/**
 * Enhanced AdminHomePage with comprehensive administrative functions
 * including user management and logout functionality.
 * Designed with LinkedIn-inspired professional interface.
 */
public class AdminHomePage {
    
    private DatabaseHelper databaseHelper;
    private User currentAdmin;
    
    public void show(Stage primaryStage) {
        // Try to get current admin info from the stage's user data
        this.databaseHelper = (DatabaseHelper) primaryStage.getProperties().get("databaseHelper");
        this.currentAdmin = (User) primaryStage.getProperties().get("currentUser");
        
        // If not available, create basic instances (fallback)
        if (this.databaseHelper == null) {
            this.databaseHelper = new DatabaseHelper();
        }
        
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f3f2ef;");
        
        // Header
        VBox header = createHeader(primaryStage);
        layout.setTop(header);
        
        // Main content
        VBox mainContent = createMainContent(primaryStage);
        layout.setCenter(mainContent);
        
        Scene adminScene = new Scene(layout, 1000, 700);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Dashboard");
    }
    
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User currentAdmin) {
        this.databaseHelper = databaseHelper;
        this.currentAdmin = currentAdmin;
        
        // Store in stage properties for access by other methods
        primaryStage.getProperties().put("databaseHelper", databaseHelper);
        primaryStage.getProperties().put("currentUser", currentAdmin);
        
        show(primaryStage);
    }
    
    private VBox createHeader(Stage primaryStage) {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        // Title and welcome message
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        String welcomeText = "Welcome, Administrator";
        if (currentAdmin != null && !currentAdmin.getFullName().equals(currentAdmin.getUserName())) {
            welcomeText = "Welcome, " + currentAdmin.getFullName();
        }
        Label welcomeLabel = new Label(welcomeText);
        welcomeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        titleBox.getChildren().addAll(titleLabel, welcomeLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Header actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        // Account Settings button
        Button accountButton = new Button("Account Settings");
        accountButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                              "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
        accountButton.setOnAction(e -> {
            if (currentAdmin != null) {
                new UserProfilePage(databaseHelper, currentAdmin, primaryStage).show(primaryStage);
            }
        });
        
        // Role switch button (if admin has multiple roles)
        if (currentAdmin != null && currentAdmin.hasMultipleRoles()) {
            Button switchRoleButton = new Button("Switch Role");
            switchRoleButton.setStyle("-fx-background-color: #42a5f5; -fx-text-fill: white; " +
                                     "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
            switchRoleButton.setOnAction(e -> {
                new RoleSelectionPage(databaseHelper, currentAdmin).show(primaryStage);
            });
            actions.getChildren().add(switchRoleButton);
        }
        
        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; " +
                             "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
        logoutButton.setOnAction(e -> {
            // Don't close the database connection - keep it open for other users
            // Only clear the current user session
            primaryStage.getProperties().remove("currentUser");
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        actions.getChildren().addAll(accountButton, logoutButton);
        
        headerContent.getChildren().addAll(titleBox, spacer, actions);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox createMainContent(Stage primaryStage) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        
        // Quick actions section
        VBox quickActions = createQuickActionsSection(primaryStage);
        
        // Statistics section removed as requested
        // VBox statistics = createStatisticsSection();
        
        mainContent.getChildren().addAll(quickActions);
        return mainContent;
    }
    
    private VBox createQuickActionsSection(Stage primaryStage) {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 25; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label sectionTitle = new Label("Administrative Functions");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Container for action cards - now only showing User Management
        VBox actionsContainer = new VBox(20);
        actionsContainer.setAlignment(Pos.CENTER);
        
        // User Management card - VISIBLE
        VBox userMgmtCard = createActionCard(
            "User Management",
            "Manage user accounts, roles, permissions, and generate invitation codes",
            "#0a66c2",
            e -> {
                if (currentAdmin != null) {
                    new AdminUserManagementPage(databaseHelper, currentAdmin).show(primaryStage);
                } else {
                    showAlert("Error", "Current admin information not available", Alert.AlertType.ERROR);
                }
            }
        );
        
        actionsContainer.getChildren().add(userMgmtCard);
        
        section.getChildren().addAll(sectionTitle, actionsContainer);
        return section;
    }
    
    private VBox createActionCard(String title, String description, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; " +
                     "-fx-background-radius: 8; -fx-cursor: hand; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(titleLabel, descLabel);
        
        // Add click handler
        card.setOnMouseClicked(e -> {
            if (action != null) {
                action.handle(new javafx.event.ActionEvent());
            }
        });
        
        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; " +
                         "-fx-background-radius: 8; -fx-cursor: hand; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2); " +
                         "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; " +
                         "-fx-background-radius: 8; -fx-cursor: hand; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1); " +
                         "-fx-scale-x: 1.0; -fx-scale-y: 1.0;");
        });
        
        return card;
    }
    
    private VBox createStatisticsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-padding: 25; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label sectionTitle = new Label("System Overview");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Statistics content
        HBox statsContainer = new HBox(30);
        statsContainer.setAlignment(Pos.CENTER);
        
        // Get user statistics
        try {
            if (databaseHelper != null) {
                java.util.List<User> allUsers = databaseHelper.getAllUsers();
                
                VBox totalUsersCard = createStatCard("Total Users", String.valueOf(allUsers.size()), "#2196f3");
                
                long adminCount = allUsers.stream().mapToLong(u -> u.hasRole("admin") ? 1 : 0).sum();
                VBox adminsCard = createStatCard("Administrators", String.valueOf(adminCount), "#f44336");
                
                long studentCount = allUsers.stream().mapToLong(u -> u.hasRole("student") ? 1 : 0).sum();
                VBox studentsCard = createStatCard("Students", String.valueOf(studentCount), "#4caf50");
                
                statsContainer.getChildren().addAll(totalUsersCard, adminsCard, studentsCard);
            } else {
                Label noDataLabel = new Label("Database connection not available");
                noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");
                statsContainer.getChildren().add(noDataLabel);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading statistics");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336;");
            statsContainer.getChildren().add(errorLabel);
        }
        
        section.getChildren().addAll(sectionTitle, statsContainer);
        return section;
    }
    
    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-padding: 15; -fx-border-color: " + color + "; " +
                     "-fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        card.getChildren().addAll(valueLabel, labelLabel);
        return card;
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}