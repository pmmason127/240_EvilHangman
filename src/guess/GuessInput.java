package guess;

import java.util.Scanner;

public class GuessInput
{
    private static final Scanner _scanner = new Scanner(System.in);

    public static char getNextGuess()
    {
        while (true)
        {
            String guessInput = _scanner.next();
            // check if input is a single character
            if (guessInput.length() == 1)
            {
                // check if input is alphabetic a-z and A-Z
                if (guessInput.matches("[a-zA-Z]"))
                {
                    return guessInput.toLowerCase().charAt(0);
                }
            }
            System.out.printf("'%s' is incorrect input. Please try again.\n", guessInput);

            System.out.print("Enter guess: ");
        }
    }
}
