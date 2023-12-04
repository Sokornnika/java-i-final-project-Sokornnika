//SOK Sokornnika final project
//ITM 201-001 JavaProgrammingI
//Date: 04/12/2023

// Import necessary packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Define the main class for the Letter Detective Game, extending JFrame
public class LetterDetectiveGame extends JFrame {

    // Declare class-level variables
    String selectedWord;
    StringBuilder hiddenWord;
    int attemptsLeft;
    int incorrectGuessCount;
    JLabel wordLabel;
    JLabel attemptsLabel;
    JLabel hangmanLabel;
    JTextField letterField;
    JButton guessButton;
    String selectedCategory;
    Timer timer;
    JLabel countdownLabel;
    int countdownSeconds;

    // Define ASCII art for the hangman
    private static final String[] HANGMAN_ASCII = {
        "   +---+\n   |   |\n       |\n       |\n       |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n       |\n       |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n   |   |\n       |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n  /|   |\n       |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n  /|\\  |\n       |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n  /|\\  |\n  /    |\n       |\n=========",
        "   +---+\n   |   |\n   O   |\n  /|\\  |\n  / \\  |\n       |\n========="
    };

    // Define word categories
    static final List<String> COUNTRIES = Arrays.asList(
    "usa", "canada", "france", "japan", "australia", 
    "brazil", "india", "germany", "cambodia", "russia", 
    "korea", "colombia", "spain", "norway", "switzerland",
    "ireland", "singapore", "philippines", "bangladesh", "argentina",
    "southkorea"
);

    static final List<String> COLORS = Arrays.asList(
    "red", "blue", "green", "yellow", "purple", 
    "orange", "pink", "brown", "black", "white", 
    "magenta", "cyan", "peach", "lavender", "emerald", 
    "ruby", "sapphire", "turquoise", "amber", "rose"
);

    static final List<String> ANIMALS = Arrays.asList(
    "elephant", "tiger", "dolphin", "giraffe", "penguin", 
    "koala", "cheetah", "octopus", "gorilla", "kangaroo", 
    "cheetah", "penguin", "gorilla", "polarbear", "jaguar", 
    "armadillo", "lemur", "koalabear", "puma", "crocodile"
);

    // Declare frame variables
    JFrame categoryFrame;
    JFrame gameFrame;

    // Constructor for the game
    public LetterDetectiveGame() {
        showCategorySelectionFrame();
    }

    // Method to display the category selection frame
    private void showCategorySelectionFrame() {
        // Create and configure the category selection frame
        categoryFrame = new JFrame("Category Selection");
        // Set frame size, default close operation, and location
        categoryFrame.setSize(300, 200);
        categoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        categoryFrame.setLocationRelativeTo(null);

        // Create and configure components for the category selection frame
        JLabel welcomeLabel = new JLabel("Welcome to Letter Detective Game!");
        // Set font and color for the welcome label
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        welcomeLabel.setForeground(Color.BLUE);
        welcomeLabel.setBounds(30, 10, 250, 25);

        JLabel label = new JLabel("Select a category:");
        label.setBounds(80, 40, 150, 25);

        // Get categories and create a combo box
        String[] categories = getAllCategories();
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setBounds(50, 70, 200, 25);

        // Create a "Start Game" button with an ActionListener
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(100, 110, 120, 30);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCategory = categoryComboBox.getSelectedItem().toString();
                categoryFrame.dispose();
                initializeGame();
                initializeGameFrame();
            }
        });

        // Configure layout and add components to the category selection frame
        categoryFrame.setLayout(null);
        categoryFrame.add(welcomeLabel);
        categoryFrame.add(label);
        categoryFrame.add(categoryComboBox);
        categoryFrame.add(startButton);
        categoryFrame.setVisible(true);
    }

    // Method to initialize the game frame
    private void initializeGameFrame() {
        // Check if a game frame already exists and dispose it
        if (gameFrame != null) {
            gameFrame.dispose();
        }
    
        // Create and configure the game frame
        gameFrame = new JFrame("Letter Detective Game");
        gameFrame.setSize(400, 300);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
    
        // Create and configure components for the game frame
        JLabel categoryLabel = new JLabel("You chose: " + selectedCategory);
        categoryLabel.setBounds(22, 130, 150, 25);
    
        wordLabel = new JLabel(getHiddenWordString());
        wordLabel.setBounds(22, 17, 198, 65);
    
        attemptsLabel = new JLabel("Attempts left: " + attemptsLeft);
        attemptsLabel.setForeground(Color.RED);
        attemptsLabel.setBounds(22, 87, 92, 65);
    
        hangmanLabel = new JLabel();
        hangmanLabel.setBounds(204, 17, 198, 174);
    
        letterField = new JTextField(1);
        letterField.setBounds(166, 14, 34, 28);
    
        guessButton = new JButton("Guess");
        guessButton.setBounds(225, 13, 106, 31);
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processGuess();
            }
        });
    
        // Initialize countdown timer
        countdownSeconds = 15;
        countdownLabel = new JLabel("Countdown: " + countdownSeconds + " seconds");
        countdownLabel.setBounds(250, 10, 120, 25);
        countdownLabel.setForeground(Color.RED);
    
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCountdown();
            }
        });
        timer.start();
    
        // Configure layout and add components to the game frame
        gameFrame.getContentPane().setLayout(null);
    
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 384, 261);
        panel.setLayout(null);
        panel.add(wordLabel);
        panel.add(attemptsLabel);
        panel.add(hangmanLabel);
        panel.add(categoryLabel);
    
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(0, 195, 384, 65);
        inputPanel.setLayout(null);
        JLabel label = new JLabel("Enter a letter: ");
        label.setBounds(47, 17, 107, 22);
        inputPanel.add(label);
        inputPanel.add(letterField);
        inputPanel.add(guessButton);
    
        panel.add(inputPanel);
        panel.add(countdownLabel);
    
        gameFrame.getContentPane().add(panel);
        gameFrame.setVisible(true);
    
        // Add ActionListener to the letterField
        letterField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processGuess();
            }
        });
    }
    
    private void updateCountdown() {
        countdownSeconds--;
        if (countdownSeconds >= 0) {
            countdownLabel.setText("Countdown: " + countdownSeconds + " sec");
        } else {
            if (attemptsLeft > 0 && hiddenWord.indexOf("_") != -1 && incorrectGuessCount > 0) {
                // Only end the game if there are no attempts left, the word hasn't been guessed,
                // and there have been incorrect guesses
                timer.stop();
                endGame();
            } else if (attemptsLeft > 0 && hiddenWord.indexOf("_") == -1) {
                // If the word has been fully guessed before the countdown ends, continue the game
                timer.stop();
            }
        }
    }
      
    
    // Method to initialize the game variables
    private void initializeGame() {
        selectedWord = getRandomWord(selectedCategory);
        hiddenWord = new StringBuilder();
        for (int i = 0; i < selectedWord.length(); i++) {
            hiddenWord.append("_ ");
        }
        attemptsLeft = 6;
        incorrectGuessCount = 0;
    }

    // Method to get the formatted hidden word string
    private String getHiddenWordString() {
        return "Word: " + hiddenWord.toString();
    }

    // Method to process a guess
    private void processGuess() {
        String guess = letterField.getText();
        if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
            JOptionPane.showMessageDialog(gameFrame, "Please enter a valid single letter.");
            return;
        }

        if (selectedWord.contains(guess)) {
            updateHiddenWord(guess.charAt(0));
        } else {
            incorrectGuessCount++;
            updateHangman();
            attemptsLeft--;
        }

        wordLabel.setText(getHiddenWordString());
        attemptsLabel.setText("Attempts left: " + attemptsLeft);

        if (attemptsLeft == 0 || hiddenWord.indexOf("_") == -1) {
            endGame();
        }

        letterField.setText("");
    }

    // Method to update the hidden word with a correct guess
    private void updateHiddenWord(char letter) {
        for (int i = 0; i < selectedWord.length(); i++) {
            if (selectedWord.charAt(i) == letter) {
                hiddenWord.setCharAt(i * 2, letter);
            }
        }
    }

    // Method to update the hangman ASCII art
    private void updateHangman() {
        if (incorrectGuessCount < HANGMAN_ASCII.length) {
            hangmanLabel.setText("<html><pre>" + HANGMAN_ASCII[incorrectGuessCount] + "</pre></html>");
        }
    }

    // Method to end the game and display the result
    private void endGame() {
        timer.stop(); // Stop the countdown timer

        String message;
        if (attemptsLeft == 0) {
            message = "You ran out of attempts. \nThe word was: " + selectedWord;
        } else if (hiddenWord.indexOf("_") == -1) {
            message = "Congratulations! \nYou guessed the word: " + selectedWord;
        } else {
            message = "You ran out of time. \nThe word was: " + selectedWord;
        }

    // Display a message dialog with the game result
        JOptionPane.showMessageDialog(gameFrame, message);

    // Close the current game frame
        gameFrame.dispose();

    // Ask the user if they want to play again
        int option = JOptionPane.showConfirmDialog(gameFrame, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            showCategorySelectionFrame(); // Show the category selection frame
        } else {
            JOptionPane.showMessageDialog(this, "Thank you for playing. \nHave a nice day!!");
            System.exit(0); // Exit the application
        }
    }
    

    // Method to get a random word from a specified category
    private String getRandomWord(String category) {
        switch (category.toLowerCase()) {
            case "country":
                return getRandomWordFromList(COUNTRIES);
            case "color":
                return getRandomWordFromList(COLORS);
            case "animal":
                return getRandomWordFromList(ANIMALS);
            default:
                return "unknown category";
        }
    }

    // Method to get a random word from a list
    private String getRandomWordFromList(List<String> wordList) {
        Random random = new Random();
        int randomIndex = random.nextInt(wordList.size());
        return wordList.get(randomIndex);
    }

    // Method to get all available categories
    private String[] getAllCategories() {
        return new String[]{"Country", "Color", "Animal"};
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LetterDetectiveGame::new);
    }
}
