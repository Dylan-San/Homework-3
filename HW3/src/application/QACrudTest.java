package application;

/**
 * Simple CRUD Test for Q&A System
 * Tests Create, Read, Update, Delete operations for Questions and Answers
 */
public class QACrudTest {
    
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("Q&A System CRUD Functionality Test");
        System.out.println("==========================================");
        System.out.println();
        
        // Initialize collections
        Questions questions = new Questions();
        Answers answers = new Answers();
        
        // Run test cases
        testCase1_CreateQuestion(questions);
        testCase2_ReadQuestion(questions);
        testCase3_CreateAnswerAndUpdate(questions, answers);
        testCase4_MarkAsResolved(questions, answers);
        testCase5_DeleteOperations(questions, answers);
        
        // Print summary
        printSummary();
    }
    
    // Test Case 1: CREATE - Add a new question
    private static void testCase1_CreateQuestion(Questions questions) {
        System.out.println("TEST CASE 1: CREATE Question");
        System.out.println("----------------------------");
        System.out.println("Question: Testing question creation");
        System.out.println("Expected: Question object created with title, body, author, and unique ID");
        System.out.println();
        
        // Create a question
        Question question = new Question(
            "How do I implement JavaFX TableView?",
            "I need help understanding JavaFX TableView implementation with custom cell factories",
            "testuser"
        );
        
        boolean added = questions.addQuestion(question);
        
        // Output
        System.out.println("Output:");
        System.out.println("  Question Added: " + added);
        System.out.println("  ID: " + question.getQuestionId());
        System.out.println("  Title: " + question.getTitle());
        System.out.println("  Author: " + question.getAuthor());
        System.out.println("  Resolved: " + question.isResolved());
        System.out.println();
        
        // Verify
        boolean passed = added && 
                        question.getQuestionId() != null && 
                        question.getTitle().equals("How do I implement JavaFX TableView?");
        
        printResult(passed);
        System.out.println();
    }
    
    // Test Case 2: READ - Retrieve question by ID
    private static void testCase2_ReadQuestion(Questions questions) {
        System.out.println("TEST CASE 2: READ Question by ID");
        System.out.println("----------------------------");
        System.out.println("Question: Can we retrieve a question using its ID?");
        System.out.println("Expected: Question object retrieved with all properties intact");
        System.out.println();
        
        // Get the first question
        Question original = questions.getAllQuestions().get(0);
        String questionId = original.getQuestionId();
        
        // Retrieve it by ID
        Question retrieved = questions.getQuestionById(questionId);
        
        // Output
        System.out.println("Output:");
        System.out.println("  Question Retrieved: " + (retrieved != null));
        if (retrieved != null) {
            System.out.println("  ID: " + retrieved.getQuestionId());
            System.out.println("  Title: " + retrieved.getTitle());
            System.out.println("  Author: " + retrieved.getAuthor());
        }
        System.out.println();
        
        // Verify
        boolean passed = retrieved != null && 
                        retrieved.getQuestionId().equals(questionId) &&
                        retrieved.getTitle().equals(original.getTitle());
        
        printResult(passed);
        System.out.println();
    }
    
    // Test Case 3: CREATE Answer and UPDATE Question count
    private static void testCase3_CreateAnswerAndUpdate(Questions questions, Answers answers) {
        System.out.println("TEST CASE 3: CREATE Answer + UPDATE Question");
        System.out.println("----------------------------");
        System.out.println("Question: Can we add an answer and update question's answer count?");
        System.out.println("Expected: Answer created and question's totalAnswers incremented");
        System.out.println();
        
        // Get the question
        Question question = questions.getAllQuestions().get(0);
        String questionId = question.getQuestionId();
        int initialCount = question.getTotalAnswers();
        
        // Create an answer
        Answer answer = new Answer(
            questionId,
            "You can use TableView with setCellValueFactory and custom cell factories. Here's an example using PropertyValueFactory.",
            "helper"
        );
        
        boolean added = answers.addAnswer(answer);
        
        // Update question's answer count
        question.incrementTotalAnswers();
        questions.updateQuestion(question);
        
        // Output
        System.out.println("Output:");
        System.out.println("  Answer Added: " + added);
        System.out.println("  Answer ID: " + answer.getAnswerId());
        System.out.println("  Answer Author: " + answer.getAuthor());
        System.out.println("  Initial Answer Count: " + initialCount);
        System.out.println("  Updated Answer Count: " + question.getTotalAnswers());
        System.out.println();
        
        // Verify
        boolean passed = added && 
                        answer.getAnswerId() != null &&
                        question.getTotalAnswers() == (initialCount + 1);
        
        printResult(passed);
        System.out.println();
    }
    
    // Test Case 4: UPDATE - Mark answer as resolved
    private static void testCase4_MarkAsResolved(Questions questions, Answers answers) {
        System.out.println("TEST CASE 4: UPDATE - Mark Answer as Resolved");
        System.out.println("----------------------------");
        System.out.println("Question: Can we mark an answer as resolving the question?");
        System.out.println("Expected: Answer marked as resolved, question status updated to resolved");
        System.out.println();
        
        // Get question and answer
        Question question = questions.getAllQuestions().get(0);
        Answer answer = answers.getAllAnswers().get(0);
        
        // Mark answer as resolved
        answer.markAsResolved();
        answers.updateAnswer(answer);
        
        // Mark question as resolved
        question.markAsResolved(answer.getAnswerId());
        questions.updateQuestion(question);
        
        // Output
        System.out.println("Output:");
        System.out.println("  Answer Marked as Resolved: " + answer.isMarkedAsResolved());
        System.out.println("  Question Marked as Resolved: " + question.isResolved());
        System.out.println("  Resolved Answer ID: " + question.getResolvedAnswerId());
        System.out.println();
        
        // Verify
        boolean passed = answer.isMarkedAsResolved() && 
                        question.isResolved() &&
                        question.getResolvedAnswerId().equals(answer.getAnswerId());
        
        printResult(passed);
        System.out.println();
    }
    
    // Test Case 5: DELETE - Remove answer and question
    private static void testCase5_DeleteOperations(Questions questions, Answers answers) {
        System.out.println("TEST CASE 5: DELETE Answer and Question");
        System.out.println("----------------------------");
        System.out.println("Question: Can we delete an answer and then the question?");
        System.out.println("Expected: Answer deleted, then question deleted successfully");
        System.out.println();
        
        // Get IDs
        Question question = questions.getAllQuestions().get(0);
        Answer answer = answers.getAllAnswers().get(0);
        String questionId = question.getQuestionId();
        String answerId = answer.getAnswerId();
        
        // Delete answer
        boolean answerDeleted = answers.deleteAnswer(answerId);
        
        // Delete question
        boolean questionDeleted = questions.deleteQuestion(questionId);
        
        // Verify deletion
        boolean answerExists = answers.answerExists(answerId);
        boolean questionExists = questions.questionExists(questionId);
        
        // Output
        System.out.println("Output:");
        System.out.println("  Answer Deleted: " + answerDeleted);
        System.out.println("  Question Deleted: " + questionDeleted);
        System.out.println("  Answer Still Exists: " + answerExists);
        System.out.println("  Question Still Exists: " + questionExists);
        System.out.println();
        
        // Verify
        boolean passed = answerDeleted && 
                        questionDeleted && 
                        !answerExists && 
                        !questionExists;
        
        printResult(passed);
        System.out.println();
    }
    
    // Helper method to print test result
    private static void printResult(boolean passed) {
        totalTests++;
        if (passed) {
            System.out.println("Status: PASS ✓");
            passedTests++;
        } else {
            System.out.println("Status: FAIL ✗");
            failedTests++;
        }
    }
    
    // Print test summary
    private static void printSummary() {
        System.out.println("==========================================");
        System.out.println("TEST SUMMARY");
        System.out.println("==========================================");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println();
        
        if (failedTests == 0) {
            System.out.println("Result: ALL TESTS PASSED ✓✓✓");
        } else {
            System.out.println("Result: SOME TESTS FAILED ✗");
        }
    }
}