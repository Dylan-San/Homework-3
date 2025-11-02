package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.*;

import databasePart1.*;

/**
 * Our AdminUserManagementPage provides comprehensive user management functionality
 * for administrators, including user listing, role management, password reset,
 * user deletion, and invitation code generation with admin support.
 * We designed it with LinkedIn-inspired clean interface.
 */
public class AdminUserManagementPage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentAdmin;
    private TableView<User> userTable;
    private ObservableList<User> userList;

    public AdminUserManagementPage(DatabaseHelper databaseHelper, User currentAdmin) {
        this.databaseHelper = databaseHelper;
        this.currentAdmin = currentAdmin;
        this.userList = FXCollections.observableArrayList();
    }

    public void show(Stage primaryStage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f3f2ef;");
        
        // Header
        VBox header = createHeader(primaryStage);
        layout.setTop(header);
        
        // Main content
        VBox mainContent = createMainContent();
        layout.setCenter(mainContent);
        
        // Load users
        refreshUserList();
        
        Scene scene = new Scene(layout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Management - Admin Panel");
    }
    
    private VBox createHeader(Stage primaryStage) {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backButton = new Button("Back to Admin Home");
        backButton.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 8 16;");
        backButton.setOnAction(e -> new AdminHomePage().show((Stage) backButton.getScene().getWindow()));
        
        headerContent.getChildren().addAll(titleLabel, spacer, backButton);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        
        // Control panel
        HBox controlPanel = createControlPanel();
        
        // User table
        VBox tableContainer = createUserTable();
        
        mainContent.getChildren().addAll(controlPanel, tableContainer);
        return mainContent;
    }
    
    private HBox createControlPanel() {
        HBox controlPanel = new HBox(15);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.setStyle("-fx-background-color: white; -fx-padding: 15; " +
                             "-fx-background-radius: 8; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #42a5f5; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 16;");
        refreshButton.setOnAction(e -> refreshUserList());
        
        Button inviteButton = new Button("Generate Invitation Code");
        inviteButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 16;");
        inviteButton.setOnAction(e -> generateInvitationCode());
        
        controlPanel.getChildren().addAll(refreshButton, inviteButton);
        return controlPanel;
    }
    
    private VBox createUserTable() {
        VBox container = new VBox(10);
        container.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                          "-fx-background-radius: 8; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        Label tableTitle = new Label("User Accounts");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        userTable = new TableView<>();
        userTable.setItems(userList);
        
        // Username column - FIXED: Use explicit lambda instead of PropertyValueFactory
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getUserName());
        });
        usernameCol.setPrefWidth(150);
        
        // Full name column
        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String fullName = user.getFullName();
            
            // If full name is empty, show username as fallback
            if (fullName == null || fullName.trim().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty(user.getUserName());
            }
            return new javafx.beans.property.SimpleStringProperty(fullName);
        });
        nameCol.setPrefWidth(200);
        
        // Email column - FIXED: Use explicit getter instead of PropertyValueFactory
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String email = user.getEmail();
            return new javafx.beans.property.SimpleStringProperty(email != null ? email : "");
        });
        emailCol.setPrefWidth(200);
        
        // Roles column
        TableColumn<User, String> rolesCol = new TableColumn<>("Roles");
        rolesCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRolesAsString()));
        rolesCol.setPrefWidth(150);
        
        // Actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final HBox actionButtons = new HBox(5);
            private final Button editButton = new Button("Edit");
            private final Button resetPasswordButton = new Button("Reset Password");
            private final Button deleteButton = new Button("Delete");
            
            {
                editButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; " +
                                   "-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 4 8;");
                resetPasswordButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; " +
                                            "-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 4 8;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                     "-fx-font-size: 12px; -fx-background-radius: 3; -fx-padding: 4 8;");
                
                actionButtons.getChildren().addAll(editButton, resetPasswordButton, deleteButton);
                actionButtons.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    
                    editButton.setOnAction(e -> editUser(user));
                    resetPasswordButton.setOnAction(e -> resetUserPassword(user));
                    deleteButton.setOnAction(e -> deleteUser(user));
                    
                    // Disable delete for current admin and if it's the last admin
                    try {
                        boolean isCurrentAdmin = user.getUserName().equals(currentAdmin.getUserName());
                        boolean isLastAdmin = isLastAdmin(user);
                        deleteButton.setDisable(isCurrentAdmin || isLastAdmin);
                    } catch (SQLException ex) {
                        deleteButton.setDisable(false);
                    }
                    
                    setGraphic(actionButtons);
                }
            }
        });
        actionsCol.setPrefWidth(250);
        
        userTable.getColumns().addAll(usernameCol, nameCol, emailCol, rolesCol, actionsCol);
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editUser(row.getItem());
                }
            });
            return row;
        });
        
        container.getChildren().addAll(tableTitle, userTable);
        return container;
    }
    
    private void refreshUserList() {
        try {
            List<User> users = databaseHelper.getAllUsers();
            userList.clear();
            
            // Debug: Log user information to see what's being loaded
            System.out.println("=== REFRESHING USER LIST ===");
            for (User user : users) {
                System.out.println("User: " + user.getUserName());
                System.out.println("  FirstName: '" + user.getFirstName() + "'");
                System.out.println("  LastName: '" + user.getLastName() + "'");
                System.out.println("  FullName: '" + user.getFullName() + "'");
                System.out.println("  Email: '" + user.getEmail() + "'");
                System.out.println("  Roles: " + user.getRolesAsString());
                System.out.println("---");
            }
            System.out.println("=== END USER LIST ===");
            
            userList.addAll(users);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load users: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void generateInvitationCode() {
        // Create custom dialog for invitation code generation
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Generate Invitation Code");
        dialog.setHeaderText("Create New Invitation Code with Deadline");
        
        // Set dialog icon and style
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        
        // Create dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        Spinner<Integer> daysSpinner = new Spinner<>(1, 365, 7);
        daysSpinner.setEditable(true);
        
        Label infoLabel = new Label("Specify how many days this invitation code should remain valid:");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        
        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(new Label("Days valid:"), 0, 1);
        grid.add(daysSpinner, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType generateButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);
        
        // Convert result when generate button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType) {
                return String.valueOf(daysSpinner.getValue());
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(daysStr -> {
            try {
                int days = Integer.parseInt(daysStr);
                if (days < 1 || days > 365) {
                    showAlert("Invalid Input", "Days must be between 1 and 365", Alert.AlertType.WARNING);
                    return;
                }
                
                String code = databaseHelper.generateInvitationCode(currentAdmin.getUserName(), days);
                
                // Create a more detailed success dialog
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Invitation Code Generated");
                successAlert.setHeaderText("New invitation code created successfully!");
                
                String message = String.format(
                    "Invitation Code: %s\n\n" +
                    "Valid for: %d days\n" +
                    "Created by: %s\n" +
                    "Created on: %s\n\n" +
                    "Share this code with new users to allow them to create accounts.",
                    code, days, currentAdmin.getUserName(), 
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
                
                successAlert.setContentText(message);
                
                // Make the code easy to copy
                TextArea textArea = new TextArea(code);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(50);
                textArea.selectAll();
                
                Label copyLabel = new Label("Invitation Code (click to select all):");
                copyLabel.setStyle("-fx-font-weight: bold;");
                
                VBox expandableContent = new VBox();
                expandableContent.setMaxWidth(Double.MAX_VALUE);
                expandableContent.getChildren().addAll(copyLabel, textArea);
                
                successAlert.getDialogPane().setExpandableContent(expandableContent);
                successAlert.getDialogPane().setExpanded(true);
                
                successAlert.showAndWait();
                
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number of days", Alert.AlertType.WARNING);
            } catch (Exception e) {
                showAlert("Error", "Failed to generate invitation code: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    
    private void editUser(User user) {
        UserEditDialog dialog = new UserEditDialog(user, databaseHelper, currentAdmin);
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                // Update user information in database
                databaseHelper.updateUser(updatedUser);
                
                // Update roles in database
                Set<String> currentRoles = databaseHelper.getUserRoles(user.getUserName());
                Set<String> newRoles = updatedUser.getRoles();
                
                // Remove old roles not in new set
                for (String role : currentRoles) {
                    if (!newRoles.contains(role)) {
                        // Don't allow removing admin role from current admin
                        if (role.equals("admin") && user.getUserName().equals(currentAdmin.getUserName())) {
                            continue;
                        }
                        // Don't allow removing admin role if user is last admin
                        if (role.equals("admin") && isLastAdmin(user)) {
                            continue;
                        }
                        databaseHelper.removeRoleFromUser(user.getUserName(), role);
                    }
                }
                
                // Add new roles
                for (String role : newRoles) {
                    if (!currentRoles.contains(role)) {
                        databaseHelper.addRoleToUser(user.getUserName(), role);
                    }
                }
                
                // IMPORTANT: Refresh the user list to show updated information
                refreshUserList();
                showAlert("Success", "User updated successfully", Alert.AlertType.INFORMATION);
                
            } catch (SQLException e) {
                showAlert("Error", "Failed to update user: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }
    
    private void resetUserPassword(User user) {
        String oneTimePassword = UUID.randomUUID().toString().substring(0, 8);
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Password");
        confirmAlert.setHeaderText("Reset password for " + user.getUserName() + "?");
        confirmAlert.setContentText("This will generate a one-time password: " + oneTimePassword + 
                                   "\n\nThe user will be required to change their password on next login.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                databaseHelper.setOneTimePassword(user.getUserName(), oneTimePassword);
                showAlert("Password Reset", 
                         "One-time password set for " + user.getUserName() + ":\n" + oneTimePassword + 
                         "\n\nPlease provide this to the user securely.", 
                         Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to reset password: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void deleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("This will permanently delete the user account for: " + user.getUserName());
        
        // Add "Yes" button explicitly
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            try {
                boolean deleted = databaseHelper.deleteUser(user.getUserName(), currentAdmin.getUserName());
                if (deleted) {
                    refreshUserList();
                    showAlert("Success", "User deleted successfully", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Cannot delete user. User might be the last admin or trying to delete yourself.", 
                             Alert.AlertType.WARNING);
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private boolean isLastAdmin(User user) throws SQLException {
        if (!user.hasRole("admin")) return false;
        
        long adminCount = userList.stream()
                .mapToLong(u -> u.hasRole("admin") ? 1 : 0)
                .sum();
        
        return adminCount <= 1;
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}