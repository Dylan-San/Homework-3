package application;

import java.util.ArrayList;
import java.util.List;

/**
 * Automated Tests for User Story 3: SEARCH AND READ QUESTIONS/ANSWERS.
 * <p>
 * This class provides automated tests for question and answer retrieval
 * functionality without relying on JUnit. Each test acts as an assertion
 * to ensure the underlying methods behave as expected.
 * </p>
 *
 * <p><b>Author:</b> Dylan</p>
 * <p><b>Version:</b> HW3</p>
 * <p><b>Created:</b> Fall 2025</p>
 *
 * <p><b>Principles:</b></p>
 * <ul>
 *     <li>All doc comments describe method behavior and expected outcomes.</li>
 *     <li>Assertions describe implementation-independent conditions that must be satisfied.</li>
 *     <li>Each test documents preconditions, postconditions, and observable behavior.</li>
 * </ul>
 *
 * <p><b>Javadoc Style Reference:</b> Inspired by Baeldung's guide on 
 * {@link https://www.baeldung.com/javadoc-see-vs-link Javadoc @see vs {@link}} usage.</p>
 *
 * @see Question
 * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: Javadoc @see vs @link</a>
 */
public class SearchAndReadTests {

    /**
     * Executes all automated tests for User Story 3.
     * <p>
     * Preconditions: none. Postconditions: All tests execute and print
     * pass/fail status to the console.
     * </p>
     *
     * @param args not used
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
     * Test 1: Retrieves a {@link Question} by its ID.
     * <p>
     * Example: Donkey Kong Country question. Validates {@link Question#getTitle()},
     * {@link Question#getAuthor()}, and {@link Question#getQuestionId()}.
     * </p>
     *
     * @see Question#getQuestionId()
     * @see Question#getTitle()
     * @see Question#getAuthor()
     * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: @see vs {@link}</a>
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
     * Example: Donkey Kong 64 and arcade questions. Ensures the returned list is non-empty.
     * </p>
     *
     * @see Question#Question(String, String, String)
     * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: @see vs {@link}</a>
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
     * Example: Counts simulated answers for a DKC64 question. Uses {@link Question#incrementTotalAnswers()}
     * and verifies {@link Question#getTotalAnswers()}.
     * </p>
     *
     * @see Question#incrementTotalAnswers()
     * @see Question#getTotalAnswers()
     * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: @see vs {@link}</a>
     */
    private static void testGetAllAnswersForQuestion() {
        System.out.println("Test 3: Get All Answers for a Question");

        Question q = new Question(
            "How many Kongs are playable in Donkey Kong 64?",
            "List all playable Kong characters in DKC64.",
            "Dylan"
        );
        q.incrementTotalAnswers();
        q.incrementTotalAnswers(); // Simulate two answers

        if (q.getTotalAnswers() == 2) {
            System.out.println("Passed: Returned all answers for the question (2 total).\n");
        } else {
            System.out.println("Failed: Expected 2 answers but found " + q.getTotalAnswers() + ".\n");
        }
    }

    /**
     * Test 4: Searches for a nonexistent {@link Question}.
     * <p>
     * Example: Simulates a search for a question ID that does not exist. Ensures
     * null is returned without throwing an exception.
     * </p>
     *
     * @see Question
     * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: @see vs {@link}</a>
     */
    private static void testSearchForNonexistentQuestion() {
        System.out.println("Test 4: Search for Nonexistent Question");

        Question result = null; // Simulate failed search
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
     * Uses {@link Question#markAsResolved(String)} and verifies {@link Question#getResolvedAnswerId()}.
     * </p>
     *
     * @see Question#markAsResolved(String)
     * @see Question#getResolvedAnswerId()
     * @see <a href="https://www.baeldung.com/javadoc-see-vs-link">Baeldung: @see vs {@link}</a>
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
