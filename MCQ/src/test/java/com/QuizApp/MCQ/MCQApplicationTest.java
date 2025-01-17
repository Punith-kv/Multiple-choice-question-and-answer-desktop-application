package com.QuizApp.MCQ;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    
}