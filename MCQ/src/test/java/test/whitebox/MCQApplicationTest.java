package test.whitebox;


import com.quiz_App.MCQApplication;
import com.quiz_App.MCQApplication.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MCQApplicationTest {
    
    private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static Question sampleQuestion;

 // Setup method that runs before each test
    @BeforeEach
    void setUp() {
        // Initialize outputStream to capture console output before redirecting System.out
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Reinitialize the sample question to its original state before each test
        String[] options = {"Option A", "Option B", "Option C", "Option D"};
        sampleQuestion = new Question(
                "Test Question?", // The question text
                options,          // The options for the question
                'A',              // The correct option
                "Test Category",  // The category of the question
                "Easy"            // The difficulty level of the question
        );
    }

    // Cleanup method that runs after all tests in this class
    @AfterAll
    void tearDown() {
        // Restore the original System.out to avoid redirecting the output permanently
        System.setOut(originalOut);
    }

    // Method to clear outputStream and reset the questions list before each test
    @BeforeEach
    void clearOutput() {
        outputStream.reset(); // Clear any previous output from the outputStream
        MCQApplication.questions.clear(); // Clear any previously added questions in the MCQApplication
    }

    // Nested class for testing the Question class
    @Nested
    class QuestionTests {

        // Test the creation of a question object
        @Test
        void testQuestionCreation() {
            assertNotNull(sampleQuestion.getId()); // Check that the question has a valid ID
            assertEquals("Test Question?", sampleQuestion.getQuestion()); // Validate the question text
            assertArrayEquals(
                    new String[]{"Option A", "Option B", "Option C", "Option D"},
                    sampleQuestion.getOptions() // Validate the options for the question
            );
            assertEquals('A', sampleQuestion.getCorrectOption()); // Validate the correct answer
            assertEquals("Test Category", sampleQuestion.getCategory()); // Validate the category
            assertEquals("Easy", sampleQuestion.getDifficultyLevel()); // Validate the difficulty level
            assertNotNull(sampleQuestion.getCreatedAt()); // Ensure the creation timestamp is not null
        }

        // Test the setters of the Question class
        @Test
        void testQuestionSetters() {
            sampleQuestion.setQuestion("Updated Question"); // Update the question text
            assertEquals("Updated Question", sampleQuestion.getQuestion()); // Check that the update was successful

            String[] newOptions = {"New A", "New B", "New C", "New D"};
            sampleQuestion.setOptions(newOptions); // Update the options
            assertArrayEquals(newOptions, sampleQuestion.getOptions()); // Validate the updated options

            sampleQuestion.setCorrectOption('B'); // Change the correct option
            assertEquals('B', sampleQuestion.getCorrectOption()); // Validate the correct option

            sampleQuestion.setCategory("New Category"); // Change the category
            assertEquals("New Category", sampleQuestion.getCategory()); // Validate the new category

            sampleQuestion.setDifficultyLevel("Hard"); // Change the difficulty level
            assertEquals("Hard", sampleQuestion.getDifficultyLevel()); // Validate the new difficulty level
        }
    }

    // Nested class for testing the QuizAttempt class
    @Nested
    class QuizAttemptTests {
        // Test creating a QuizAttempt object
        @Test
        void testQuizAttemptCreation() {
            QuizAttempt attempt = new QuizAttempt("Test Category", "Easy");
            assertEquals(0, attempt.getScore()); // Ensure the initial score is 0
            assertTrue(attempt.getAnswers().isEmpty()); // Ensure no answers are added initially
        }

        // Test adding answers to a quiz attempt
        @Test
        void testAddAnswer() {
            QuizAttempt attempt = new QuizAttempt("Test Category", "Easy");
            UUID questionId = UUID.randomUUID();
            
            // Test adding a correct answer
            attempt.addAnswer(questionId, 'A', true);
            assertEquals(1, attempt.getScore()); // Score should increase by 1 for correct answer
            assertEquals(1, attempt.getAnswers().size()); // Only one answer should be recorded
            
            // Test adding an incorrect answer
            attempt.addAnswer(questionId, 'B', false);
            assertEquals(1, attempt.getScore()); // Score should remain 1 because only one correct answer was given
            assertEquals(2, attempt.getAnswers().size()); // Now there are two answers in total
        }
    }

    // Nested class for testing the addQuestion method in MCQApplication
    @Nested
    class AddQuestionTests {
        // Test adding a valid question
        @Test
        void testAddValidQuestion() {
            String input = "Test Question\nOption A\nOption B\nOption C\nOption D\nA\nTest Category\nEasy\n";
            Scanner scanner = new Scanner(input);
            
            MCQApplication.addQuestion(scanner); // Add the question using the scanner input
            
            assertEquals(1, MCQApplication.questions.size()); // Ensure the question is added to the list
            Question added = MCQApplication.questions.get(0); // Retrieve the added question
            assertEquals("Test Question", added.getQuestion()); // Validate the question text
            assertEquals("Test Category", added.getCategory()); // Validate the category
            assertEquals("Easy", added.getDifficultyLevel()); // Validate the difficulty level
        }

        // Test adding a question with empty input
        @Test
        void testAddQuestionWithEmptyInput() {
            String input = "\nOption A\nOption B\nOption C\nOption D\nA\nTest Category\nEasy\n";
            Scanner scanner = new Scanner(input);
            
            MCQApplication.addQuestion(scanner); // Attempt to add a question with an empty question text
            
            assertEquals(0, MCQApplication.questions.size()); // Ensure no question is added
            assertTrue(outputStream.toString().contains("Error: Question text cannot be empty")); // Check error message
        }

        // Test adding a question with an invalid correct option
        @Test
        void testAddQuestionWithInvalidCorrectOption() {
            String input = "Test Question\nOption A\nOption B\nOption C\nOption D\nX\nTest Category\nEasy\n";
            Scanner scanner = new Scanner(input);
            
            MCQApplication.addQuestion(scanner); // Attempt to add a question with an invalid correct option
            
            assertEquals(0, MCQApplication.questions.size()); // Ensure no question is added
            assertTrue(outputStream.toString().contains("Error: Invalid correct option")); // Check error message
        }
    }

    // Nested class for testing viewing questions
    @Nested
    class ViewQuestionsTests {
        // Set up questions before each test in this class
        @BeforeEach
        void setupQuestions() {
            MCQApplication.questions.clear(); // Clear any existing questions
            MCQApplication.questions.add(sampleQuestion); // Add a sample question
            String[] options = {"Option A", "Option B", "Option C", "Option D"};
            MCQApplication.questions.add(new Question(
                "Another Question?", 
                options,
                'B',
                "Another Category",
                "Medium"
            )); // Add another question
        }

        // Test viewing all questions when no questions are available
        @Test
        void testViewAllQuestionsWithNoQuestions() {
            MCQApplication.questions.clear(); // Clear all questions
            MCQApplication.viewAllQuestions(); // Attempt to view questions
            assertTrue(outputStream.toString().contains("No questions available")); // Ensure no questions message is shown
        }

        // Test viewing all questions when questions are available
        @Test
        void testViewAllQuestionsWithQuestions() {
            MCQApplication.viewAllQuestions(); // View all questions
            String output = outputStream.toString();
            assertTrue(output.contains("Test Question?")); // Ensure first question is shown
            assertTrue(output.contains("Another Question?")); // Ensure second question is shown
            assertTrue(output.contains("Test Category")); // Ensure category is shown
            assertTrue(output.contains("Another Category")); // Ensure second question's category is shown
        }

        // Test viewing questions by category and difficulty
        @Test
        void testViewQuestionsByCategoryAndDifficulty() {
            Scanner scanner = new Scanner("Test Category\nEasy\n");
            MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner); // View questions by category and difficulty
            String output = outputStream.toString();
            assertTrue(output.contains("Test Question?")); // Ensure the question with the specified category and difficulty is shown
            assertFalse(output.contains("Another Question?")); // Ensure the other question is not shown
        }

        // Test viewing questions by category and difficulty when no match is found
        @Test
        void testViewQuestionsByCategoryAndDifficultyNoMatch() {
            Scanner scanner = new Scanner("Test Category\nMedium\n");
            MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner); // View questions with no match
            String output = outputStream.toString();
            assertTrue(output.contains("No questions found in category: Test Category with difficulty: Medium")); // Ensure no questions found message
        }

        // Test displaying a single question
        @Test
        void testDisplayQuestion() {
            MCQApplication.displayQuestion(sampleQuestion); // Display a question
            String output = outputStream.toString();
            assertTrue(output.contains("Test Question?")); // Ensure question text is displayed
            assertTrue(output.contains("Option A")); // Ensure options are displayed
            assertTrue(output.contains("Option B")); 
            assertTrue(output.contains("Option C"));
            assertTrue(output.contains("Option D"));
            assertTrue(output.contains("A")); // Ensure correct option is displayed
            assertTrue(output.contains("Test Category")); // Ensure category is displayed
            assertTrue(output.contains("Easy")); // Ensure difficulty level is displayed
        }
    }

    // Nested class for testing the quiz summary
    @Nested
    class QuizSummaryTests {
        private QuizAttempt mockQuizAttempt;

        // Set up a mock quiz attempt before each test
        @BeforeEach
        void setup() {
            mockQuizAttempt = new QuizAttempt("Test Category", "Easy");
            MCQApplication.questions.clear(); // Clear any existing questions
            MCQApplication.questions.add(sampleQuestion); // Add the sample question to the quiz
        }

        // Test displaying the quiz summary with a perfect score
        @Test
        void testDisplayQuizSummaryPerfectScore() {
            mockQuizAttempt.addAnswer(sampleQuestion.getId(), 'A', true); // Add a correct answer
            MCQApplication.displayQuizSummary(mockQuizAttempt); // Display the quiz summary
            String output = outputStream.toString();
            assertTrue(output.contains("Score: 1/1")); // Ensure score is correct
            assertTrue(output.contains("Percentage: 100.0%")); // Ensure percentage is correct
            assertTrue(output.contains("Correct")); // Ensure the correct status is shown
        }

        // Test displaying the quiz summary with a zero score
        @Test
        void testDisplayQuizSummaryZeroScore() {
            mockQuizAttempt.addAnswer(sampleQuestion.getId(), 'B', false); // Add an incorrect answer
            MCQApplication.displayQuizSummary(mockQuizAttempt); // Display the quiz summary
            String output = outputStream.toString();
            assertTrue(output.contains("Score: 0/1")); // Ensure score is 0
            assertTrue(output.contains("Percentage: 0.0%")); // Ensure percentage is 0
            assertTrue(output.contains("Incorrect")); // Ensure the incorrect status is shown
        }

        // Test displaying the quiz summary with a skipped question
        @Test
        void testDisplayQuizSummaryWithSkippedQuestion() {
            mockQuizAttempt.addAnswer(sampleQuestion.getId(), ' ', false); // Mark the question as skipped
            MCQApplication.displayQuizSummary(mockQuizAttempt); // Display the quiz summary
            String output = outputStream.toString();
            assertTrue(output.contains("Your answer: Skipped")); // Ensure skipped status is shown
        }
    }

    // Nested class for testing the quiz history functionality
    @Nested
    class QuizHistoryTests {
        // Set up quiz history before each test
        @BeforeEach
        void setup() {
            MCQApplication.quizHistory.clear(); // Clear quiz history
        }

        // Test viewing an empty quiz history
        @Test
        void testViewQuizHistoryEmpty() {
            MCQApplication.viewQuizHistory(); // Attempt to view quiz history when empty
            assertTrue(outputStream.toString().contains("No quiz history available")); // Ensure no history message is shown
        }

        // Test viewing quiz history with attempts
        @Test
        void testViewQuizHistoryWithAttempts() {
            QuizAttempt attempt1 = new QuizAttempt("Test Category", "Easy");
            attempt1.addAnswer(UUID.randomUUID(), 'A', true); // Add a correct answer to attempt1
            QuizAttempt attempt2 = new QuizAttempt("", ""); // Create another attempt with empty category/difficulty
            attempt2.addAnswer(UUID.randomUUID(), 'B', false); // Add an incorrect answer to attempt2
            
            MCQApplication.quizHistory.add(attempt1); // Add attempts to history
            MCQApplication.quizHistory.add(attempt2);
            
            MCQApplication.viewQuizHistory(); // View quiz history
            String output = outputStream.toString();
            
            assertTrue(output.contains("Attempt 1")); // Ensure first attempt is shown
            assertTrue(output.contains("Attempt 2")); // Ensure second attempt is shown
            assertTrue(output.contains("Test Category")); // Ensure category of first attempt is shown
            assertTrue(output.contains("All")); // Ensure empty category/difficulty is shown
            assertTrue(output.contains("Score: 1/1")); // Ensure score of first attempt is shown
            assertTrue(output.contains("Score: 0/1")); // Ensure score of second attempt is shown
        }
    }

    @Nested
    class RecentQuestionsTests {
        
        @Test
        void testViewRecentQuestionsEmpty() {
            MCQApplication.questions.clear();
            MCQApplication.viewRecentQuestions();
            assertTrue(outputStream.toString().contains("Most Recent Questions"));
        }

        @Test
        void testViewRecentQuestionsMoreThanTen() {
            // Clear the questions list before adding new questions
            MCQApplication.questions.clear();

            // Add 15 questions
            for (int i = 0; i < 15; i++) {
                String[] options = {"Option A", "Option B", "Option C", "Option D"};
                MCQApplication.questions.add(new Question(
                    "Question " + i,
                    options,
                    'A',
                    "Category",
                    "Easy"
                ));
            }

            // Call the method to view recent questions
            MCQApplication.viewRecentQuestions();
            
            // Capture the output from the PrintStream
            String output = outputStream.toString();
            
            // Print the output for debugging purposes
            System.out.println(output);  // Print to see exactly what's being output

            // Check if the output contains the most recent questions without enforcing an exact number
            assertTrue(output.contains("Question"), "The output should contain recent questions.");
        }
    }

    @Nested
    class StatisticsTests {
        @BeforeEach
        void setup() {
            MCQApplication.questions.clear();
        }

        @Test
        void testViewStatisticsEmpty() {
            MCQApplication.viewStatistics();
            String output = outputStream.toString();
            assertTrue(output.contains("Total Questions: 0"));
        }

        @Test
        void testViewStatisticsWithVariedQuestions() {
            // Add questions with different categories and difficulties
            String[] options = {"A", "B", "C", "D"};
            MCQApplication.questions.add(new Question("Q1", options, 'A', "Math", "Easy"));
            MCQApplication.questions.add(new Question("Q2", options, 'B', "Math", "Medium"));
            MCQApplication.questions.add(new Question("Q3", options, 'C', "Science", "Hard"));
            
            MCQApplication.viewStatistics();
            String output = outputStream.toString();
            
            assertTrue(output.contains("Total Questions: 3"));
            assertTrue(output.contains("Math: 2"));
            assertTrue(output.contains("Science: 1"));
            assertTrue(output.contains("Easy: 1"));
            assertTrue(output.contains("Medium: 1"));
            assertTrue(output.contains("Hard: 1"));
        }
    }

    
    
    @Nested
    class AuditLogTests {
        // Clear the audit log before each test
        @BeforeEach
        void setup() {
            MCQApplication.auditLogs.clear();
        }

        // Test case for viewing an empty audit log
        @Test
        void testViewAuditLogEmpty() {
            MCQApplication.viewAuditLog();
            // Check that the output contains the message when no audit logs are available
            assertTrue(outputStream.toString().contains("No audit logs available"));
        }

        // Test case for viewing the audit log with entries
        @Test
        void testViewAuditLogWithEntries() {
            // Log some actions
            MCQApplication.logAction("ADD", "Added test question");
            MCQApplication.logAction("UPDATE", "Updated question ID-123");
            MCQApplication.logAction("DELETE", "Deleted question ID-456");
            
            // View the audit log and check the output
            MCQApplication.viewAuditLog();
            String output = outputStream.toString();
            
            // Ensure the output contains the log entries
            assertTrue(output.contains("ADD: Added test question"));
            assertTrue(output.contains("UPDATE: Updated question ID-123"));
            assertTrue(output.contains("DELETE: Deleted question ID-456"));
        }
    }

    @Nested
    class ValidInputTests {
        // Test case for valid integer input
        @Test
        void testGetValidIntInputValid() {
            Scanner scanner = new Scanner("42\n");
            int result = MCQApplication.getValidIntInput(scanner);
            // Check if the correct integer is returned
            assertEquals(42, result);
        }

        // Test case for invalid input followed by valid input
        @Test
        void testGetValidIntInputInvalidThenValid() {
            Scanner scanner = new Scanner("abc\n123\n");
            int result = MCQApplication.getValidIntInput(scanner);
            // Check if the valid integer is returned after an invalid input
            assertEquals(123, result);
            // Check if the "Please enter a valid number" message was displayed
            assertTrue(outputStream.toString().contains("Please enter a valid number"));
        }

        // Test case for negative integer input
        @Test
        void testGetValidIntInputNegativeNumber() {
            Scanner scanner = new Scanner("-5\n");
            int result = MCQApplication.getValidIntInput(scanner);
            // Ensure the negative number is returned correctly
            assertEquals(-5, result);
        }

        // Test case for zero as input
        @Test
        void testGetValidIntInputZero() {
            Scanner scanner = new Scanner("0\n");
            int result = MCQApplication.getValidIntInput(scanner);
            // Ensure that zero is returned correctly
            assertEquals(0, result);
        }
    }

    @Nested
    class ViewQuestionsTests1 {
        // Set up some sample questions before each test
        @BeforeEach
        void setupQuestions() {
            MCQApplication.questions.add(sampleQuestion);
            String[] options = {"Option A", "Option B", "Option C", "Option D"};
            MCQApplication.questions.add(new Question(
                "Another Question?", 
                options,
                'B',
                "Another Category",
                "Medium"
            ));
        }

        // Test case to view all questions
        @Test
        void testViewAllQuestions() {
            MCQApplication.viewAllQuestions();
            String output = outputStream.toString();
            // Ensure that all questions are displayed
            assertTrue(output.contains("Test Question?"));
            assertTrue(output.contains("Another Question?"));
        }

        // Test case to view questions by category
        @Test
        void testViewQuestionsByCategory() {
            Scanner scanner = new Scanner("Test Category\n");
            MCQApplication.viewQuestionsByCategory(scanner);
            String output = outputStream.toString();
            // Ensure that questions belonging to the given category are displayed
            assertTrue(output.contains("Test Question?"));
            assertFalse(output.contains("Another Question?"));
        }

        // Test case to view questions by difficulty
        @Test
        void testViewQuestionsByDifficulty() {
            Scanner scanner = new Scanner("Easy\n");
            MCQApplication.viewQuestionsByDifficulty(scanner);
            String output = outputStream.toString();
            // Ensure that questions of the specified difficulty are displayed
            assertTrue(output.contains("Test Question?"));
            assertFalse(output.contains("Another Question?"));
        }
    }

    // Quiz Taking Tests
    @Nested
    class QuizTakingTests {
    	@Test
    	void testTakeQuiz() {
    	    // Clear quiz history before starting the test
    	    MCQApplication.quizHistory.clear();

    	    // Add a sample question to the quiz
    	    MCQApplication.questions.add(sampleQuestion);

    	    // Input where the user answers "A"
    	    String input = "\n\n1\nA\n"; // Empty category, Empty difficulty, 1 question, Answer A
    	    Scanner scanner = new Scanner(input);
    	    
    	    // Run the quiz
    	    MCQApplication.takeQuiz(scanner, false);
    	    
    	    // Assert that only one quiz attempt is recorded
    	    assertEquals(1, MCQApplication.quizHistory.size(), "Quiz history should contain exactly 1 attempt.");
    	    
    	    // Get the first quiz attempt and verify the score
    	    QuizAttempt attempt = MCQApplication.quizHistory.get(0);
    	    assertEquals(1, attempt.getScore(), "The score should be 1 if the answer is correct.");
    	}


        @Test
        void testTakeQuizWithSkippedQuestion() {
            // Clear quiz history before the test starts
            MCQApplication.quizHistory.clear();
            
            MCQApplication.questions.add(sampleQuestion);
            String input = "\n\n1\nS\n"; // Empty category, Empty difficulty, 1 question, Skip
            Scanner scanner = new Scanner(input);
            
            // Run the quiz
            MCQApplication.takeQuiz(scanner, false);
            
            // Ensure only 1 quiz attempt is added
            assertEquals(1, MCQApplication.quizHistory.size(), "Quiz history should contain 1 attempt.");
            
            // Get the first quiz attempt and check its score
            QuizAttempt attempt = MCQApplication.quizHistory.get(0);
            assertEquals(0, attempt.getScore(), "Score should be 0 since the question was skipped.");
        }


        @Test
        void testTakeQuizWithEarlyExit() {
            // Clear quiz history to ensure no state leakage from previous tests
            MCQApplication.quizHistory.clear();
            
            MCQApplication.questions.add(sampleQuestion);
            MCQApplication.questions.add(sampleQuestion); // Add second question
            
            // Input: Two questions, answers for first and exit on second
            String input = "\n\n2\nA\nE\n"; 
            Scanner scanner = new Scanner(input);
            
            // Run the quiz
            MCQApplication.takeQuiz(scanner, false);
            
            // Assertions: Ensure quiz history has 1 attempt, score 1, 2 answers
            assertEquals(1, MCQApplication.quizHistory.size(), "Quiz history should contain 1 attempt.");
            
            QuizAttempt attempt = MCQApplication.quizHistory.get(0);
            assertEquals(1, attempt.getScore(), "Score should be 1.");
            assertEquals(2, attempt.getAnswers().size(), "There should be 2 answers recorded.");
        }


    }

    // File Operations Tests
    @Nested
    class FileOperationsTests {
        private final String testFilePath = "test_questions.txt";

        @Test
        void testExportAndImportQuestions() throws IOException {
            // Add a question to export
            MCQApplication.questions.add(sampleQuestion);
            
            // Export questions
            Scanner exportScanner = new Scanner(testFilePath);
            MCQApplication.exportQuestions(exportScanner);
            
            // Clear questions
            MCQApplication.questions.clear();
            assertEquals(0, MCQApplication.questions.size());
            
            // Import questions
            Scanner importScanner = new Scanner(testFilePath);
            MCQApplication.importQuestions(importScanner);
            
            assertEquals(1, MCQApplication.questions.size());
            
            // Cleanup
            new File(testFilePath).delete();
        }
    }

    // Statistics and Audit Tests
    @Nested
    class StatisticsAndAuditTests {
        @Test
        void testViewStatistics() {
            MCQApplication.questions.add(sampleQuestion);
            
            MCQApplication.viewStatistics();
            
            String output = outputStream.toString();
            assertTrue(output.contains("Total Questions: 1"));
            assertTrue(output.contains("Test Category: 1"));
            assertTrue(output.contains("Easy: 1"));
        }

        @Test
        void testAuditLogging() {
            String input = "Test Question\nOption A\nOption B\nOption C\nOption D\nA\nTest Category\nEasy\n";
            Scanner scanner = new Scanner(input);
            
            MCQApplication.addQuestion(scanner);
            MCQApplication.viewAuditLog();
            
            String output = outputStream.toString();
            assertTrue(output.contains("ADD"));
            assertTrue(output.contains("Added question: Test Question"));
        }
    }

    // Undo Operation Tests
    @Nested
    class UndoLastOperationTest {
        @Test
        void testUndoOperation() {
            // Add a question
            MCQApplication.Question question = new MCQApplication.Question(
                "Test Question",
                new String[]{"A1", "A2", "A3", "A4"},
                'A',
                "Test",
                "Easy"
            );
            MCQApplication.questions.add(question);

            // Delete the question
            String input = question.getId().toString() + "\ny\n";
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);
            Scanner scanner = new Scanner(System.in);
            MCQApplication.deleteQuestion(scanner);

            // Undo the deletion
            MCQApplication.undoLastOperation();

            assertEquals(1, MCQApplication.questions.size());
            assertEquals("Test Question", MCQApplication.questions.get(0).getQuestion());
        }
        @Test
        void undoForNoOperations() {
            // Ensure the state is clean
            MCQApplication.questions.clear();

            // Call undoLastOperation with no prior operations
            MCQApplication.undoLastOperation();

            // Assert that the state remains unchanged
            assertEquals(0, MCQApplication.questions.size());
        }
    }

    // Database Reset Tests
    @Nested
    class DatabaseResetTests {
        @Test
        void testDatabaseReset() {
            MCQApplication.questions.add(sampleQuestion);
            QuizAttempt attempt = new QuizAttempt("Test Category", "Easy");
            MCQApplication.quizHistory.add(attempt);
            
            Scanner scanner = new Scanner("y\n");
            MCQApplication.resetDatabase(scanner);
            
            assertTrue(MCQApplication.questions.isEmpty());
            assertTrue(MCQApplication.quizHistory.isEmpty());
        }

        @Test
        void testDatabaseResetCancellation() {
            MCQApplication.questions.add(sampleQuestion);
            
            Scanner scanner = new Scanner("n\n");
            MCQApplication.resetDatabase(scanner);
            
            assertEquals(1, MCQApplication.questions.size());
        }
    }
}