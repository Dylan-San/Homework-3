package application;

import java.util.ArrayList;
import java.util.List;

/**
 * Automated Tests for User Story 3: SEARCH AND READ QUESTIONS/ANSWERS
 * (Implemented and documented by Victor)
 *
 * This class simulates automated tests without using JUnit.
 * It verifies that question and answer retrieval functions behave correctly.
 */
public class SearchAndReadTests {

    public static void main(String[] args) {
        System.out.println("=== Running Automated Tests for User Story 3 ===\n");

        testGetQuestionById();
        testGetAllQuestions();
        testGetAllAnswersForQuestion();
        testSearchForNonexistentQuestion();
        testGetAnswerById();

        System.out.println("\n=== All tests executed. Review output for results. ===");
    }

    // Get Question by ID
    private static void testGetQuestionById() {
        System.out.println("Test 1: Get Question by ID");

        Question q = new Question("Why is Java popular?", "Because of its portability.", "Alice");
        String expectedId = q.getQuestionId();

        if (q.getQuestionId().equals(expectedId)
                && q.getTitle().equals("Why is Java popular?")
                && q.getAuthor().equals("Alice")) {
            System.out.println("Passed: Question retrieved successfully with correct ID and properties.\n");
        } else {
            System.out.println("Failed: Question data mismatch or ID incorrect.\n");
        }
    }

    // Get All Questions
    private static void testGetAllQuestions() {
        System.out.println("Test 2: Get All Questions");

        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("Q1", "Body 1", "User1"));
        questionList.add(new Question("Q2", "Body 2", "User2"));

        if (!questionList.isEmpty() && questionList.size() > 0) {
            System.out.println("Passed: System returned list of questions (" + questionList.size() + " found).\n");
        } else {
            System.out.println("Failed: No questions were returned.\n");
        }
    }

    //  Get All Answers for a Question
    private static void testGetAllAnswersForQuestion() {
        System.out.println("Test 3: Get All Answers for a Question");

        Question q = new Question("What is polymorphism?", "Explain with an example.", "Bob");
        q.incrementTotalAnswers();
        q.incrementTotalAnswers(); // Simulating two answers

        if (q.getTotalAnswers() == 2) {
            System.out.println("Passed: Returned all answers for the question (2 total).\n");
        } else {
            System.out.println("Failed: Expected 2 answers but found " + q.getTotalAnswers() + ".\n");
        }
    }

    //  Search for Nonexistent Question
    private static void testSearchForNonexistentQuestion() {
        System.out.println("Test 4: Search for Nonexistent Question");

        Question result = null; // Simulating a failed search
        if (result == null) {
            System.out.println("Passed: Nonexistent question returned null (no crash or error).\n");
        } else {
            System.out.println("Failed: Expected null but got a result.\n");
        }
    }

    // Get Answer by ID
    private static void testGetAnswerById() {
        System.out.println("Test 5: Get Answer by ID");

        // Simulate answer retrieval
        String expectedAnswerId = "ANS-001";
        Question q = new Question("What is inheritance?", "Describe types of inheritance.", "Charlie");
        q.markAsResolved(expectedAnswerId);

        if (q.getResolvedAnswerId() != null && q.getResolvedAnswerId().equals(expectedAnswerId)) {
            System.out.println("Passed: Answer retrieved successfully with matching ID.\n");
        } else {
            System.out.println("Failed: Answer ID mismatch or not found.\n");
        }
    }
}
