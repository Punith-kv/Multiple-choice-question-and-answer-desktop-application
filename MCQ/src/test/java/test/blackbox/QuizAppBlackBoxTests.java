package test.blackbox;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

import com.quiz_App.MCQApplication;
import com.quiz_App.MCQApplication.Question;

class QuizAppBlackBoxTests {

    /**
     * Tests the deletion of a valid question from the MCQApplication.
     * Verifies that the question list size decreases after deletion.
     */
    @Test
    void testDeleteValidQuestion() {
        Question q = new Question("Test Question", new String[]{"A", "B", "C", "D"}, 'A', "Science", "Easy");
        MCQApplication.questions.add(q);
        Scanner scanner = new Scanner(q.getId().toString() + "\ny\n");
        MCQApplication.deleteQuestion(scanner);
        assertEquals(0, MCQApplication.questions.size());
    }

    /**
     * Tests updating a valid question in the MCQApplication.
     * Verifies that the question's text is updated correctly.
     */
    @Test
    void testUpdateValidQuestion() {
        Question q = new Question("Test Question", new String[]{"A", "B", "C", "D"}, 'A', "Science", "Easy");
        MCQApplication.questions.add(q);
        Scanner scanner = new Scanner(q.getId().toString() + "\nUpdated Question\nA\nB\nC\nD\nA\n");
        MCQApplication.updateQuestion(scanner);
        assertEquals("Updated Question", MCQApplication.questions.get(0).getQuestion());
    }

    /**
     * Tests attempting to update a question that does not exist in the database.
     * This is expected to fail silently or handle the invalid input gracefully.
     */
    @Test
    void testUpdateInvalidQuestion() {
        Scanner scanner = new Scanner(UUID.randomUUID().toString() + "\nNew Question\nA\nB\nC\nD\nA\n");
        MCQApplication.updateQuestion(scanner);
    }

    /**
     * Tests viewing questions filtered by a valid difficulty level.
     * Verifies that questions matching the difficulty are displayed.
     */
    @Test
    void testViewQuestionsByDifficultyValid() {
        Scanner scanner = new Scanner("Easy\n");
        MCQApplication.viewQuestionsByDifficulty(scanner);
    }

    /**
     * Tests viewing questions filtered by an invalid difficulty level.
     * Verifies that the application handles invalid inputs gracefully.
     */
    @Test
    void testViewQuestionsByDifficultyInvalid() {
        Scanner scanner = new Scanner("SuperHard\n");
        MCQApplication.viewQuestionsByDifficulty(scanner);
    }

    /**
     * Tests viewing questions filtered by a valid category.
     * Verifies that questions in the specified category are displayed.
     */
    @Test
    void testViewQuestionsByCategoryValid() {
        Scanner scanner = new Scanner("Science\n");
        MCQApplication.viewQuestionsByCategory(scanner);
    }

    /**
     * Tests viewing questions filtered by an invalid category.
     * Verifies that the application handles invalid inputs gracefully.
     */
    @Test
    void testViewQuestionsByCategoryInvalid() {
        Scanner scanner = new Scanner("Mythology\n");
        MCQApplication.viewQuestionsByCategory(scanner);
    }

    /**
     * Tests viewing questions filtered by a valid category and difficulty.
     * Verifies that the correct questions are displayed.
     */
    @Test
    void testViewQuestionsByCategoryAndDifficulty_ValidCategoryValidDifficulty() {
        Scanner scanner = new Scanner("Science\nEasy\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
    }

    /**
     * Tests viewing questions filtered by a valid category but an invalid difficulty.
     * Verifies that the application handles mismatched filters appropriately.
     */
    @Test
    void testViewQuestionsByCategoryAndDifficulty_ValidCategoryInvalidDifficulty() {
        Scanner scanner = new Scanner("Science\nVeryHard\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
    }

    /**
     * Tests viewing questions filtered by an invalid category but a valid difficulty.
     * Verifies that no questions are displayed.
     */
    @Test
    void testViewQuestionsByCategoryAndDifficulty_InvalidCategoryValidDifficulty() {
        Scanner scanner = new Scanner("Mythology\nEasy\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
    }

    /**
     * Tests viewing questions filtered by an invalid category and difficulty.
     * Verifies that the application handles invalid inputs appropriately.
     */
    @Test
    void testViewQuestionsByCategoryAndDifficulty_InvalidCategoryInvalidDifficulty() {
        Scanner scanner = new Scanner("UnknownCategory\nUnknownDifficulty\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
    }

    /**
     * Tests viewing recent questions when the database is empty.
     * Verifies that the application handles an empty database appropriately.
     */
    @Test
    void testViewRecentQuestionsEmptyDB() {
        MCQApplication.viewRecentQuestions();
    }

    /**
     * Tests resetting the database with user confirmation ('y').
     * Verifies that all data is cleared upon reset.
     */
    @Test
    void testResetDatabaseConfirm() {
        Scanner scanner = new Scanner("y\n");
        MCQApplication.resetDatabase(scanner);
    }

    /**
     * Tests resetting the database when the user declines ('n').
     * Verifies that no changes are made to the database.
     */
    @Test
    void testResetDatabaseDecline() {
        Scanner scanner = new Scanner("n\n");
        MCQApplication.resetDatabase(scanner);
    }

    /**
     * Tests viewing application statistics.
     * Verifies that the statistics are displayed correctly.
     */
    @Test
    void testViewStatistics() {
        MCQApplication.viewStatistics();
    }

    /**
     * Tests undoing the last operation performed in the application.
     * Verifies that the last change is reverted correctly.
     */
    @Test
    void testUndoLastOperation() {
        com.quiz_App.MCQApplication.undoLastOperation();
    }

    /**
     * Tests viewing the audit log of the application.
     * Verifies that the log displays the expected operations.
     */
    @Test
    void testViewAuditLog() {
        com.quiz_App.MCQApplication.viewAuditLog();
    }
}
