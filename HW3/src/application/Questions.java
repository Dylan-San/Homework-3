package application;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Our Questions class manages a collection of all questions in the Q&A system.
 * Provides CRUD operations and filtering/searching capabilities for questions.
 */
public class Questions {
    
    // Store all questions in a HashMap 
    private Map<String, Question> questionsMap;
    
    /**
     * Constructor initializes an empty questions collection.
     */
    public Questions() {
        this.questionsMap = new HashMap<>();
    }
    
    //CRUD Operations
    
    /**
     * CREATE: Adds a new question to the collection.
     * 
     * question The question to add
     * return true if question was added successfully, false if question with same ID already exists
     */
    public boolean addQuestion(Question question) {
        if (question == null || question.getQuestionId() == null) {
            return false;
        }
        
        // Check if question with this ID already exists
        if (questionsMap.containsKey(question.getQuestionId())) {
            return false;
        }
        
        questionsMap.put(question.getQuestionId(), question);
        return true;
    }
    
    /**
     * READ: Retrieves a question by its ID.
     * 
     * questionId The unique ID of the question
     * return The question object, or null if not found
     */
    public Question getQuestionById(String questionId) {
        return questionsMap.get(questionId);
    }
    
    /**
     * READ: Retrieves all questions in the system.
     * 
     * return List of all questions
     */
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questionsMap.values());
    }
    
    /**
     * UPDATE: Updates an existing question in the collection.
     * Note: The question's ID cannot be changed.
     * 
     * question The updated question object
     * return true if question was updated successfully, false if question doesn't exist
     */
    public boolean updateQuestion(Question question) {
        if (question == null || question.getQuestionId() == null) {
            return false;
        }
        
        // Check if question exists
        if (!questionsMap.containsKey(question.getQuestionId())) {
            return false;
        }
        
        questionsMap.put(question.getQuestionId(), question);
        return true;
    }
    
    /**
     * DELETE: Removes a question from the collection.
     * 
     * questionId The ID of the question to delete
     * return true if question was deleted successfully, false if question doesn't exist
     */
    public boolean deleteQuestion(String questionId) {
        if (questionId == null || !questionsMap.containsKey(questionId)) {
            return false;
        }
        
        questionsMap.remove(questionId);
        return true;
    }
    
    //Filtering and Search Operations
    
    /**
     * Gets all questions, sorted by most recent first.
     * 
     * return List of questions sorted by creation date (newest first)
     */
    public List<Question> getAllQuestionsSortedByMostRecent() {
        return questionsMap.values().stream()
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all unresolved questions.
     * 
     * return List of unresolved questions
     */
    public List<Question> getUnresolvedQuestions() {
        return questionsMap.values().stream()
                .filter(q -> !q.isResolved())
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all resolved (answered) questions.
     * 
     * return List of resolved questions
     */
    public List<Question> getResolvedQuestions() {
        return questionsMap.values().stream()
                .filter(Question::isResolved)
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all questions with at least one answer.
     * 
     * return List of answered questions
     */
    public List<Question> getAnsweredQuestions() {
        return questionsMap.values().stream()
                .filter(q -> q.getTotalAnswers() > 0)
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all questions with no answers.
     * 
     * return List of unanswered questions
     */
    public List<Question> getUnansweredQuestions() {
        return questionsMap.values().stream()
                .filter(q -> q.getTotalAnswers() == 0)
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all unresolved questions asked by a specific user.
     * 
     * username The username of the question author
     * return List of user's unresolved questions
     */
    public List<Question> getMyUnresolvedQuestions(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return questionsMap.values().stream()
                .filter(q -> q.getAuthor().equals(username) && !q.isResolved())
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all questions asked by a specific user.
     * 
     * username The username of the question author
     * return List of user's questions
     */
    public List<Question> getQuestionsByAuthor(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return questionsMap.values().stream()
                .filter(q -> q.getAuthor().equals(username))
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches questions by keywords in title or body.
     * Case-insensitive search that matches any keyword in the query.
     * 
     * searchQuery The search keywords (max 150 characters)
     * return List of matching questions
     */
    public List<Question> searchQuestions(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Validate search query length
        if (searchQuery.length() > Question.getMaxTitleLength()) {
            return new ArrayList<>();
        }
        
        String queryLower = searchQuery.toLowerCase().trim();
        String[] keywords = queryLower.split("\\s+");
        
        return questionsMap.values().stream()
                .filter(q -> {
                    String titleLower = q.getTitle().toLowerCase();
                    String bodyLower = q.getBody().toLowerCase();
                    
                    // Check if any keyword matches title or body
                    for (String keyword : keywords) {
                        if (titleLower.contains(keyword) || bodyLower.contains(keyword)) {
                            return true;
                        }
                    }
                    return false;
                })
                .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches questions by keywords with additional filter.
     * 
     * searchQuery The search keywords
     * filter Filter option: "all", "unresolved", "answered", "unanswered"
     * return List of matching questions with filter applied
     */
    public List<Question> searchQuestionsWithFilter(String searchQuery, String filter) {
        List<Question> searchResults = searchQuestions(searchQuery);
        
        if (filter == null || filter.equalsIgnoreCase("all")) {
            return searchResults;
        }
        
        switch (filter.toLowerCase()) {
            case "unresolved":
                return searchResults.stream()
                        .filter(q -> !q.isResolved())
                        .collect(Collectors.toList());
            case "answered":
                return searchResults.stream()
                        .filter(q -> q.getTotalAnswers() > 0)
                        .collect(Collectors.toList());
            case "unanswered":
                return searchResults.stream()
                        .filter(q -> q.getTotalAnswers() == 0)
                        .collect(Collectors.toList());
            default:
                return searchResults;
        }
    }
   
    //Utility Methods
    
    /**
     * Gets the total number of questions in the system.
     * 
     * return Total question count
     */
    public int getQuestionCount() {
        return questionsMap.size();
    }
    
    /**
     * Checks if a question exists in the collection.
     * 
     * questionId The ID of the question to check
     * return true if question exists, false otherwise
     */
    public boolean questionExists(String questionId) {
        return questionsMap.containsKey(questionId);
    }
    
    /**
     * Removes all questions from the collection.
     * Used primarily for testing purposes.
     */
    public void clearAllQuestions() {
        questionsMap.clear();
    }
    
    @Override
    public String toString() {
        return "Questions{" +
                "total=" + questionsMap.size() +
                ", unresolved=" + getUnresolvedQuestions().size() +
                ", answered=" + getAnsweredQuestions().size() +
                '}';
    }
}