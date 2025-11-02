package application;

import java.util.UUID; ;

public class Reply {
	
	public static final int MAX_CONTENT_LENGTH = 2000; 
	
	private String replyID; 
	private String answerID; 
	private String content;
	private String author; 
	
	 //Constructor for creating a new reply to an answer and unique ID generation
	public Reply(String answerID, String content, String author) {
		this.replyID = UUID.randomUUID().toString(); 
		this.answerID = answerID; 
		this.content = content; 
		this.author = author; 
		
	}
	
	//Getters for answer replies class
	public String getReplyID() { return replyID; }
	public String getAnswerID() { return answerID; }
	public String getContent() { return content; }
	public String getAuthor() { return author; }
	
	//Setter with validation
	public boolean setContent(String content) {
		if(content == null || content.trim().isEmpty()) {
			return false; 
		}
		this.content = content; 
		return true; 
	}
	
	public static String validateContent(String content) {
		if(content == null || content.trim().isEmpty()) {
			return "Reply cannot be empty"; 
		}
		if(content.length() > MAX_CONTENT_LENGTH) {
			return "You have exceeded the maximum character limit";
		}
		return ""; 
	}
	
	@Override
    public String toString() {
        return "Reply{" +
                "id='" + replyID + '\'' +
                ", answerID='" + answerID + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
	
	
	
	

}