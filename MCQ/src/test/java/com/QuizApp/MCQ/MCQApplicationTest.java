package com.QuizApp.MCQ;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.quiz_App.MCQApplication;
import com.quiz_App.MCQApplication.Question;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MCQApplicationTest {
    //
  private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static Question sampleQuestion;


    @BeforeEach
    void setUp() {
        // Initialize outputStream before setting the System.out redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Reinitialize sample question to its original state before each test
        String[] options = {"Option A", "Option B", "Option C", "Option D"};
        sampleQuestion = new Question(
                "Test Question?", 
                options, 
                'A', 
                "Test Category", 
                "Easy"
        );
    }
    
    @AfterAll
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @BeforeEach
    void clearOutput() {
        outputStream.reset();
        MCQApplication.questions.clear();
    }

     @Nested
    class AddQuestionTests {
        @Test
        void testAddValidQuestion() {
            String input = "Test Question\nOption A\nOption B\nOption C\nOption D\nA\nTest Category\nEasy\n";
            Scanner scanner = new Scanner(input);
            
            MCQApplication.addQuestion(scanner);
            
            assertEquals(1, MCQApplication.questions.size());
            Question added = MCQApplication.questions.get(0);
            assertEquals("Test Question", added.getQuestion());
            assertEquals("Test Category", added.getCategory());
            assertEquals("Easy", added.getDifficultyLevel());
        }
    }

    @Nested
class ViewQuestionsTests {
    @BeforeEach
    void setupQuestions() {
        MCQApplication.questions.clear();
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

    @Test
    void testViewAllQuestionsWithNoQuestions() {
        MCQApplication.questions.clear();
        MCQApplication.viewAllQuestions();
        assertTrue(outputStream.toString().contains("No questions available"));
    }

    @Test
    void testViewAllQuestionsWithQuestions() {
        MCQApplication.viewAllQuestions();
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertTrue(output.contains("Another Question?"));
        assertTrue(output.contains("Test Category"));
        assertTrue(output.contains("Another Category"));
    }

    @Test
    void testViewQuestionsByCategoryAndDifficulty() {
        Scanner scanner = new Scanner("Test Category\nEasy\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertFalse(output.contains("Another Question?"));
    }

    @Test
    void testViewQuestionsByCategoryAndDifficultyNoMatch() {
        Scanner scanner = new Scanner("Test Category\nMedium\n");
        MCQApplication.viewQuestionsByCategoryAndDifficulty(scanner);
        String output = outputStream.toString();
        assertTrue(output.contains("No questions found in category: Test Category with difficulty: Medium"));
    }

    @Test
    void testDisplayQuestion() {
        MCQApplication.displayQuestion(sampleQuestion);
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertTrue(output.contains("Option A"));
        assertTrue(output.contains("Option B"));
        assertTrue(output.contains("Option C"));
        assertTrue(output.contains("Option D"));
        assertTrue(output.contains("A"));
        assertTrue(output.contains("Test Category"));
        assertTrue(output.contains("Easy"));
    }
}

@Nested
class QuestionTests {

    @Test
    void testQuestionCreation() {
        assertNotNull(sampleQuestion.getId());
        assertEquals("Test Question?", sampleQuestion.getQuestion());
        assertArrayEquals(
                new String[]{"Option A", "Option B", "Option C", "Option D"},
                sampleQuestion.getOptions()
        );
        assertEquals('A', sampleQuestion.getCorrectOption());
        assertEquals("Test Category", sampleQuestion.getCategory());
        assertEquals("Easy", sampleQuestion.getDifficultyLevel());
        assertNotNull(sampleQuestion.getCreatedAt());
    }

    @Test
    void testQuestionSetters() {
        sampleQuestion.setQuestion("Updated Question");
        assertEquals("Updated Question", sampleQuestion.getQuestion());

        String[] newOptions = {"New A", "New B", "New C", "New D"};
        sampleQuestion.setOptions(newOptions);
        assertArrayEquals(newOptions, sampleQuestion.getOptions());

        sampleQuestion.setCorrectOption('B');
        assertEquals('B', sampleQuestion.getCorrectOption());

        sampleQuestion.setCategory("New Category");
        assertEquals("New Category", sampleQuestion.getCategory());

        sampleQuestion.setDifficultyLevel("Hard");
        assertEquals("Hard", sampleQuestion.getDifficultyLevel());
    }
}
// QuizAttempt Class Tests
@Nested
class QuizAttemptTests {
    @Test
    void testQuizAttemptCreation() {
        QuizAttempt attempt = new QuizAttempt("Test Category", "Easy");
        assertEquals(0, attempt.getScore());
        assertTrue(attempt.getAnswers().isEmpty());
    }

    @Test
    void testAddAnswer() {
        QuizAttempt attempt = new QuizAttempt("Test Category", "Easy");
        UUID questionId = UUID.randomUUID();
        
        // Test correct answer
        attempt.addAnswer(questionId, 'A', true);
        assertEquals(1, attempt.getScore());
        assertEquals(1, attempt.getAnswers().size());
        
        // Test incorrect answer
        attempt.addAnswer(questionId, 'B', false);
        assertEquals(1, attempt.getScore());
        assertEquals(2, attempt.getAnswers().size());
    }
}

@Nested
class ViewQuestionsTests1 {
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

    @Test
    void testViewAllQuestions() {
        MCQApplication.viewAllQuestions();
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertTrue(output.contains("Another Question?"));
    }

    @Test
    void testViewQuestionsByCategory() {
        Scanner scanner = new Scanner("Test Category\n");
        MCQApplication.viewQuestionsByCategory(scanner);
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertFalse(output.contains("Another Question?"));
    }

    @Test
    void testViewQuestionsByDifficulty() {
        Scanner scanner = new Scanner("Easy\n");
        MCQApplication.viewQuestionsByDifficulty(scanner);
        String output = outputStream.toString();
        assertTrue(output.contains("Test Question?"));
        assertFalse(output.contains("Another Question?"));
    }
}

@Nested
class AuditLogTests {
    @BeforeEach
    void setup() {
        MCQApplication.auditLogs.clear();
    }

    @Test
    void testViewAuditLogEmpty() {
        MCQApplication.viewAuditLog();
        assertTrue(outputStream.toString().contains("No audit logs available"));
    }

    @Test
    void testViewAuditLogWithEntries() {
        MCQApplication.logAction("ADD", "Added test question");
        MCQApplication.logAction("UPDATE", "Updated question ID-123");
        MCQApplication.logAction("DELETE", "Deleted question ID-456");
        
        MCQApplication.viewAuditLog();
        String output = outputStream.toString();
        
        assertTrue(output.contains("ADD: Added test question"));
        assertTrue(output.contains("UPDATE: Updated question ID-123"));
        assertTrue(output.contains("DELETE: Deleted question ID-456"));
    }
}
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

    @Nested
    class QuizHistoryTests {
        @BeforeEach
        void setup() {
            MCQApplication.quizHistory.clear();
        }

        @Test
        void testViewQuizHistoryEmpty() {
            MCQApplication.viewQuizHistory();
            assertTrue(outputStream.toString().contains("No quiz history available"));
        }

        @Test
        void testViewQuizHistoryWithAttempts() {
            QuizAttempt attempt1 = new QuizAttempt("Test Category", "Easy");
            attempt1.addAnswer(UUID.randomUUID(), 'A', true);
            QuizAttempt attempt2 = new QuizAttempt("", ""); // All categories and difficulties
            attempt2.addAnswer(UUID.randomUUID(), 'B', false);

            MCQApplication.quizHistory.add(attempt1);
            MCQApplication.quizHistory.add(attempt2);

            MCQApplication.viewQuizHistory();
            String output = outputStream.toString();

            assertTrue(output.contains("Attempt 1"));
            assertTrue(output.contains("Attempt 2"));
            assertTrue(output.contains("Test Category"));
            assertTrue(output.contains("All")); // For empty category/difficulty
            assertTrue(output.contains("Score: 1/1"));
            assertTrue(output.contains("Score: 0/1"));
        }
    }

    
}