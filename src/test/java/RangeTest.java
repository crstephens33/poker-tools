import poker.game.HoleCards;
import org.junit.jupiter.api.Test;
import range.Range;
import util.OutputVerifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeTest {
//consider creating Term, Expression, and Combination objects that convert between each other well. Instead of just Strings
//refactor "test framework" to be able to consolidate code that compares input/expected output for "A, B, C: X, Y, Z"

    @Test
    public void testExpressionCombinations() throws Exception {

        Map<String, List<String>> inputToExpectedOutputs = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/range/ExpectedExpressionCombinations.txt");
        for(String expression : inputToExpectedOutputs.keySet()) {
            List<String> expectedCombinations = inputToExpectedOutputs.get(expression);
            Set<HoleCards> expectedHoleCards = new HashSet<>();
            for(String combination : expectedCombinations) {
                expectedHoleCards.add(HoleCards.getHoleCardsFromCombination(combination));
            }
            Set<HoleCards> actualHoleCards = Range.generateHoleCardsFromExpression(expression);
            assertEquals(expectedHoleCards, actualHoleCards);
        }

    }

    @Test
    public void testRangeValidation() throws Exception {
        Map<String, List<String>> inputAndExpectedOutputs = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/range/ExpectedValidRangeExpression.txt");
        for(String input : inputAndExpectedOutputs.keySet()) {
            Set<String> expectedTermSet = new HashSet<>(inputAndExpectedOutputs.get(input));

            String actualRangeString = Range.getValidRangeString(input);
            HashSet<String> actualTermSet = new HashSet<>();
            String[] actualTerms = actualRangeString.split(", ");
            for(String actualTerm : actualTerms) {
                if(actualTerm.length() > 0)
                    actualTermSet.add(actualTerm);
            }
            assertEquals(expectedTermSet, actualTermSet);
            assertEquals(expectedTermSet.size(), actualTermSet.size());
        }
    }

    //range combinations should match all term validations as well.
    @Test
    public void testRangeCombinations() throws Exception {
        Map<String, List<String>> inputToExpectedOutput = OutputVerifier.parseFileForInputsAndExpectedOutputs("src/test/resources/range/ExpectedRangeCombinations.txt");
        for(String range : inputToExpectedOutput.keySet()) {
            List<String> expectedCombinations = inputToExpectedOutput.get(range);
            Set<HoleCards> expectedHoleCards = new HashSet<>();
            for(String combination : expectedCombinations) {
                expectedHoleCards.add(HoleCards.getHoleCardsFromCombination(combination));
            }
            Set<HoleCards> actualHoleCards = Range.getCombinationsFromRangeString(range);
            assertEquals(expectedHoleCards, actualHoleCards);
        }
    }
}
