package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Implements our Answer class which represents a single answer to a question in the Q&A system.
 * Stores answer data including content, author, timestamps, and resolved status.
 */
public class Answer {
    
    // Maximum character limit as per requirements
    private static final int MAX_CONTENT_LENGTH = 5000;
    
    // Answer attributes
    private String answerId;             // Unique identifier for the answer
    private String questionId;           // ID of the question this answer belongs to
    private String content;              // Answer content (max 5000 characters)
    private String author;               // Username of the person who answered
    private LocalDateTime createdAt;     // Timestamp when answer was created
    private LocalDateTime updatedAt;     // Timestamp when answer was last updated
    private boolean markedAsResolved;    // Whether this answer resolved the question
    
    /**
     * Constructor for creating a new answer.
     * Automatically generates a unique ID and sets creation timestamp.
     * 
     * questionId The ID of the question being answered
     * content The answer content (max 5000 characters)
     * author The username of the person answering
     */
    public Answer(String questionId, String content, String author) {
        this.answerId = UUID.randomUUID().toString();
        this.questionId = questionId;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.markedAsResolved = false;
    }
    
    /**
     * Constructor for loading an existing answer from storage.
     * Used when retrieving answers from a database or file.
     * 
     * answerId Unique identifier
     * questionId Associated question ID
     * content Answer content
     * author Answer author
     * createdAt Creation timestamp
     * updatedAt Last update timestamp
     * markedAsResolved Resolution status
     */
    public Answer(String answerId, String questionId, String content, String author,
                 LocalDateTime createdAt, LocalDateTime updatedAt, boolean markedAsResolved) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.markedAsResolved = markedAsResolved;
    }
    
    //Getters 
    
    public String getAnswerId() {
        return answerId;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public String getContent() {
        return content;
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
    
    public boolean isMarkedAsResolved() {
        return markedAsResolved;
    }
    
    // Setters with Validation 
    
    /**
     * Sets the answer content with validation.
     * 
     * content New content (must not exceed MAX_CONTENT_LENGTH)
     * return true if content was set successfully, false if validation failed
     */
    public boolean setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            return false;
        }
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Marks this answer as the one that resolved the question.
     */
    public void markAsResolved() {
        this.markedAsResolved = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Unmarks this answer as the resolving answer.
     */
    public void unmarkAsResolved() {
        this.markedAsResolved = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Utility Methods 
    
    /**
     * Returns time string showing how long ago the answer was posted.
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
     * Format: "MMM dd, yyyy HH:mm" (e.g., "Sep 29, 2025 14:30")
     * 
     * return Formatted timestamp string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdAt.format(formatter);
    }
    
    /**
     * Validates the answer content length.
     * 
     * content Content to validate
     * return Error message if invalid, empty string if valid
     */
    public static String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Answer cannot be empty";
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            return "You have exceeded the maximum character limit";
        }
        return "";
    }
    
    /**
     * Gets the maximum allowed content length.
     * 
     * return Maximum content length
     */
    public static int getMaxContentLength() {
        return MAX_CONTENT_LENGTH;
    }
    
    @Override
    public String toString() {
        return "Answer{" +
                "id='" + answerId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", author='" + author + '\'' +
                ", createdAt=" + getFormattedTimestamp() +
                ", markedAsResolved=" + markedAsResolved +
                '}';
    }
}