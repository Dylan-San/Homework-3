package application;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class FirstPage {

    private final DatabaseHelper databaseHelper;

    public FirstPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Root with LinkedIn-like light gray background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f3f2ef;"); // LinkedIn light gray

        // Card container
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28));
        card.setPrefWidth(420);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            // soft card shadow
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0.2, 0, 6);"
        );

        // Title + subtitle (LinkedIn inspired)
        Label title = new Label("Set up your admin account");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#222222"));

        Label subtitle = new Label("You're the first user — let’s create the administrator.");
        subtitle.setStyle("-fx-text-fill: #6f6f6f; -fx-font-size: 12px;");

        // Primary action in LinkedIn blue with pill shape
        Button continueButton = new Button("Continue");
        continueButton.setDefaultButton(true);
        continueButton.setPrefWidth(160);
        continueButton.setStyle(
            "-fx-background-color: #0a66c2;" +   // LinkedIn blue
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 9999;" +     
            "-fx-padding: 10 22 10 22;"
        );
        continueButton.setOnAction(e -> new AdminSetupPage(databaseHelper).show(primaryStage));

        card.getChildren().addAll(title, subtitle, continueButton);

        // Back link under the card (optional)
        Hyperlink backLink = new Hyperlink("Back");
        backLink.setBorder(Border.EMPTY);
        backLink.setStyle("-fx-text-fill: #0a66c2; -fx-font-size: 12px;");
        backLink.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        VBox center = new VBox(12, card, backLink);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(24));

        root.setCenter(center);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("First Page");
        primaryStage.show();
    }
}