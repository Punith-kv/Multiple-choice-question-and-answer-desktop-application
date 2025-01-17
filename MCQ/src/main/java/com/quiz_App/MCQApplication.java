package com.quiz_App;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.app.mcq.MCQApplication.Question;

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

		public Question(String question, String[] options, char correctOption, String category,
				String difficultyLevel) {
			this.id = UUID.randomUUID();
			this.question = question;
			this.options = options;
			this.correctOption = correctOption;
			this.category = category;
			this.difficultyLevel = difficultyLevel;
			this.createdAt = LocalDateTime.now();
		}

		// Getters and setters
		public UUID getId() {
			return id;
		}

		public String getQuestion() {
			return question;
		}

		public String[] getOptions() {
			return options;
		}

		public char getCorrectOption() {
			return correctOption;
		}

		public String getCategory() {
			return category;
		}

		public String getDifficultyLevel() {
			return difficultyLevel;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public void setOptions(String[] options) {
			this.options = options;
		}

		public void setCorrectOption(char correctOption) {
			this.correctOption = correctOption;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public void setDifficultyLevel(String difficultyLevel) {
			this.difficultyLevel = difficultyLevel;
		}
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
			case 15 -> {
				return;
			}
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
			case 4 -> {
				return;
			}
			default -> System.out.println("Invalid choice. Please try again.");
			}
		}
	}

}

	private static void addQuestion(Scanner scanner) {
		// Get the question text
		System.out.print("Enter the question: ");
		String questionText = scanner.nextLine();

		// Get the options
		String[] options = new String[4];
		for (int i = 0; i < 4; i++) {
			System.out.print("Enter option " + (char) ('A' + i) + ": ");
			options[i] = scanner.nextLine();
		}

		// Get the correct option
		System.out.print("Enter the correct option (A/B/C/D): ");
		String correctOptionInput = scanner.nextLine().toUpperCase();

		if (correctOptionInput.length() != 1
				|| (correctOptionInput.charAt(0) < 'A' || correctOptionInput.charAt(0) > 'D')) {
			System.out.println("Error: Invalid correct option. Please choose from A, B, C, or D.");
			return;
		}

		char correctOption = correctOptionInput.charAt(0);

		// Get the category
		String category;

		System.out.print("Enter category (cannot be empty): ");
		category = scanner.nextLine().trim();

		String difficultyLevel;

		System.out.print("Enter difficulty level (Easy/Medium/Hard): ");
		difficultyLevel = scanner.nextLine().trim();

		// Save current state for undo
		undoStack.push(new ArrayList<>(questions));

		// Create and add the new question
		Question newQuestion = new Question(questionText, options, correctOption, category, difficultyLevel);
		questions.add(newQuestion);

		logAction("ADD", "Added question: " + questionText);

	}

	private static void updateQuestion(Scanner scanner) {
        viewAllQuestions();
        System.out.print("Enter question ID to update: ");
        String id = scanner.nextLine();

        try {
            UUID questionId = UUID.fromString(id);
            Optional<Question> questionOpt = questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst();

            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                undoStack.push(new ArrayList<>(questions));

                System.out.println("Current question: " + question.getQuestion());
                System.out.print("Enter new question (press Enter to keep current): ");
                String newQuestion = scanner.nextLine();
                if (!newQuestion.trim().isEmpty()) {
                    question.setQuestion(newQuestion);
                }

                // Update options
                for (int i = 0; i < 4; i++) {
                    System.out.println("Current option " + (char)('A' + i) + ": " + 
                                     question.getOptions()[i]);
                    System.out.print("Enter new option (press Enter to keep current): ");
                    String newOption = scanner.nextLine();
                    if (!newOption.trim().isEmpty()) {
                        question.getOptions()[i] = newOption;
                    }
                }

                System.out.println("Current correct option: " + question.getCorrectOption());
                System.out.print("Enter new correct option (press Enter to keep current): ");
                String newCorrectOption = scanner.nextLine();
                if (!newCorrectOption.trim().isEmpty()) {
                    question.setCorrectOption(newCorrectOption.toUpperCase().charAt(0));
                }

                logAction("UPDATE", "Updated question: " + question.getQuestion());
                System.out.println("Question updated successfully!");
            } else {
                System.out.println("Error: Question not found.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid question ID format.");
        }
    }

	public static void viewAllQuestions() {
    if (questions.isEmpty()) {
        System.out.println("No questions available.");
        return;
    }

    System.out.println("\nAll Questions:");
    for (Question q : questions) {
        displayQuestion(q);
    }
}
public static void displayQuestion(Question q) {
    System.out.println("\nID: " + q.getId());
    System.out.println("Question: " + q.getQuestion());
    System.out.println("Category: " + q.getCategory());
    System.out.println("Difficulty: " + q.getDifficultyLevel());
    System.out.println("Options:");
    for (int i = 0; i < q.getOptions().length; i++) {
        System.out.println((char)('A' + i) + ". " + q.getOptions()[i]);
    }
    System.out.println("Correct Answer: " + q.getCorrectOption());
    System.out.println("Created: " + q.getCreatedAt());
}
public static void viewQuestionsByCategory(Scanner scanner) {
    // Extract all unique categories
    Set<String> categories = questions.stream()
        .map(Question::getCategory)
        .collect(Collectors.toSet());

    // Display all available categories
    System.out.println("Available categories:");
    categories.forEach(System.out::println);

    // Ask the user to input a category
    System.out.print("Enter category: ");
    String category = scanner.nextLine();

    // Filter questions by the selected category
    List<Question> filteredQuestions = questions.stream()
        .filter(q -> q.getCategory().equalsIgnoreCase(category))
        .collect(Collectors.toList());

    // Display the results
    if (filteredQuestions.isEmpty()) {
        System.out.println("No questions found in category: " + category);
    } else {
        System.out.println("\nQuestions in category " + category + ":");
        filteredQuestions.forEach(MCQApplication::displayQuestion);
    }
}


public static void viewQuestionsByDifficulty(Scanner scanner) {
    System.out.print("Enter difficulty level (Easy/Medium/Hard): ");
    String difficulty = scanner.nextLine();

    List<Question> filteredQuestions = questions.stream()
        .filter(q -> q.getDifficultyLevel().equalsIgnoreCase(difficulty))
        .collect(Collectors.toList());

    if (filteredQuestions.isEmpty()) {
        System.out.println("No questions found for difficulty: " + difficulty);
    } else {
        System.out.println("\nQuestions with difficulty " + difficulty + ":");
        filteredQuestions.forEach(MCQApplication::displayQuestion);
    }
}
public static void viewQuestionsByCategoryAndDifficulty(Scanner scanner) {
    // Extract all unique categories
    Set<String> categories = questions.stream()
        .map(Question::getCategory)
        .collect(Collectors.toSet());

    // Extract all unique difficulty levels
    Set<String> difficultyLevels = questions.stream()
        .map(Question::getDifficultyLevel)
        .collect(Collectors.toSet());

    // Display all available categories
    System.out.println("Available categories:");
    categories.forEach(System.out::println);

    // Display all available difficulty levels
    System.out.println("\nAvailable difficulty levels (Easy/Medium/Hard):");
    difficultyLevels.forEach(System.out::println);

    // Prompt for category and difficulty level
    System.out.print("Enter category: ");
    String category = scanner.nextLine();

    System.out.print("Enter difficulty level (Easy/Medium/Hard): ");
    String difficulty = scanner.nextLine();

    // Filter questions by both category and difficulty
    List<Question> filteredQuestions = questions.stream()
        .filter(q -> q.getCategory().equalsIgnoreCase(category) && q.getDifficultyLevel().equalsIgnoreCase(difficulty))
        .collect(Collectors.toList());

    // Check if filtered questions list is empty
    if (filteredQuestions.isEmpty()) {
        System.out.println("No questions found in category: " + category + " with difficulty: " + difficulty);
    } else {
        System.out.println("\nQuestions in category " + category + " with difficulty " + difficulty + ":");
        filteredQuestions.forEach(MCQApplication::displayQuestion);
    }
}
public static void viewRecentQuestions() {
    System.out.println("\nMost Recent Questions (up to 10):");
    questions.stream()
        .sorted((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()))
        .limit(10)  // Only print 10 questions
        .forEach(q -> System.out.println(q.getQuestion())); // Only print the question text
}



public static void takeQuiz(Scanner scanner, boolean retake) {
    if (questions.isEmpty()) {
        System.out.println("No questions available. Please add questions first.");
        return;
    }

    final String selectedCategory;
    final String selectedDifficulty;
    List<Question> quizQuestions;

    if (!retake) {
        // Extract unique categories
        Set<String> categories = questions.stream()
            .map(Question::getCategory)
            .collect(Collectors.toSet());

        // Display available categories
        System.out.println("\nAvailable Categories:");
        categories.forEach(System.out::println);
        System.out.println("Press Enter to include all categories.");

        // Get user's choice
        System.out.print("Enter category: ");
        selectedCategory = scanner.nextLine();

        System.out.print("Enter difficulty level (Easy/Medium/Hard, press Enter for all): ");
        selectedDifficulty = scanner.nextLine();

        quizQuestions = questions.stream()
            .filter(q -> selectedCategory.isEmpty() || q.getCategory().equalsIgnoreCase(selectedCategory))
            .filter(q -> selectedDifficulty.isEmpty() || q.getDifficultyLevel().equalsIgnoreCase(selectedDifficulty))
            .collect(Collectors.toList());
    } else {
        if (quizHistory.isEmpty()) {
            System.out.println("No previous quiz to retake.");
            return;
        }
        QuizAttempt lastQuiz = quizHistory.get(quizHistory.size() - 1);
        selectedCategory = lastQuiz.category;
        selectedDifficulty = lastQuiz.difficultyLevel;
        quizQuestions = new ArrayList<>(questions);
    }

    if (quizQuestions.isEmpty()) {
        System.out.println("No questions available for selected criteria.");
        return;
    }

    System.out.print("Enter number of questions (max " + quizQuestions.size() + "): ");
    int numQuestions = getValidIntInput(scanner);
    scanner.nextLine(); // Consume newline

    // Check if the entered number exceeds available questions
    if (numQuestions > quizQuestions.size()) {
        System.out.println("Error: Only " + quizQuestions.size() + " questions are available selected category '"
                + selectedCategory + "' and with selected difficulty '" + selectedDifficulty + "'.");
        return; // Exit the quiz-taking process
    }

    Collections.shuffle(quizQuestions);
    quizQuestions = quizQuestions.subList(0, numQuestions);

    QuizAttempt attempt = new QuizAttempt(selectedCategory, selectedDifficulty);
    int questionNum = 1;

    System.out.println("\nInstructions: Enter A/B/C/D to answer, S to skip, or E to exit the quiz.");

    for (Question question : quizQuestions) {
        System.out.println("\nQuestion " + questionNum + "/" + numQuestions + ":");
        System.out.println(question.getQuestion());

        // Display options in their original order
        for (int i = 0; i < 4; i++) {
            System.out.println((char) ('A' + i) + ". " + question.getOptions()[i]);
        }

        System.out.print("Your answer (A/B/C/D, S to skip, E to exit): ");
        String input = scanner.nextLine().toUpperCase();

        if (input.equals("E")) {
            System.out.println("\nExiting quiz... All remaining unanswered questions will be marked as incorrect.");
            break;
        } else if (input.equals("S")) {
            attempt.addAnswer(question.getId(), ' ', false);
            System.out.println("Question skipped.");
        } else if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'D') {
            char userAnswer = input.charAt(0);
            boolean correct = (userAnswer == question.getCorrectOption());

            attempt.addAnswer(question.getId(), userAnswer, correct);

            if (correct) {
                System.out.println("Correct! Well done!");
            } else {
                System.out.println("Wrong. The correct answer was " + question.getCorrectOption() + ".");
            }
        } else {
            System.out.println("Invalid input. Question marked as incorrect.");
            attempt.addAnswer(question.getId(), ' ', false);
        }
        questionNum++;
    }

    // Mark all remaining questions as incorrect if user exits early
    if (questionNum <= numQuestions) {
        for (int i = questionNum - 1; i < numQuestions; i++) {
            attempt.addAnswer(quizQuestions.get(i).getId(), ' ', false);
        }
    }

    quizHistory.add(attempt);
    System.out.println("\nQuiz completed!");
    displayQuizSummary(attempt);
}



public static void importQuestions(Scanner scanner) {
    System.out.print("Enter file path to import questions from: ");
    String filePath = scanner.nextLine();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        undoStack.push(new ArrayList<>(questions));
        String line;
        int imported = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                String questionText = parts[0];
                String[] options = new String[4];
                System.arraycopy(parts, 1, options, 0, 4);
                char correctOption = parts[5].charAt(0);
                String category = parts.length > 6 ? parts[6] : "General";
                String difficulty = parts.length > 7 ? parts[7] : "Medium";

                questions.add(new Question(questionText, options, correctOption, 
                                        category, difficulty));
                imported++;
            }
        }
        logAction1("IMPORT", "Imported " + imported + " questions from " + filePath);
        System.out.println("Successfully imported " + imported + " questions.");
    } catch (IOException e) {
        System.out.println("Error importing questions: " + e.getMessage());
    }
}


public static void exportQuestions(Scanner scanner) {
    System.out.print("Enter file path to export questions to: ");
    String filePath = scanner.nextLine();

    // Debug: Print the resolved file path
    System.out.println("Exporting to: " + filePath);

    // Ensure the parent directory exists
    File file = new File(filePath);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
        System.out.println("The directory does not exist. Please provide a valid path.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        for (Question q : questions) {
            writer.write(q.getQuestion() + "|" + 
                         String.join("|", q.getOptions()) + "|" +
                         q.getCorrectOption() + "|" +
                         q.getCategory() + "|" +
                         q.getDifficultyLevel());
            writer.newLine(); // For new line after each question
        }
        logAction1("EXPORT", "Exported " + questions.size() + " questions to " + filePath);
        System.out.println("Successfully exported " + questions.size() + " questions.");
    } catch (IOException e) {
        System.out.println("Error exporting questions: " + e.getMessage());
    }
}

public static void viewAuditLog() {
    if (auditLogs.isEmpty()) {
        System.out.println("No audit logs available.");
        return;
    }

    System.out.println("\nAudit Log:");
    auditLogs.forEach(log -> System.out.println(
        log.timestamp + " - " + log.action + ": " + log.details));
}

public static void resetDatabase(Scanner scanner) {
    System.out.print("Are you sure you want to reset the database? (y/n): ");
    String confirm = scanner.nextLine();

    if (confirm.equalsIgnoreCase("y")) {
        undoStack.push(new ArrayList<>(questions));
        questions.clear();
        quizHistory.clear();
        logAction1("RESET", "Database reset");
        System.out.println("Database reset successfully.");
    }
}

public static void loadData() {
    try {
        File dataFile = new File(DATA_FILE);
        File auditFile = new File(AUDIT_FILE);
        
        if (dataFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                questions = (List<Question>) ois.readObject();
                quizHistory = (List<QuizAttempt>) ois.readObject();
            }
        }
        
        if (auditFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(auditFile))) {
                auditLogs = (List<AuditLog>) ois.readObject();
            }
        }
    } catch (Exception e) {
        System.out.println("Error loading data: " + e.getMessage());
    }
}

public static void saveData() {
    try {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(questions);
            oos.writeObject(quizHistory);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUDIT_FILE))) {
            oos.writeObject(auditLogs);
        }
        
        System.out.println("Data saved successfully.");
    } catch (IOException e) {
        System.out.println("Error saving data: " + e.getMessage());
    }
}

public static void undoLastOperation() {
    if (undoStack.isEmpty()) {
        System.out.println("No operations to undo.");
        return;
    }

    questions = undoStack.pop();
    logAction("UNDO", "Undid last operation");
    System.out.println("Last operation undone successfully.");
}

public static void viewQuizHistory() {
    if (quizHistory.isEmpty()) {
        System.out.println("No quiz history available.");
        return;
    }

    System.out.println("\nQuiz History:");
    for (int i = 0; i < quizHistory.size(); i++) {
        QuizAttempt attempt = quizHistory.get(i);
        System.out.println("\nAttempt " + (i + 1) + ":");
        System.out.println("Category: " +
                (attempt.category.isEmpty() ? "All" : attempt.category));
        System.out.println("Difficulty: " +
                (attempt.difficultyLevel.isEmpty() ? "All" : attempt.difficultyLevel));
        System.out.println("Score: " + attempt.getScore() + "/" + attempt.getAnswers().size());
        System.out.println("Date: " + attempt.timestamp);
    }
}
 public static void logAction(String action, String details) {
        auditLogs.add(new AuditLog(action, details));
    }

    public static int getValidIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }
    public static void displayQuizSummary(QuizAttempt attempt) {
        System.out.println("\nQuiz Summary:");
        System.out.println("Score: " + attempt.getScore() + "/" + attempt.getAnswers().size());
        System.out.println("Percentage: " + 
                          (attempt.getScore() * 100.0 / attempt.getAnswers().size()) + "%");
        
        System.out.println("\nDetailed Answer Review:");
        for (int i = 0; i < attempt.getAnswers().size(); i++) {
            QuizAnswer answer = attempt.getAnswers().get(i);
            Question question = questions.stream()
                .filter(q -> q.getId().equals(answer.questionId))
                .findFirst()
                .orElse(null);
            
            if (question != null) {
                System.out.println("\nQuestion " + (i + 1) + ": " + question.getQuestion());
                System.out.println("Your answer: " + 
                                 (answer.userAnswer == ' ' ? "Skipped" : answer.userAnswer));
                System.out.println("Correct answer: " + question.getCorrectOption());
                System.out.println("Result: " + (answer.correct ? "Correct" : "Incorrect"));
            }
        }
    }



}