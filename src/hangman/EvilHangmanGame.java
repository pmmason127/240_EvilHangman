package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame
{
    private Set<String> _wordSet;
    private final SortedSet<Character> _lettersGuessed;
    private int _correctSpacesOnFinalGuess;
    private char[] _wordSoFar;


    public EvilHangmanGame()
    {
        _lettersGuessed = new TreeSet<>();
        _correctSpacesOnFinalGuess = 0;
        _wordSet = new HashSet<>();
    }

    @Override
    public void startGame(File dictionary, int wordLen) throws EmptyDictionaryException, IOException
    {
        Scanner scanner = new Scanner(dictionary);
        _wordSet.clear();
        while (scanner.hasNext())
        {
            String word = scanner.next().toLowerCase();
            word = word.replaceAll("[^a-zA-Z]", "");

            if (word.length() == wordLen)
            {
                _wordSet.add(word);
            }
        }

        if (_wordSet.size() == 0)
        {
            throw new EmptyDictionaryException();
        }

        _wordSoFar = new char[wordLen];

        for (int i = 0; i < wordLen; i++)
        {
            _wordSoFar[i] = '-';
        }
    }

    public char[] getWordSoFar()
    {
        return _wordSoFar;
    }

    private static String getPartitionID(String word, char letterChar)
    {
        StringBuilder build = new StringBuilder();
        for (char c : word.toCharArray())
        {
            if (c == letterChar)
            {
                build.append("1");
            }
            else
            {
                build.append("0");
            }
        }
        return build.toString();
    }

    private static int getNumberCorrect(String ID)
    {
        int counter = 0;
        for (char c : ID.toCharArray())
        {
            if (c == '1')
            {
                counter++;
            }
        }
        return counter;
    }

    private static String determineImportance(String first, String second)
    {
        int firstCharCount = getNumberCorrect(first);
        int secondCharCount = getNumberCorrect(second);
        // whichever has fewer characters in it is the priority partition
        if (firstCharCount < secondCharCount)
        {
            return first;
        }
        else if (firstCharCount > secondCharCount)
        {
            return second;
            // if equal partitions, go by whichever has the first rightmost letter
        }
        else
        {
            for (int i = 0; i < first.length(); i++)
            {
                boolean firstHasRightmostLetter = first.charAt(first.length() - i - 1) == '1';
                boolean secondHasRightmostLetter = second.charAt(second.length() - i - 1) == '1';

                if (firstHasRightmostLetter && !secondHasRightmostLetter)
                {
                    return first;
                }
                else if (!firstHasRightmostLetter && secondHasRightmostLetter)
                {
                    return second;
                }
            }
            // it should be impossible to get here. They should never be equal.
            throw new IllegalArgumentException("first ID and second ID are equal");
        }
    }

    @Override
    public Set<String> makeGuess(char newGuess) throws GuessAlreadyMadeException
    {
        // convert guess to lowercase
        newGuess = Character.toLowerCase(newGuess);
        if (_lettersGuessed.contains(newGuess))
        {
            throw new GuessAlreadyMadeException();
        }
        else
        {
            _correctSpacesOnFinalGuess = 0;
            _lettersGuessed.add(newGuess);

            Map<String, Set<String>> partitions = new HashMap<>();
            // add each word into a partition depending on its letter id (boolean[])
            for (String word : _wordSet)
            {
                String key = getPartitionID(word, newGuess);
                if (partitions.containsKey(key))
                {
                    partitions.get(key).add(word);
                }
                else
                {
                    partitions.put(key, new HashSet<>(List.of(word)));
                }
            }

            // determine the largest partition
            String biggestPartitionKey = "";
            Set<String> biggestPartition = new HashSet<>();

            for (String key : partitions.keySet())
            {
                // in the case that this partition is just outright larger than the previous
                if (partitions.get(key).size() > biggestPartition.size())
                {
                    biggestPartition = partitions.get(key);
                    biggestPartitionKey = key;
                    // in the case that the partitions are equal we'll need a tiebreaker
                }
                else if (partitions.get(key).size() == biggestPartition.size())
                {
                    biggestPartitionKey = determineImportance(biggestPartitionKey, key);
                    biggestPartition = partitions.get(biggestPartitionKey);

                }
            }
            _correctSpacesOnFinalGuess = getNumberCorrect(biggestPartitionKey);

            for (int i = 0; i < biggestPartitionKey.length(); i++)
            {
                if (biggestPartitionKey.charAt(i) == '1')
                {
                    _wordSoFar[i] = newGuess;
                }
            }
            _wordSet = biggestPartition;
            return biggestPartition;
        }
    }

    public boolean isGameOver()
    {
        for (char c : _wordSoFar)
        {
            if (c == '-')
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public SortedSet<Character> getGuessedLetters()
    {
        return _lettersGuessed;
    }

    public String getWord()
    {
        // get random word from the set of words
        String[] arrayNumbers = _wordSet.toArray(new String[0]);
        Random random = new Random();
        int randomIndex = random.nextInt(arrayNumbers.length);
        return arrayNumbers[randomIndex];
    }

    public int getNumberOfCorrectSpacesOnLastGuess()
    {
        return _correctSpacesOnFinalGuess;
    }
}














