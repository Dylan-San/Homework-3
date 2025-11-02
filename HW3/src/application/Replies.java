package application;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Replies class manages a collection of all replies in the Q&A system.
 */
public class Replies {
    
    private Map<String, Reply> repliesMap;
    private Map<String, List<String>> answerToRepliesMap;
    
    public Replies() {
        this.repliesMap = new HashMap<>();
        this.answerToRepliesMap = new HashMap<>();
    }
    
    // CREATE
    public boolean addReply(Reply reply) {
        if (reply == null || reply.getReplyID() == null) {
            return false;
        }
        if (repliesMap.containsKey(reply.getReplyID())) {
            return false;
        }
        
        repliesMap.put(reply.getReplyID(), reply);
        
        String answerId = reply.getAnswerID();
        answerToRepliesMap.putIfAbsent(answerId, new ArrayList<>());
        answerToRepliesMap.get(answerId).add(reply.getReplyID());
        
        return true;
    }
    
 // READ
    public Reply getReplyById(String replyId) {
        return repliesMap.get(replyId);
    }
    
    public List<Reply> getAllReplies() {
        return new ArrayList<>(repliesMap.values());
    }
    
    // UPDATE
    public boolean updateReply(Reply reply) {
        if (reply == null || reply.getReplyID() == null) {
            return false;
        }
        if (!repliesMap.containsKey(reply.getReplyID())) {
            return false;
        }
        repliesMap.put(reply.getReplyID(), reply);
        return true;
    }
    
    // DELETE
    public boolean deleteReply(String replyId) {
        if (replyId == null || !repliesMap.containsKey(replyId)) {
            return false;
        }
        
        Reply reply = repliesMap.get(replyId);
        String answerId = reply.getAnswerID();
        
        repliesMap.remove(replyId);
        
        if (answerToRepliesMap.containsKey(answerId)) {
            answerToRepliesMap.get(answerId).remove(replyId);
            
            if (answerToRepliesMap.get(answerId).isEmpty()) {
                answerToRepliesMap.remove(answerId);
            }
        }
        
        return true;
    }
    
    public List<Reply> getRepliesForAnswers(String answerId) {
        if (answerId == null || !answerToRepliesMap.containsKey(answerId)) {
            return new ArrayList<>();
        }
        
        List<String> replyIds = answerToRepliesMap.get(answerId);
        List<Reply> replies = new ArrayList<>();
        
        for (String replyId : replyIds) {
            Reply reply = repliesMap.get(replyId);
            if (reply != null) {
                replies.add(reply);
            }
        }
        return replies; 
    }
    
    //Get count of replies for each answer
    public int getReplyCountForAnswer(String answerId) {
        if (answerId == null || !answerToRepliesMap.containsKey(answerId)) {
            return 0;
        }
        return answerToRepliesMap.get(answerId).size();
    }
    
    //Delete all replies for a specific answer
    
    public int deleteAllRepliesForAnswer(String answerId) {
        if (answerId == null || !answerToRepliesMap.containsKey(answerId)) {
            return 0;
        }
        List<String> replyIds = new ArrayList<>(answerToRepliesMap.get(answerId));
        int deletedCount = 0;
        
        for (String replyId : replyIds) {
            if (deleteReply(replyId)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    public int getReplyCount() {
        return repliesMap.size();
    }
    
    public boolean replyExists(String replyId) {
        return repliesMap.containsKey(replyId);
    }
    
    public void clearAllReplies() {
        repliesMap.clear();
        answerToRepliesMap.clear();
    }
    
    @Override
    public String toString() {
        return "Replies{" +
                "total=" + repliesMap.size() +
                ", answers with replies=" + answerToRepliesMap.size() +
                '}';
    }
}
    
    
    
    
    
    
    
    