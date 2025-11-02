package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Our Question class represents a single question in the Q&A system.
 * Stores question data including title, body, author, timestamps, and resolved status.
 */
public class Question {
    
    // Maximum character limits as per requirements
    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_BODY_LENGTH = 5000;
    
    // Question attributes
    private String questionId;           
    private String title;        
    private String body;    
    private String author;   
    private LocalDateTime createdAt;     
    private LocalDateTime updatedAt;  
    private boolean resolved;            
    private String resolvedAnswerId;    
    private int totalAnswers; 
    private int newAnswers;    
    
    /**
     * Constructor for creating a new question.
     * Automatically generates a unique ID and sets creation timestamp.
     * 
     * title The question title (max 150 characters)
     * body The question body (max 5000 characters)
     * author The username of the person asking the question
     */
    
    public Question(String title, String body, String author) {
        this.questionId = UUID.randomUUID().toString();
        this.title = title;
        this.body = body;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.resolved = false;
        this.resolvedAnswerId = null;
        this.totalAnswers = 0;
        this.newAnswers = 0;
    }
    
    /**
     * Constructor for loading an existing question from storage.
     * Used when retrieving questions from a database or file.
     * 
     * questionId: Unique identifier
     * title: Question title
     * body: Question body
     * author: Question author
     * createdAt: Creation timestamp
     * updatedAt: Last update timestamp
     * resolved: Resolution status
     * resolvedAnswerId: ID of resolving answer (if resolved)
     * totalAnswers: Total answer count
     * newAnswers: New answer count
     */
    
    public Question(String questionId, String title, String body, String author,
                   LocalDateTime createdAt, LocalDateTime updatedAt, boolean resolved,
                   String resolvedAnswerId, int totalAnswers, int newAnswers) {
        this.questionId = questionId;
        this.title = title;
        this.body = body;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolved = resolved;
        this.resolvedAnswerId = resolvedAnswerId;
        this.totalAnswers = totalAnswers;
        this.newAnswers = newAnswers;
    }
    
    // Getters
    
    public String getQuestionId() {
        return questionId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getBody() {
        return body;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean isResolved() {
        return resolved;
    }
    
    public String getResolvedAnswerId() {
        return resolvedAnswerId;
    }
    
    public int getTotalAnswers() {
        return totalAnswers;
    }
    
    public int getNewAnswers() {
        return newAnswers;
    }
    
    //Setters with Validation 
    
    /**
     * Sets the question title with validation.
     * 
     * title New title (must not exceed MAX_TITLE_LENGTH)
     * return true if title was set successfully, false if validation failed
     */
    public boolean setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            return false;
        }
        this.title = title;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Sets the question body with validation.
     * 
     * body New body (must not exceed MAX_BODY_LENGTH)
     * return true if body was set successfully, false if validation failed
     */
    public boolean setBody(String body) {
        if (body == null || body.trim().isEmpty()) {
            return false;
        }
        if (body.length() > MAX_BODY_LENGTH) {
            return false;
        }
        this.body = body;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Marks the question as resolved with a specific answer.
     * 
     *answerId The ID of the answer that resolved the question
     */
    public void markAsResolved(String answerId) {
        this.resolved = true;
        this.resolvedAnswerId = answerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the question as unresolved.
     */
    public void markAsUnresolved() {
        this.resolved = false;
        this.resolvedAnswerId = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Increments the total answer count.
     * Called when a new answer is added to this question.
     */
    public void incrementTotalAnswers() {
        this.totalAnswers++;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Decrements the total answer count.
     * Called when an answer is deleted from this question.
     */
    public void decrementTotalAnswers() {
        if (this.totalAnswers > 0) {
            this.totalAnswers--;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Increments the new answer count.
     * Called when a new answer is added that the question author hasn't read.
     */
    public void incrementNewAnswers() {
        this.newAnswers++;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Resets the new answer count to zero.
     * Called when the question author views all answers.
     */
    public void resetNewAnswers() {
        this.newAnswers = 0;
    }
    
    //Utility Methods
    
    /**
     * Returns a string showing how long ago the question was posted.
     * Examples: "1h" (1 hour ago), "2d" (2 days ago), "1w" (1 week ago)
     * 
     * return Formatted time string
     */
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long weeks = ChronoUnit.WEEKS.between(createdAt, now);
        
        if (minutes < 60) {
            return minutes + "m";
        } else if (hours < 24) {
            return hours + "h";
        } else if (days < 7) {
            return days + "d";
        } else {
            return weeks + "w";
        }
    }
    
    /**
     * Returns a formatted date string for display.
     * Format: "MMM dd, yyyy" (e.g., "Sep 29, 2025")
     * 
     * return Formatted date string
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return createdAt.format(formatter);
    }
    
    /**
     * Returns a formatted timestamp for display.
     * Format: "MMM dd, yyyy HH:mm" (e.g., "Oct 15, 2025 14:30")
     * 
     * return Formatted timestamp string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdAt.format(formatter);
    }
    
    /**
     * Validates the title length.
     * 
     * title Title to validate
     * return Error message if invalid, empty string if valid
     */
    public static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Title cannot be empty";
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            return "You have exceeded the maximum character limit";
        }
        return "";
    }
    
    /**
     * Validates the body length.
     * 
     * body Body to validate
     * return Error message if invalid, empty string if valid
     */
    public static String validateBody(String body) {
        if (body == null || body.trim().isEmpty()) {
            return "Question body cannot be empty";
        }
        if (body.length() > MAX_BODY_LENGTH) {
            return "You have exceeded the maximum character limit";
        }
        return "";
    }
    
    /**
     * Gets the maximum allowed title length.
     * 
     * return Maximum title length
     */
    public static int getMaxTitleLength() {
        return MAX_TITLE_LENGTH;
    }
    
    /**
     * Gets the maximum allowed body length.
     * 
     * return Maximum body length
     */
    public static int getMaxBodyLength() {
        return MAX_BODY_LENGTH;
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id='" + questionId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", createdAt=" + getFormattedTimestamp() +
                ", resolved=" + resolved +
                ", totalAnswers=" + totalAnswers +
                ", newAnswers=" + newAnswers +
                '}';
    }
}