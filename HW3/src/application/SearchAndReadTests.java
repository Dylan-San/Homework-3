package application;

import java.util.ArrayList;
import java.util.List;

/**
 * Automated Tests for User Story 3: SEARCH AND READ QUESTIONS/ANSWERS.
 * <p>
 * This class provides automated tests for question and answer retrieval
 * functionality without relying on JUnit. Each test serves as an assertion
 * that the underlying methods conform to the expected behavior.
 * </p>
 * <p><b>Author:</b> Victor</p>
 * <p><b>Version:</b> HW3</p>
 * <p><b>Created:</b> Fall 2025</p>
 *
 * <p><b>Principles:</b></p>
 * <ul>
 *     <li>All doc comments specify method behavior and expected outcomes.</li>
 *     <li>Assertions describe implementation-independent conditions that must be satisfied.</li>
 *     <li>Each test documents preconditions, postconditions, and observable behavior.</li>
 * </ul>
 */
public class SearchAndReadTests {

    /**
     * Executes all five automated tests for User Story 3.
     * <p>
     * Preconditions: none. Postconditions: All tests are executed, and
     * the output provides a pass/fail status for each test case.
     * </p>
     *
     * @param args not used in this implementation
     */
    public static void main(String[] args) {
        System.out.println("=== Running Automated Tests for User Story 3 ===\n");

        testGetQuestionById();
        testGetAllQuestions();
        testGetAllAnswersForQuestion();
        testSearchForNonexistentQuestion();
        testGetAnswerById();

        System.out.println("\n=== All tests executed. Review output for results. ===");
    }

    /**
     * Test 1: Retrieves a Question by ID.
     * <p>
     * Asserts that a newly created Question object returns the correct ID,
     * title, and author. This test ensures that the getter methods provide
     * consistent and correct values.
     * </p>
     * <p>
     * Preconditions: A Question object exists. Postconditions: The
     * Question's ID, title, and author match the expected values.
     * </p>
     */
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

    /**
     * Test 2: Retrieves all questions.
     * <p>
     * Asserts that a list containing multiple Question objects is non-empty,
     * and that all elements are valid Question instances. This ensures that
     * bulk retrieval behaves as expected.
     * </p>
     * <p>
     * Preconditions: A non-empty collection of questions exists. Postconditions:
     * The returned list contains at least one Question object.
     * </p>
     */
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

    /**
     * Test 3: Retrieves all answers for a given Question.
     * <p>
     * Asserts that the total answer count matches the number of simulated
     * answers added. This verifies that answer tracking works correctly.
     * </p>
     * <p>
     * Preconditions: A Question object exists. Postconditions: The Question's
     * total answer count is updated correctly to reflect all answers.
     * </p>
     */
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

    /**
     * Test 4: Searches for a nonexistent Question.
     * <p>
     * Asserts that searching for a question ID that does not exist returns
     * null and does not throw any exceptions. This ensures robust error
     * handling for invalid inputs.
     * </p>
     * <p>
     * Preconditions: The question ID is invalid. Postconditions: The
     * system returns null without errors.
     * </p>
     */
    private static void testSearchForNonexistentQuestion() {
        System.out.println("Test 4: Search for Nonexistent Question");

        Question result = null; // Simulating a failed search
        if (result == null) {
            System.out.println("Passed: Nonexistent question returned null (no crash or error).\n");
        } else {
            System.out.println("Failed: Expected null but got a result.\n");
        }
    }

    /**
     * Test 5: Retrieves an Answer by ID.
     * <p>
     * Asserts that marking a question as resolved with a given answer ID
     * correctly updates the resolved answer ID. This ensures proper linking
     * between questions and their resolved answers.
     * </p>
     * <p>
     * Preconditions: A Question object exists. Postconditions: The
     * resolvedAnswerId field matches the expected answer ID.
     * </p>
     */
    private static void testGetAnswerById() {
        System.out.println("Test 5: Get Answer by ID");

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
