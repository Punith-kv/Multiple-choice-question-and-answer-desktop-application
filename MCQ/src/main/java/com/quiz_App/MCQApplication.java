package com.quiz_App;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class MCQApplication {
    // Class to represent an MCQ question
    public static class Question implements Serializable {
        private UUID id;
        private String question;
        private String[] options;
        private char correctOption;
        private String category;
        private String difficultyLevel;
        private LocalDateTime createdAt;

        public Question(String question, String[] options, char correctOption, 
                       String category, String difficultyLevel) {
            this.id = UUID.randomUUID();
            this.question = question;
            this.options = options;
            this.correctOption = correctOption;
            this.category = category;
            this.difficultyLevel = difficultyLevel;
            this.createdAt = LocalDateTime.now();
        }

        // Getters and setters
        public UUID getId() { return id; }
        public String getQuestion() { return question; }
        public String[] getOptions() { return options; }
        public char getCorrectOption() { return correctOption; }
        public String getCategory() { return category; }
        public String getDifficultyLevel() { return difficultyLevel; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public void setQuestion(String question) { this.question = question; }
        public void setOptions(String[] options) { this.options = options; }
        public void setCorrectOption(char correctOption) { this.correctOption = correctOption; }
        public void setCategory(String category) { this.category = category; }
        public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    }

    
    
    

    public static void main(String[] args) {
        loadData();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMCQ Application Menu:");
            System.out.println("1. Admin Menu");
            System.out.println("2. User Menu");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidIntInput(scanner);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> adminMenu(scanner);
                case 2 -> userMenu(scanner);
                case 3 -> {
                    saveData();
                    System.out.println("Thank you for using the MCQ Application. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void adminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Question");
            System.out.println("2. Update Question");
            System.out.println("3. Delete Question");
            System.out.println("4. View All Questions");
            System.out.println("5. View Questions by Category");
            System.out.println("6. View Questions by Difficulty");
			System.out.println("7. View Questions by CategoryAndDifficulty");
            System.out.println("8. View Recent Questions");
            System.out.println("9. Import Questions");
            System.out.println("10. Export Questions");
            System.out.println("11. View Statistics");
            System.out.println("12. Undo Last Operation");
            System.out.println("13. View Audit Log");
            System.out.println("14. Reset Database");
            System.out.println("15. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = getValidIntInput(scanner);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addQuestion(scanner);
                case 2 -> updateQuestion(scanner);
                case 3 -> deleteQuestion(scanner);
                case 4 -> viewAllQuestions();
                case 5 -> viewQuestionsByCategory(scanner);
                case 6 -> viewQuestionsByDifficulty(scanner);
				case 7 -> viewQuestionsByCategoryAndDifficulty(scanner);
                case 8 -> viewRecentQuestions();
                case 9 -> importQuestions(scanner);
                case 10 -> exportQuestions(scanner);
                case 11 -> viewStatistics();
                case 12 -> undoLastOperation();
                case 13 -> viewAuditLog();
                case 14 -> resetDatabase(scanner);
                case 15 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void userMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Take New Quiz");
            System.out.println("2. Retake Last Quiz");
            System.out.println("3. View Quiz History");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = getValidIntInput(scanner);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> takeQuiz(scanner, false);
                case 2 -> takeQuiz(scanner, true);
                case 3 -> viewQuizHistory();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
	}}

}



