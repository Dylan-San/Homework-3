package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.util.Comparator;

import databasePart1.*;

/**
 * Our Student Dashboard that integrates all Q&A implementation files.
 * Followed the same Linkedin inspired UI for the user.
 * Each teammember assisted with implementation.
 */

public class StudentDashboard {
    
    private static final String PRIMARY_BLUE = "#0a66c2";
    private static final String BACKGROUND_GRAY = "#f3f2ef";
    private static final String WHITE = "white";
    private static final String GREEN = "#4caf50";
    private static final String GRAY = "#666666";
    private static final String RED = "#f44336";
    private static final String TEXT_PRIMARY = "#000000";
    private static final String TEXT_SECONDARY = "#666666";
    
    private static Questions sharedQuestions = null;
    private static Answers sharedAnswers = null;
    private static Replies sharedReplies = null; 
    
    private DatabaseHelper databaseHelper;
    private User currentUser;
    private Questions questions;
    private Answers answers;
    private VBox centerPanel;
    private VBox rightPanel;
    private String currentView;
    private Question selectedQuestion;
    private Replies replies; 
    
    public StudentDashboard(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        
        // Initialize shared collections only once (thread-safe)
        synchronized (StudentDashboard.class) {
            if (sharedQuestions == null) {
                sharedQuestions = new Questions();
                sharedAnswers = new Answers();
                sharedReplies = new Replies(); 
            }
        }
        
        // Use the shared collections
        this.questions = sharedQuestions;
        this.answers = sharedAnswers;
        this.currentView = "welcome";
        this.replies = sharedReplies; 
        
        // Load sample data only once
        synchronized (StudentDashboard.class) {
            if (this.questions.getQuestionCount() == 0) {
                loadSampleData();
            }
        }
    }
    
    public void show(Stage primaryStage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + BACKGROUND_GRAY + ";");
        
        VBox header = createHeader(primaryStage);
        layout.setTop(header);
        
        HBox mainContent = createMainContent();
        layout.setCenter(mainContent);
        
        Scene scene = new Scene(layout, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Dashboard");
    }
    
    private VBox createHeader(Stage primaryStage) {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: " + WHITE + "; -fx-padding: 20; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Student Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        titleBox.getChildren().add(titleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button accountButton = new Button("Account Settings");
        accountButton.setStyle("-fx-background-color: " + GREEN + "; -fx-text-fill: white; " +
                              "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
        accountButton.setOnAction(e -> {
            new UserProfilePage(databaseHelper, currentUser, primaryStage).show(primaryStage);
        });
        
        Button signOutButton = new Button("Sign Out");
        signOutButton.setStyle("-fx-background-color: " + GRAY + "; -fx-text-fill: white; " +
                             "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
        signOutButton.setOnAction(e -> {
            primaryStage.getProperties().remove("currentUser");
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        actions.getChildren().addAll(accountButton, signOutButton);
        headerContent.getChildren().addAll(titleBox, spacer, actions);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private HBox createMainContent() {
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(20));
        
        VBox leftPanel = createLeftPanel();
        leftPanel.setPrefWidth(200);
        leftPanel.setMinWidth(200);
        leftPanel.setMaxWidth(200);
        
        centerPanel = createCenterPanel();
        HBox.setHgrow(centerPanel, Priority.ALWAYS);
        centerPanel.setPrefWidth(600);
        
        rightPanel = createRightPanel();
        rightPanel.setPrefWidth(400);
        rightPanel.setMinWidth(400);
        rightPanel.setMaxWidth(400);
        
        mainContent.getChildren().addAll(leftPanel, centerPanel, rightPanel);
        return mainContent;
    }
    
    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: " + WHITE + "; -fx-padding: 20; " +
                      "-fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Button askButton = new Button("Ask");
        askButton.setPrefWidth(160);
        askButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                          "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        askButton.setOnAction(e -> showAskQuestionView());
        
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(160);
        searchButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        searchButton.setOnAction(e -> showSearchView());
        
        panel.getChildren().addAll(askButton, searchButton);
        return panel;
    }
    
    private VBox createCenterPanel() {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + WHITE + "; -fx-padding: 25; " +
                      "-fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        Label welcomeLabel = new Label("Welcome to the Q&A System");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label instructionLabel = new Label("Click 'Ask' to post a new question or select a question from the list to view details.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        instructionLabel.setWrapText(true);
        
        panel.getChildren().addAll(welcomeLabel, instructionLabel);
        return panel;
    }
    
    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: " + WHITE + "; -fx-padding: 20; " +
                      "-fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Questions List");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Unresolved", "My Unresolved", "Most Recent", "Answered", "Unanswered");
        filterCombo.setValue("All");
        filterCombo.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
        filterCombo.setOnAction(e -> updateQuestionsList(filterCombo.getValue()));
        
        headerBox.getChildren().addAll(titleLabel, spacer, filterCombo);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        VBox questionsContainer = new VBox(10);
        scrollPane.setContent(questionsContainer);
        
        updateQuestionsListContent(questionsContainer, "All");
        
        panel.getChildren().addAll(headerBox, scrollPane);
        return panel;
    }
    
    private void updateQuestionsList(String filter) {
        ScrollPane scrollPane = (ScrollPane) rightPanel.getChildren().get(1);
        VBox questionsContainer = (VBox) scrollPane.getContent();
        updateQuestionsListContent(questionsContainer, filter);
    }
    
    private void updateQuestionsListContent(VBox container, String filter) {
        container.getChildren().clear();
        
        java.util.List<Question> questionsList;
        
        switch (filter) {
            case "Unresolved":
                questionsList = questions.getUnresolvedQuestions();
                break;
            case "My Unresolved":
                questionsList = questions.getMyUnresolvedQuestions(currentUser.getUserName());
                break;
            case "Most Recent":
                questionsList = questions.getAllQuestionsSortedByMostRecent();
                break;
            case "Answered":
                questionsList = questions.getAnsweredQuestions();
                break;
            case "Unanswered":
                questionsList = questions.getUnansweredQuestions();
                break;
            default:
                questionsList = questions.getAllQuestionsSortedByMostRecent();
                break;
        }
        
        if (questionsList.isEmpty()) {
            Label noQuestionsLabel = new Label("No questions found");
            noQuestionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            container.getChildren().add(noQuestionsLabel);
            return;
        }
        
        for (Question q : questionsList) {
            VBox questionItem = createQuestionListItem(q);
            container.getChildren().add(questionItem);
        }
    }
    
    private VBox createQuestionListItem(Question question) {
        VBox item = new VBox(5);
        item.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; " +
                     "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; " +
                     "-fx-background-radius: 5; -fx-cursor: hand;");
        
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        titleLabel.setWrapText(true);
        
        HBox metadataBox = new HBox(10);
        metadataBox.setAlignment(Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("by " + question.getAuthor());
        authorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        Label timeLabel = new Label(question.getTimeAgo());
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        Label dateLabel = new Label(question.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        metadataBox.getChildren().addAll(authorLabel, new Label("•"), timeLabel, new Label("•"), dateLabel);
        
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        String answerText = question.getTotalAnswers() + " answer" + 
                           (question.getTotalAnswers() != 1 ? "s" : "");
        Label answerLabel = new Label(answerText);
        answerLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        statusBox.getChildren().add(answerLabel);
        
        if (question.getNewAnswers() > 0) {
            Label newLabel = new Label("(" + question.getNewAnswers() + " new)");
            newLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + RED + "; -fx-font-weight: bold;");
            statusBox.getChildren().add(newLabel);
        }
        
        if (question.isResolved()) {
            Label resolvedLabel = new Label("RESOLVED");
            resolvedLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");
            statusBox.getChildren().add(resolvedLabel);
        }
        
        item.getChildren().addAll(titleLabel, metadataBox, statusBox);
        item.setOnMouseClicked(e -> showQuestionDetails(question));
        
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #e8e8e8; -fx-padding: 10; " +
                                                  "-fx-border-color: #d0d0d0; -fx-border-width: 1; " +
                                                  "-fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; " +
                                                 "-fx-border-color: #e0e0e0; -fx-border-width: 1; " +
                                                 "-fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        
        return item;
    }
    
    private void showAskQuestionView() {
        currentView = "ask";
        centerPanel.getChildren().clear();
        
        Label headingLabel = new Label("Ask a Question");
        headingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label titleFieldLabel = new Label("Title (max " + Question.getMaxTitleLength() + " characters):");
        titleFieldLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        TextField titleField = new TextField();
        titleField.setPromptText("Enter question title");
        titleField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; " +
                           "-fx-background-radius: 5;");
        
        Label titleErrorLabel = new Label();
        titleErrorLabel.setTextFill(Color.web(RED));
        titleErrorLabel.setWrapText(true);
        
        Label titleCountLabel = new Label("0/" + Question.getMaxTitleLength());
        titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            titleCountLabel.setText(newVal.length() + "/" + Question.getMaxTitleLength());
            if (newVal.length() > Question.getMaxTitleLength()) {
                titleErrorLabel.setText("You have exceeded the maximum character limit");
                titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                titleErrorLabel.setText("");
                titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        Label bodyFieldLabel = new Label("Question Body (max " + Question.getMaxBodyLength() + " characters):");
        bodyFieldLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 10 0 0 0;");
        
        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Enter your question details");
        bodyField.setPrefRowCount(10);
        bodyField.setWrapText(true);
        bodyField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; " +
                          "-fx-background-radius: 5;");
        
        Label bodyErrorLabel = new Label();
        bodyErrorLabel.setTextFill(Color.web(RED));
        bodyErrorLabel.setWrapText(true);
        
        Label bodyCountLabel = new Label("0/" + Question.getMaxBodyLength());
        bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        bodyField.textProperty().addListener((obs, oldVal, newVal) -> {
            bodyCountLabel.setText(newVal.length() + "/" + Question.getMaxBodyLength());
            if (newVal.length() > Question.getMaxBodyLength()) {
                bodyErrorLabel.setText("You have exceeded the maximum character limit");
                bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                bodyErrorLabel.setText("");
                bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String body = bodyField.getText().trim();
            
            titleErrorLabel.setText("");
            bodyErrorLabel.setText("");
            
            if (title.isEmpty()) {
                titleErrorLabel.setText("Field cannot be left empty");
                return;
            }
            String titleValidation = Question.validateTitle(title);
            if (!titleValidation.isEmpty()) {
                titleErrorLabel.setText(titleValidation);
                return;
            }
            
            if (body.isEmpty()) {
                bodyErrorLabel.setText("Field cannot be left empty");
                return;
            }
            String bodyValidation = Question.validateBody(body);
            if (!bodyValidation.isEmpty()) {
                bodyErrorLabel.setText(bodyValidation);
                return;
            }
            
            // Check for duplicate question
            if (isDuplicateQuestion(title, body)) {
                titleErrorLabel.setText("Duplicates not allowed");
                return;
            }
            
            Question newQuestion = new Question(title, body, currentUser.getUserName());
            questions.addQuestion(newQuestion);
            
            titleField.clear();
            bodyField.clear();
            titleCountLabel.setText("0/" + Question.getMaxTitleLength());
            bodyCountLabel.setText("0/" + Question.getMaxBodyLength());
            
            updateQuestionsList("All");
            showQuestionDetails(newQuestion);
        });
        
        centerPanel.getChildren().addAll(headingLabel, titleFieldLabel, titleField, titleCountLabel, 
            titleErrorLabel, bodyFieldLabel, bodyField, bodyCountLabel, bodyErrorLabel, submitButton);
    }
    
    private void showSearchView() {
        currentView = "search";
        centerPanel.getChildren().clear();
        
        HBox headingBox = new HBox(15);
        headingBox.setAlignment(Pos.CENTER_LEFT);
        
        Label headingLabel = new Label("Search");
        headingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        ComboBox<String> searchFilterCombo = new ComboBox<>();
        searchFilterCombo.getItems().addAll("All", "Unresolved", "My Unresolved", "Most Recent", "Answered", "Unanswered");
        searchFilterCombo.setValue("All");
        searchFilterCombo.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
        
        headingBox.getChildren().addAll(headingLabel, new Label("Filter:"), searchFilterCombo);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter keywords to search (max " + Question.getMaxTitleLength() + " characters)");
        searchField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label searchErrorLabel = new Label();
        searchErrorLabel.setTextFill(Color.web(RED));
        searchErrorLabel.setWrapText(true);
        
        Label searchCountLabel = new Label("0/" + Question.getMaxTitleLength());
        searchCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchCountLabel.setText(newVal.length() + "/" + Question.getMaxTitleLength());
            if (newVal.length() > Question.getMaxTitleLength()) {
                searchErrorLabel.setText("You have exceeded the maximum character limit");
                searchCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                searchErrorLabel.setText("");
                searchCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        VBox resultsContainer = new VBox(10);
        resultsContainer.setStyle("-fx-padding: 20 0 0 0;");
        
        ScrollPane resultsScrollPane = new ScrollPane();
        resultsScrollPane.setContent(resultsContainer);
        resultsScrollPane.setFitToWidth(true);
        resultsScrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(resultsScrollPane, Priority.ALWAYS);
        
        searchButton.setOnAction(e -> {
            String searchQuery = searchField.getText().trim();
            searchErrorLabel.setText("");
            resultsContainer.getChildren().clear();
            
            if (searchQuery.isEmpty()) {
                searchErrorLabel.setText("Field cannot be left empty");
                return;
            }
            
            if (searchQuery.length() > Question.getMaxTitleLength()) {
                searchErrorLabel.setText("You have exceeded the maximum character limit");
                return;
            }
            
            java.util.List<Question> searchResults = questions.searchQuestionsWithFilter(searchQuery, searchFilterCombo.getValue());
            
            if (searchResults.isEmpty()) {
                Label noResultsLabel = new Label("No questions found matching your search");
                noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
                resultsContainer.getChildren().add(noResultsLabel);
            } else {
                Label resultsLabel = new Label("Found " + searchResults.size() + " result(s):");
                resultsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
                resultsContainer.getChildren().add(resultsLabel);
                
                for (Question q : searchResults) {
                    VBox questionItem = createQuestionListItem(q);
                    resultsContainer.getChildren().add(questionItem);
                }
            }
        });
        
        centerPanel.getChildren().addAll(headingBox, searchField, searchCountLabel, searchErrorLabel, searchButton, resultsScrollPane);
    }
    
    private void showQuestionDetails(Question question) {
        currentView = "questionDetails";
        selectedQuestion = question;
        
        if (question.getAuthor().equals(currentUser.getUserName())) {
            question.resetNewAnswers();
        }
        
        centerPanel.getChildren().clear();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        VBox contentBox = new VBox(15);
        contentBox.setStyle("-fx-background-color: white;");
        scrollPane.setContent(contentBox);
        
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        titleLabel.setWrapText(true);
        
        Label authorLabel = new Label("Asked by " + question.getAuthor());
        authorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        Label timeLabel = new Label("Posted " + question.getTimeAgo() + " ago (" + question.getFormattedTimestamp() + ")");
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        VBox resolvedBox = new VBox(5);
        if (question.isResolved()) {
            Label resolvedLabel = new Label("RESOLVED");
            resolvedLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");
            resolvedBox.getChildren().add(resolvedLabel);
        }
        
        Separator separator1 = new Separator();
        
        Label bodyLabel = new Label(question.getBody());
        bodyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        bodyLabel.setWrapText(true);
        
        HBox actionButtons = new HBox(10);
        if (question.getAuthor().equals(currentUser.getUserName())) {
            // Edit link
            Hyperlink editLink = new Hyperlink("Edit");
            editLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            
            // Delete link
            Hyperlink deleteLink = new Hyperlink("Delete");
            deleteLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            
            // Disable edit and delete if question is resolved
            if (question.isResolved()) {
                editLink.setDisable(true);
                editLink.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
                deleteLink.setDisable(true);
                deleteLink.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            } else {
                editLink.setOnAction(e -> showEditQuestionView(question));
                deleteLink.setOnAction(e -> {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Deletion");
                    confirmAlert.setHeaderText("Delete this question?");
                    confirmAlert.setContentText("This action cannot be undone. All answers will also be deleted.");
                    
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                        	//Deletes all replies for answer - NEW
                        	for (Answer ans : answers.getAnswersForQuestion(question.getQuestionId())) {
                        		replies.deleteAllRepliesForAnswer(ans.getAnswerId()); 
                        	}
                            answers.deleteAllAnswersForQuestion(question.getQuestionId());
                            questions.deleteQuestion(question.getQuestionId());
                            updateQuestionsList("All");
                            showWelcomeView();
                        }
                    });
                });
            }
            
            actionButtons.getChildren().addAll(editLink, new Label("|"), deleteLink);
        }
        
        Separator separator2 = new Separator();
        
        VBox answersSection = new VBox(15);
        
        // Only show answer input if question is not resolved AND user is not the question author
        if (!question.isResolved() && !question.getAuthor().equals(currentUser.getUserName())) {
            Label answerHeading = new Label("Answer");
            answerHeading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
            
            TextArea answerField = new TextArea();
            answerField.setPromptText("Enter your answer (max " + Answer.getMaxContentLength() + " characters)");
            answerField.setPrefRowCount(5);
            answerField.setWrapText(true);
            answerField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            Label answerErrorLabel = new Label();
            answerErrorLabel.setTextFill(Color.web(RED));
            answerErrorLabel.setWrapText(true);
            
            Label answerCountLabel = new Label("0/" + Answer.getMaxContentLength());
            answerCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            
            answerField.textProperty().addListener((obs, oldVal, newVal) -> {
                answerCountLabel.setText(newVal.length() + "/" + Answer.getMaxContentLength());
                if (newVal.length() > Answer.getMaxContentLength()) {
                    answerErrorLabel.setText("You have exceeded the maximum character limit");
                    answerCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
                } else {
                    answerErrorLabel.setText("");
                    answerCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
                }
            });
            
            Button postAnswerButton = new Button("Post");
            postAnswerButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                                     "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
            
            postAnswerButton.setOnAction(e -> {
                String answerContent = answerField.getText().trim();
                answerErrorLabel.setText("");
                
                if (answerContent.isEmpty()) {
                    answerErrorLabel.setText("Field cannot be left empty");
                    return;
                }
                
                String validation = Answer.validateContent(answerContent);
                if (!validation.isEmpty()) {
                    answerErrorLabel.setText(validation);
                    return;
                }
                
                // Check if user is trying to answer their own question
                if (question.getAuthor().equals(currentUser.getUserName())) {
                    answerErrorLabel.setText("You cannot answer your own question");
                    return;
                }
                
                // Check for duplicate answer
                if (isDuplicateAnswer(question.getQuestionId(), answerContent)) {
                    answerErrorLabel.setText("Duplicates not allowed");
                    return;
                }
                
                Answer newAnswer = new Answer(question.getQuestionId(), answerContent, currentUser.getUserName());
                answers.addAnswer(newAnswer);
                
                question.incrementTotalAnswers();
                if (!question.getAuthor().equals(currentUser.getUserName())) {
                    question.incrementNewAnswers();
                }
                questions.updateQuestion(question);
                
                answerField.clear();
                answerCountLabel.setText("0/" + Answer.getMaxContentLength());
                
                showQuestionDetails(question);
                updateQuestionsList("All");
            });
            
            answersSection.getChildren().addAll(answerHeading, answerField, answerCountLabel, answerErrorLabel, 
                postAnswerButton, new Separator());
        } else {
            Label answerHeading = new Label("Answers");
            answerHeading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_SECONDARY + ";");
            answersSection.getChildren().addAll(answerHeading, new Separator());
        }
        
        java.util.List<Answer> questionAnswers = answers.getAnswersForQuestionWithResolvedFirst(
            question.getQuestionId(), question.getResolvedAnswerId());
        
        if (questionAnswers.isEmpty()) {
            Label noAnswersLabel = new Label("No answers yet. Be the first to answer!");
            noAnswersLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            answersSection.getChildren().add(noAnswersLabel);
        } else {
            for (Answer answer : questionAnswers) {
                VBox answerBox = createAnswerBox(answer, question);
                answersSection.getChildren().add(answerBox);
            }
        }
        
        contentBox.getChildren().addAll(titleLabel, authorLabel, timeLabel, resolvedBox, separator1, 
            bodyLabel, actionButtons, separator2, answersSection);
        
        centerPanel.getChildren().add(scrollPane);
    }
    
    private VBox createAnswerBox(Answer answer, Question question) {
    	
    	//outer container for replies to answers -- NEW 
    	VBox mainBox = new VBox(10);
    	
        VBox answerBox = new VBox(10);
        answerBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; " +
                          "-fx-border-color: #e0e0e0; -fx-border-width: 1; " +
                          "-fx-border-radius: 5; -fx-background-radius: 5;");
        
        HBox metadataBox = new HBox(10);
        metadataBox.setAlignment(Pos.CENTER_LEFT);
        
        Label authorLabel = new Label("Answered by " + answer.getAuthor());
        authorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        Label timeLabel = new Label(answer.getTimeAgo() + " ago");
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        metadataBox.getChildren().addAll(authorLabel, new Label("•"), timeLabel);
        
        if (answer.isMarkedAsResolved()) {
            Label resolvedLabel = new Label("RESOLVED");
            resolvedLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");
            metadataBox.getChildren().add(resolvedLabel);
        }
        
        Label contentLabel = new Label(answer.getContent());
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        contentLabel.setWrapText(true);
        
        answerBox.getChildren().addAll(metadataBox, contentLabel);
        
        // Edit/Delete options for answer author
        if (answer.getAuthor().equals(currentUser.getUserName())) {
            HBox answerActions = new HBox(10);
            
            Hyperlink editAnswerLink = new Hyperlink("Edit");
            editAnswerLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            
            Hyperlink deleteAnswerLink = new Hyperlink("Delete");
            deleteAnswerLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            
            // Disable edit and delete if answer is marked as resolved
            if (answer.isMarkedAsResolved()) {
                editAnswerLink.setDisable(true);
                editAnswerLink.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
                deleteAnswerLink.setDisable(true);
                deleteAnswerLink.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            } else {
                editAnswerLink.setOnAction(e -> showEditAnswerView(answer, question));
                deleteAnswerLink.setOnAction(e -> {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Deletion");
                    confirmAlert.setHeaderText("Delete this answer?");
                    confirmAlert.setContentText("This action cannot be undone.");
                    
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                        	//Delete all replies attached to specific answer - NEW 
                        	replies.deleteAllRepliesForAnswer(answer.getAnswerId());
                        	
                            answers.deleteAnswer(answer.getAnswerId());
                            question.decrementTotalAnswers();
                            questions.updateQuestion(question);
                            showQuestionDetails(question);
                            updateQuestionsList("All");
                        }
                    });
                });
            }
            
            answerActions.getChildren().addAll(editAnswerLink, new Label("|"), deleteAnswerLink);
            answerBox.getChildren().add(answerActions);
        }
        
        // Mark as Resolved button (only for question author and if question is not already resolved)
        if (question.getAuthor().equals(currentUser.getUserName())) {
            Button resolveButton = new Button("Mark as Resolved");
            
            if (question.isResolved()) {
                resolveButton.setDisable(true);
                resolveButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; " +
                                      "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
            } else {
                resolveButton.setStyle("-fx-background-color: " + GREEN + "; -fx-text-fill: white; " +
                                      "-fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
                resolveButton.setOnAction(e -> {
                    answer.markAsResolved();
                    answers.updateAnswer(answer);
                    
                    question.markAsResolved(answer.getAnswerId());
                    questions.updateQuestion(question);
                    
                    showQuestionDetails(question);
                    updateQuestionsList("All");
                });
            }
            
            answerBox.getChildren().add(resolveButton);
        }
        
        //Add answerBox to mainBox -- NEW
        mainBox.getChildren().add(answerBox); 
        
        //Create the reply Section -- NEW
        VBox replySection = createReplySection(answer, question); 
        mainBox.getChildren().add(replySection); 
        
        //Return the mainBox (Includes answers and replies together) -- NEW
        return mainBox; 
        //return answerBox;
    }
    
    //Create the input box for the new replies to answers -- NEW METHOD
    private VBox createReplyInputBox(Answer answer, Question question) {
    	VBox inputBox = new VBox(10); 
    	inputBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
    	
    	Label replyLabel = new Label("Reply to Answer"); 
    	replyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_PRIMARY + ";");
    	
    	TextArea replyField = new TextArea(); 
    	replyField.setPromptText("Enter your reply (max "  + Reply.MAX_CONTENT_LENGTH + " characters)" );  
    	replyField.setPrefRowCount(3);
    	replyField.setWrapText(true);
    	replyField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
    	
    	Label replyErrorLabel = new Label(); 
    	replyErrorLabel.setTextFill(Color.web(RED));
    	replyErrorLabel.setWrapText(true); 
    	
    	Label replyCountLabel = new Label("0/" + Reply.MAX_CONTENT_LENGTH);
    	replyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");

    	
    	replyField.textProperty().addListener((obs, oldVal, newVal) -> {
    		
    		replyCountLabel.setText(newVal.length() + "/" + Reply.MAX_CONTENT_LENGTH);
    	    if (newVal.length() > Reply.MAX_CONTENT_LENGTH) {
    	    	replyErrorLabel.setText("You have exceeded the maximum character limit"); 
    	    	replyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
    	    } else { 
    	    	replyErrorLabel.setText(""); 
    	    	replyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
    	    }
    		
    	}); 
    	
    	Button submitReplyButton = new Button("Submit Reply"); 
    	submitReplyButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 15; -fx-padding: 6 12;");
    	
    	submitReplyButton.setOnAction(e -> {
    		String replyContent = replyField.getText().trim(); 
    		replyErrorLabel.setText("");
    		
    		if(replyContent.isEmpty()) {
    			replyErrorLabel.setText("Field cannot be empty"); 
    			return; 
    		}
    		
    		String validation = Reply.validateContent(replyContent);
    		if(!validation.isEmpty()) {
    			replyErrorLabel.setText(validation); 
    			return; 
    		}
    		
    		Reply newReply = new Reply(answer.getAnswerId(), replyContent, currentUser.getUserName()); 
    		replies.addReply(newReply); 
    		
    		replyField.clear(); 
    		replyCountLabel.setText("0/" + Reply.MAX_CONTENT_LENGTH); 
    		
    		showQuestionDetails(question); 
    	}); 
    	
    	inputBox.getChildren().addAll(replyLabel, replyField, replyCountLabel, replyErrorLabel, submitReplyButton);
    	
    	return inputBox; 

    }
    
    //Shows dialog to edit reply -- NEW METHOD
    
    private void showEditReplyDialog(Reply reply, Answer answer, Question question) {
    	Alert dialog = new Alert(Alert.AlertType.NONE); 
    	dialog.setTitle("Edit Reply");
    	dialog.setHeaderText("Edit your reply");
    	
    	VBox content = new VBox(15);
    	content.setPadding(new Insets(20));
    	
    	Label label = new Label ("Reply content:"); 
    	label.setStyle("-fx-font-size: 14px;");
    	
    	TextArea replyField = new TextArea(reply.getContent()); 
    	replyField.setPrefRowCount(5);
    	replyField.setWrapText(true);
    	replyField.setStyle("-fx-padding: 10;");
    	
    	Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web(RED));
        errorLabel.setWrapText(true);
        
        Label countLabel = new Label(reply.getContent().length() + "/" + Reply.MAX_CONTENT_LENGTH);
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        replyField.textProperty().addListener((obs, oldVal, newVal) -> {
        	
        	countLabel.setText(newVal.length() + "/" + Reply.MAX_CONTENT_LENGTH);
    	    if (newVal.length() > Reply.MAX_CONTENT_LENGTH) {
    	    	errorLabel.setText("You have exceeded the maximum character limit"); 
    	    	countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
    	    } else { 
    	    	errorLabel.setText(""); 
    	    	countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
    	    }
        }); 
        
        content.getChildren().addAll(label, replyField, countLabel, errorLabel); 
        dialog.getDialogPane().setContent(content); 
        
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE); 
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.OK_DONE); 
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);
        
        dialog.showAndWait().ifPresent(response -> {
        	if(response == saveButton) {
        		String newContent = replyField.getText().trim(); 
        		
        		if(newContent.isEmpty()) {
        			errorLabel.setText("Field cannot be empty"); 
        			return; 
        		}
        		
        		String validation = Reply.validateContent(newContent); 
        		if(!validation.isEmpty()) {
        			errorLabel.setText(validation);
        			return; 
        		}
        		
        		reply.setContent(newContent);
        		replies.updateReply(reply);
        		showQuestionDetails(question); 
        	}
        });
    }
    
    //Creates a single reply box -- NEW METHOD
    private VBox createReplyBox(Reply reply, Answer answer, Question question) {
    	
    	VBox replyBox = new VBox(5);
    	replyBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        
    	
    	//Reply author
    	Label authorLabel = new Label(reply.getAuthor()); 
    	authorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
    	
    	
    	//reply content
    	Label contentLabel = new Label(reply.getContent()); 
    	contentLabel.setWrapText(true); 
    	contentLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
    	
    	replyBox.getChildren().addAll(authorLabel, contentLabel);
    	
    	//edit & delete hyperLinks
    	if (reply.getAuthor().equals(currentUser.getUserName())) {
            // Edit link
    		
    		HBox replyActions = new HBox(10); 
    		
            Hyperlink editLink = new Hyperlink("Edit");
            editLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            editLink.setOnAction(e -> showEditReplyDialog(reply, answer, question));
            
            // Delete link
            Hyperlink deleteLink = new Hyperlink("Delete");
            deleteLink.setStyle("-fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 12px;");
            deleteLink.setOnAction(e -> {
            	replies.deleteReply(reply.getReplyID()); 
            	showQuestionDetails(question); 
            });
            
            replyActions.getChildren().addAll(editLink, new Label("|"), deleteLink); 
            replyBox.getChildren().add(replyActions); 
    	}
    	
    	return replyBox; 
    }
    
    
    //Creates a reply section for answer -- NEW METHOD
    private VBox createReplySection(Answer answer, Question question) {
    	
    	VBox replySection = new VBox(10);
    	
    	//indent replies -- NEW
    	replySection.setStyle("-fx-padding: 0 0 0 30;");
    	
    	//Get existing replies
    	java.util.List<Reply> existingReplies = replies.getRepliesForAnswers(answer.getAnswerId()); 
    	
    	
    	//Display replies -- NEW
    	if(!existingReplies.isEmpty()) {
    		for(Reply reply : existingReplies) {
    			VBox replyBox = createReplyBox(reply, answer, question);
    			replySection.getChildren().add(replyBox); 
    		}
    	}
    	
    	VBox replyInputBox = createReplyInputBox(answer, question); 
    	replySection.getChildren().add(replyInputBox);
    	
    	return replySection; 
    }
    
    private void showEditQuestionView(Question question) {
        currentView = "editQuestion";
        centerPanel.getChildren().clear();
        
        Label headingLabel = new Label("Edit Question");
        headingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label titleFieldLabel = new Label("Title (max " + Question.getMaxTitleLength() + " characters):");
        titleFieldLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        TextField titleField = new TextField(question.getTitle());
        titleField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label titleErrorLabel = new Label();
        titleErrorLabel.setTextFill(Color.web(RED));
        titleErrorLabel.setWrapText(true);
        
        Label titleCountLabel = new Label(question.getTitle().length() + "/" + Question.getMaxTitleLength());
        titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            titleCountLabel.setText(newVal.length() + "/" + Question.getMaxTitleLength());
            if (newVal.length() > Question.getMaxTitleLength()) {
                titleErrorLabel.setText("You have exceeded the maximum character limit");
                titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                titleErrorLabel.setText("");
                titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        Label bodyFieldLabel = new Label("Question Body (max " + Question.getMaxBodyLength() + " characters):");
        bodyFieldLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-padding: 10 0 0 0;");
        
        TextArea bodyField = new TextArea(question.getBody());
        bodyField.setPrefRowCount(10);
        bodyField.setWrapText(true);
        bodyField.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label bodyErrorLabel = new Label();
        bodyErrorLabel.setTextFill(Color.web(RED));
        bodyErrorLabel.setWrapText(true);
        
        Label bodyCountLabel = new Label(question.getBody().length() + "/" + Question.getMaxBodyLength());
        bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        bodyField.textProperty().addListener((obs, oldVal, newVal) -> {
            bodyCountLabel.setText(newVal.length() + "/" + Question.getMaxBodyLength());
            if (newVal.length() > Question.getMaxBodyLength()) {
                bodyErrorLabel.setText("You have exceeded the maximum character limit");
                bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                bodyErrorLabel.setText("");
                bodyCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        HBox buttonsBox = new HBox(10);
        
        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: " + GRAY + "; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 10 20;");
        
        saveButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String body = bodyField.getText().trim();
            
            titleErrorLabel.setText("");
            bodyErrorLabel.setText("");
            
            if (title.isEmpty()) {
                titleErrorLabel.setText("Field cannot be left empty");
                return;
            }
            String titleValidation = Question.validateTitle(title);
            if (!titleValidation.isEmpty()) {
                titleErrorLabel.setText(titleValidation);
                return;
            }
            
            if (body.isEmpty()) {
                bodyErrorLabel.setText("Field cannot be left empty");
                return;
            }
            String bodyValidation = Question.validateBody(body);
            if (!bodyValidation.isEmpty()) {
                bodyErrorLabel.setText(bodyValidation);
                return;
            }
            
            question.setTitle(title);
            question.setBody(body);
            questions.updateQuestion(question);
            
            updateQuestionsList("All");
            showQuestionDetails(question);
        });
        
        cancelButton.setOnAction(e -> showQuestionDetails(question));
        
        buttonsBox.getChildren().addAll(saveButton, cancelButton);
        
        centerPanel.getChildren().addAll(headingLabel, titleFieldLabel, titleField, titleCountLabel, 
            titleErrorLabel, bodyFieldLabel, bodyField, bodyCountLabel, bodyErrorLabel, buttonsBox);
    }
    
    private void showWelcomeView() {
        currentView = "welcome";
        selectedQuestion = null;
        centerPanel.getChildren().clear();
        
        Label welcomeLabel = new Label("Welcome to the Q&A System");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label instructionLabel = new Label("Click 'Ask' to post a new question or select a question from the list to view details.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        instructionLabel.setWrapText(true);
        
        centerPanel.getChildren().addAll(welcomeLabel, instructionLabel);
    }
    
    private void loadSampleData() {
        Question q1 = new Question(
            "How do I implement JavaFX TableView with custom cell factories?",
            "I'm trying to create a TableView in JavaFX where one of the columns contains buttons. " +
            "I've looked at the documentation but I'm confused about how to use cell factories. " +
            "Can someone provide a clear example of how to add action buttons to table cells?",
            "alice_student"
        );
        questions.addQuestion(q1);
        
        Question q2 = new Question(
            "What's the difference between ArrayList and LinkedList?",
            "I understand both are List implementations, but when should I use one over the other? " +
            "I've heard ArrayList is faster for random access, but what does that mean in practical terms? " +
            "Are there specific use cases where LinkedList is clearly better?",
            "bob_coder"
        );
        questions.addQuestion(q2);
        
        Answer a2_1 = new Answer(
            q2.getQuestionId(),
            "ArrayList uses an array internally, so accessing elements by index is O(1). " +
            "LinkedList uses nodes with pointers, so accessing by index requires traversing from the start - O(n). " +
            "Use ArrayList when you frequently access elements by index. Use LinkedList when you frequently " +
            "add/remove elements from the beginning or middle of the list.",
            "charlie_helper"
        );
        answers.addAnswer(a2_1);
        q2.incrementTotalAnswers();
        q2.incrementNewAnswers();
        
        Answer a2_2 = new Answer(
            q2.getQuestionId(),
            "In practice, ArrayList is almost always the better choice unless you're doing tons of " +
            "insertions/deletions at the front of the list. Modern CPUs are optimized for array access, " +
            "and the overhead of pointer chasing in LinkedList often makes it slower even for operations " +
            "where it should theoretically be faster.",
            "diana_expert"
        );
        answers.addAnswer(a2_2);
        q2.incrementTotalAnswers();
        q2.incrementNewAnswers();
        questions.updateQuestion(q2);
        
        Question q3 = new Question(
            "NullPointerException when accessing user input",
            "I keep getting a NullPointerException on line 42 of my code when I try to process user input " +
            "from a TextField. The error happens after I click the submit button. Here's the relevant code: " +
            "String input = textField.getText(); if(input.equals(\"test\")) {...}",
            currentUser.getUserName()
        );
        questions.addQuestion(q3);
        
        Answer a3_1 = new Answer(
            q3.getQuestionId(),
            "The issue is that you're calling equals() on the input variable which might be null. " +
            "Instead, reverse the comparison: if(\"test\".equals(input)). This way, even if input is null, " +
            "you won't get a NullPointerException because you're calling equals() on the String literal.",
            "eve_debugger"
        );
        answers.addAnswer(a3_1);
        a3_1.markAsResolved();
        answers.updateAnswer(a3_1);
        q3.incrementTotalAnswers();
        q3.markAsResolved(a3_1.getAnswerId());
        questions.updateQuestion(q3);
        
        Question q4 = new Question(
            "How to connect JavaFX application to H2 database?",
            "I'm working on a project that requires database connectivity. I've added the H2 dependency " +
            "to my project, but I'm not sure how to establish the connection from my JavaFX application. " +
            "Should I create a separate database helper class? Any examples would be appreciated!",
            "frank_dev"
        );
        questions.addQuestion(q4);
        
        Question q5 = new Question(
            "Best practices for input validation in JavaFX forms?",
            "I have a registration form with multiple TextFields and I need to validate user input. " +
            "What's the recommended approach? Should I validate on every keystroke using listeners, " +
            "or wait until the submit button is clicked? Also, how should I display error messages?",
            currentUser.getUserName()
        );
        questions.addQuestion(q5);
        
        Answer a5_1 = new Answer(
            q5.getQuestionId(),
            "I recommend using real-time validation with TextProperty listeners for immediate feedback. " +
            "Display error messages in Labels below each field with red text. You can also change the " +
            "border color of the TextField to red when invalid. Just make sure the validation isn't too strict " +
            "while the user is still typing.",
            "grace_ux"
        );
        answers.addAnswer(a5_1);
        q5.incrementTotalAnswers();
        q5.incrementNewAnswers();
        questions.updateQuestion(q5);
    }
    
    private boolean isDuplicateQuestion(String title, String body) {
        for (Question q : questions.getAllQuestions()) {
            if (q.getTitle().equalsIgnoreCase(title) && q.getBody().equalsIgnoreCase(body)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDuplicateAnswer(String questionId, String content) {
        for (Answer a : answers.getAnswersForQuestion(questionId)) {
            if (a.getContent().equalsIgnoreCase(content)) {
                return true;
            }
        }
        return false;
    }
    
    private void showEditAnswerView(Answer answer, Question question) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Edit Answer");
        dialog.setHeaderText("Edit your answer");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label label = new Label("Answer content:");
        label.setStyle("-fx-font-size: 14px;");
        
        TextArea answerField = new TextArea(answer.getContent());
        answerField.setPrefRowCount(8);
        answerField.setWrapText(true);
        answerField.setStyle("-fx-padding: 10;");
        
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web(RED));
        errorLabel.setWrapText(true);
        
        Label countLabel = new Label(answer.getContent().length() + "/" + Answer.getMaxContentLength());
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        answerField.textProperty().addListener((obs, oldVal, newVal) -> {
            countLabel.setText(newVal.length() + "/" + Answer.getMaxContentLength());
            if (newVal.length() > Answer.getMaxContentLength()) {
                errorLabel.setText("You have exceeded the maximum character limit");
                countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
            } else {
                errorLabel.setText("");
                countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            }
        });
        
        content.getChildren().addAll(label, answerField, countLabel, errorLabel);
        dialog.getDialogPane().setContent(content);
        
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButton) {
                String newContent = answerField.getText().trim();
                
                if (newContent.isEmpty()) {
                    errorLabel.setText("Field cannot be left empty");
                    return;
                }
                
                String validation = Answer.validateContent(newContent);
                if (!validation.isEmpty()) {
                    errorLabel.setText(validation);
                    return;
                }
                
                // Check for duplicate (excluding current answer)
                for (Answer a : answers.getAnswersForQuestion(question.getQuestionId())) {
                    if (!a.getAnswerId().equals(answer.getAnswerId()) && 
                        a.getContent().equalsIgnoreCase(newContent)) {
                        errorLabel.setText("Duplicates not allowed");
                        return;
                    }
                }
                
                answer.setContent(newContent);
                answers.updateAnswer(answer);
                showQuestionDetails(question);
            }
        });
    }
}