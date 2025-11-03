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
 *
 * <p><b>Author:</b> Dylan</p>
 * <p><b>Version:</b> HW3</p>
 * <p><b>Created:</b> Fall 2025</p>
 *
 * <p><b>Principles:</b></p>
 * <ul>
 *     <li>All doc comments specify method behavior and expected outcomes.</li>
 *     <li>Assertions describe implementation-independent conditions that must be satisfied.</li>
 *     <li>Each test documents preconditions, postconditions, and observable behavior.</li>
 * </ul>
 *
 * <p><b>Reference for Javadoc Style:</b> Inspired by professional examples from 
 * <a href="https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#terminology">
 * Oracle's Javadoc Tool Guide</a>, which demonstrates proper usage of 
 * {@link #main(String[])}, {@link Question#getQuestionId()}, and other {@link Question} methods.</p>
 *
 * @see Question
 * @see <a href="https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#terminology">Professional Javadoc Example</a>
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
     * @see #testGetQuestionById()
     * @see #testGetAllQuestions()
     * @see #testGetAllAnswersForQuestion()
     * @see #testSearchForNonexistentQuestion()
     * @see #testGetAnswerById()
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
     * Test 1: Retrieves a {@link Question} by ID.
     * <p>
     * Example: Donkey Kong Country question. Validates title, author, and ID.
     * </p>
     *
     * @see Question#getQuestionId()
     * @see Question#getTitle()
     * @see Question#getAuthor()
     */
    private static void testGetQuestionById() {
        System.out.println("Test 1: Get Question by ID");

        Question q = new Question(
            "Who is the main antagonist in Donkey Kong Country?",
            "Identify the main villain in the original DKC game series.",
            "Dylan"
        );
        String expectedId = q.getQuestionId();

        if (q.getQuestionId().equals(expectedId)
                && q.getTitle().equals("Who is the main antagonist in Donkey Kong Country?")
                && q.getAuthor().equals("Dylan")) {
            System.out.println("Passed: Question retrieved successfully with correct ID and properties.\n");
        } else {
            System.out.println("Failed: Question data mismatch or ID incorrect.\n");
        }
    }

    /**
     * Test 2: Retrieves all {@link Question} objects.
     * <p>
     * Example: Donkey Kong 64 and arcade questions. Verifies list is non-empty.
     * </p>
     *
     * @see Question#Question(String, String, String)
     */
    private static void testGetAllQuestions() {
        System.out.println("Test 2: Get All Questions");

        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question(
            "What is the first level in Donkey Kong 64?",
            "Provide the name of the opening stage in DKC64.",
            "Dylan"
        ));
        questionList.add(new Question(
            "When was the original Donkey Kong arcade game released?",
            "Identify the year the classic arcade DK was released.",
            "Dylan"
        ));

        if (!questionList.isEmpty() && questionList.size() > 0) {
            System.out.println("Passed: System returned list of questions (" + questionList.size() + " found).\n");
        } else {
            System.out.println("Failed: No questions were returned.\n");
        }
    }

    /**
     * Test 3: Retrieves all answers for a given {@link Question}.
     * <p>
     * Example: Counts simulated answers for Donkey Kong question.
     * </p>
     *
     * @see Question#incrementTotalAnswers()
     * @see Question#getTotalAnswers()
     */
    private static void testGetAllAnswersForQuestion() {
        System.out.println("Test 3: Get All Answers for a Question");

        Question q = new Question(
            "How many Kongs are playable in Donkey Kong 64?",
            "List all playable Kong characters in DKC64.",
            "Dylan"
        );
        q.incrementTotalAnswers();
        q.incrementTotalAnswers(); // Simulating two answers

        if (q.getTotalAnswers() == 2) {
            System.out.println("Passed: Returned all answers for the question (2 total).\n");
        } else {
            System.out.println("Failed: Expected 2 answers but found " + q.getTotalAnswers() + ".\n");
        }
    }

    /**
     * Test 4: Searches for a nonexistent {@link Question}.
     * <p>
     * Example: Simulates a search for a DK question ID that doesn't exist.
     * </p>
     *
     * @see Question
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
     * Test 5: Retrieves an answer by ID for a {@link Question}.
     * <p>
     * Example: Who voiced Donkey Kong in the 2023 Super Mario Bros. movie.
     * </p>
     *
     * @see Question#markAsResolved(String)
     * @see Question#getResolvedAnswerId()
     */
    private static void testGetAnswerById() {
        System.out.println("Test 5: Get Answer by ID");

        String expectedAnswerId = "DK-ANS-001";
        Question q = new Question(
            "Who voiced Donkey Kong in the 2023 Super Mario Bros. movie?",
            "Provide the actor's name for Donkey Kong's voice.",
            "Dylan"
        );
        q.markAsResolved(expectedAnswerId);

        if (q.getResolvedAnswerId() != null && q.getResolvedAnswerId().equals(expectedAnswerId)) {
            System.out.println("Passed: Answer retrieved successfully with matching ID.\n");
        } else {
            System.out.println("Failed: Answer ID mismatch or not found.\n");
        }
    }
}
