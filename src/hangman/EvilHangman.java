package hangman;

import guess.GuessInput;

import java.io.IOException;
import java.io.File;

public class EvilHangman
{

    public static void main(String[] args) throws IOException, GuessAlreadyMadeException, EmptyDictionaryException
    {
        File dictionary = new File(args[0]);
        int wordLen = Integer.parseInt(args[1]);
        int guessCounter = Integer.parseInt(args[2]);
        int currentGuessCount = 0;
        EvilHangmanGame newGame = new EvilHangmanGame();
        newGame.startGame(dictionary, wordLen);

        while (guessCounter > currentGuessCount)
        {
            System.out.printf("You have %d guesses left\n", guessCounter - currentGuessCount);
            StringBuilder totalLettersUsed = new StringBuilder();
            for (char c : newGame.getGuessedLetters())
            {
                totalLettersUsed.append(c).append(" ");
            }
            System.out.println("Used letters: " + totalLettersUsed);

            StringBuilder hangmanWordSoFar = new StringBuilder();

            for (char c : newGame.getWordSoFar())
            {
                hangmanWordSoFar.append(c);
            }

            System.out.println("Word: " + hangmanWordSoFar);
            System.out.print("Enter guess: ");

            char guess = GuessInput.getNextGuess();
            try
            {
                newGame.makeGuess(guess);
                if (newGame.getNumberOfCorrectSpacesOnLastGuess() > 0)
                {
                    System.out.println("Yes, there is " + newGame.getNumberOfCorrectSpacesOnLastGuess() + " " + guess);
                }
                else
                {
                    System.out.println("Sorry, there are no " + guess + "'s");
                    currentGuessCount++;
                }
                if (newGame.isGameOver())
                {
                    System.out.println("You win!");
                    System.out.println("The word was: " + newGame.getWord());
                    return;
                }
                else if (currentGuessCount >= guessCounter)
                {
                    System.out.println("You lose!");
                    System.out.println("The word was: " + newGame.getWord());
                    return;
                }
            }
            catch (GuessAlreadyMadeException e)
            {
                System.out.println("You already guessed that letter");
            }
            System.out.println();
        }
    }
}
