package Suggestion;

import info.debatty.java.stringsimilarity.CharacterSubstitutionInterface;

public class WordSimilarityHelper
{
    /**
     * Most used letter of the english alphabet are
     * e, t, a ,i ....
     * https://en.oxforddictionaries.com/explore/which-letters-are-used-most/
     * https://www.uselessdaily.com/world/which-is-the-most-commonly-used-letter-in-the-alphabet/
     * On a querty keyboard adjacent letters are
     * e -> w,r
     * t-> r, y
     * a-> s
     * i -> o,u
     * Reducing the weight for these letter will produce better error/typo understanding.
     * @return char Inteface
     */
    public CharacterSubstitutionInterface getCharInterface()
    {
        return (c1, c2) ->
        {
            if ((c1 == 'e' && c2 == 'r') ||
                    (c1 == 'e' && c2 == 'w') ||
                    (c1 == 'r' && c2 == 'e') ||
                    (c1 == 'w' && c2 == 'e') ||
                    (c1 == 't' && c2 == 'r') ||
                    (c1 == 'r' && c2 == 't') ||
                    (c1 == 't' && c2 == 'y') ||
                    (c1 == 'y' && c2 == 't') ||
                    (c1 == 'a' && c2 == 's') ||
                    (c1 == 's' && c2 == 'a') ||
                    (c1 == 'i' && c2 == 'o') ||
                    (c1 == 'o' && c2 == 'i') ||
                    (c1 == 'i' && c2 == 'u') ||
                    (c1 == 'u' && c2 == 'i'))
            {
                return 0.8;
            }

            return 1.0;
        };
    }
}
