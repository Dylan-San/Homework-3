package application;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements our Answers class and manages a collection of all answers in the Q&A system.
 * Provides CRUD operations and filtering capabilities for answers.
 */
public class Answers {
    
    private Map<String, Answer> answersMap;
    private Map<String, List<String>> questionToAnswersMap;
    
    public Answers() {
        this.answersMap = new HashMap<>();
        this.questionToAnswersMap = new HashMap<>();
    }
    
    // CREATE
    public boolean addAnswer(Answer answer) {
        if (answer == null || answer.getAnswerId() == null) {
            return false;
        }
        if (answersMap.containsKey(answer.getAnswerId())) {
            return false;
        }
        
        answersMap.put(answer.getAnswerId(), answer);
        
        String questionId = answer.getQuestionId();
        questionToAnswersMap.putIfAbsent(questionId, new ArrayList<>());
        questionToAnswersMap.get(questionId).add(answer.getAnswerId());
        
        return true;
    }
    
    // READ
    public Answer getAnswerById(String answerId) {
        return answersMap.get(answerId);
    }
    
    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answersMap.values());
    }
    
    // UPDATE
    public boolean updateAnswer(Answer answer) {
        if (answer == null || answer.getAnswerId() == null) {
            return false;
        }
        if (!answersMap.containsKey(answer.getAnswerId())) {
            return false;
        }
        answersMap.put(answer.getAnswerId(), answer);
        return true;
    }
    
    // DELETE
    public boolean deleteAnswer(String answerId) {
        if (answerId == null || !answersMap.containsKey(answerId)) {
            return false;
        }
        
        Answer answer = answersMap.get(answerId);
        String questionId = answer.getQuestionId();
        
        answersMap.remove(answerId);
        
        if (questionToAnswersMap.containsKey(questionId)) {
            questionToAnswersMap.get(questionId).remove(answerId);
            
            if (questionToAnswersMap.get(questionId).isEmpty()) {
                questionToAnswersMap.remove(questionId);
            }
        }
        
        return true;
    }
    
    // Question-specific operations
    public List<Answer> getAnswersForQuestion(String questionId) {
        if (questionId == null || !questionToAnswersMap.containsKey(questionId)) {
            return new ArrayList<>();
        }
        
        List<String> answerIds = questionToAnswersMap.get(questionId);
        List<Answer> answers = new ArrayList<>();
        
        for (String answerId : answerIds) {
            Answer answer = answersMap.get(answerId);
            if (answer != null) {
                answers.add(answer);
            }
        }
        
        answers.sort(Comparator.comparing(Answer::getCreatedAt));
        return answers;
    }
    
    public List<Answer> getAnswersForQuestionWithResolvedFirst(String questionId, String resolvedAnswerId) {
        List<Answer> answers = getAnswersForQuestion(questionId);
        
        if (resolvedAnswerId == null || answers.isEmpty()) {
            return answers;
        }
        
        Answer resolvedAnswer = null;
        List<Answer> otherAnswers = new ArrayList<>();
        
        for (Answer answer : answers) {
            if (answer.getAnswerId().equals(resolvedAnswerId)) {
                resolvedAnswer = answer;
            } else {
                otherAnswers.add(answer);
            }
        }
        
        List<Answer> result = new ArrayList<>();
        if (resolvedAnswer != null) {
            result.add(resolvedAnswer);
        }
        result.addAll(otherAnswers);
        
        return result;
    }
    
    public int getAnswerCountForQuestion(String questionId) {
        if (questionId == null || !questionToAnswersMap.containsKey(questionId)) {
            return 0;
        }
        return questionToAnswersMap.get(questionId).size();
    }
    
    public List<Answer> getAnswersByAuthor(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return answersMap.values().stream()
                .filter(a -> a.getAuthor().equals(username))
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    
    public boolean hasAnswers(String questionId) {
        return questionId != null && 
               questionToAnswersMap.containsKey(questionId) && 
               !questionToAnswersMap.get(questionId).isEmpty();
    }
    
    public int deleteAllAnswersForQuestion(String questionId) {
        if (questionId == null || !questionToAnswersMap.containsKey(questionId)) {
            return 0;
        }
        
        List<String> answerIds = new ArrayList<>(questionToAnswersMap.get(questionId));
        int deletedCount = 0;
        
        for (String answerId : answerIds) {
            if (deleteAnswer(answerId)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    public int getAnswerCount() {
        return answersMap.size();
    }
    
    public boolean answerExists(String answerId) {
        return answersMap.containsKey(answerId);
    }
    
    public void clearAllAnswers() {
        answersMap.clear();
        questionToAnswersMap.clear();
    }
    
    @Override
    public String toString() {
        return "Answers{" +
                "total=" + answersMap.size() +
                ", questions with answers=" + questionToAnswersMap.size() +
                '}';
    }
}