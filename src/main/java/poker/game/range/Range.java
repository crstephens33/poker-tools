package poker.game.range;

import java.util.*;

import poker.game.basics.Card;
import poker.game.basics.HoleCards;

public class Range {

    //Range: Comma separated set of terms i.e. "QQ+, 77, 32o+"
    //Terms: "QQ+", "77", "32o+", "32o", "AdKd"
    //Expressions: "QQ", "77", "32o", "AdKd" Expressions are a subset of terms, where an expression has no "+" notation.
    //Combinations: "AdKd", "9s8s" Combination is subset of expression, but in 4-character RankSuitRankSuit Format

    //a "Range" is a 2D Array of Set<HoleCards>s.
    //biggest question is how to model the range. is it just a set? no ordering whatsoever?
    //trying just a set on 8/14/2021

    //perhaps refactor some of the rangeBuilding static stuff into a new class. Validation, etc. Range can just be the object

    private final static String rangeTermPattern = "([AKQJT98765432][AKQJT98765432])\\+?|([AKQJT98765432][cdhs]){2}|([AKQJT98765432]{2}[so]\\+?)";

    private final static String[] pocketPairSuits = {"cd", "ch", "cs", "dh", "ds", "hs"};

    private final static String[] unsuitedSuits = {"cd", "ch", "cs", "dc", "dh", "ds", "hc", "hd", "hs", "sc", "sd", "sh"};

    private final static String[] suitedSuits = { "cc", "dd", "hh", "ss"};

    private enum ExpressionType {PocketPair, Offsuit, Suited, AllSuits, Combination};

    private final Set<HoleCards> rangeCombinations = new HashSet<>();

    public static void main(String[] args) {
        testRangeValidation();
    }

    private static void testRangeValidation() {
        String[] testInputs = {
              //"AA", "22+, AdKd, JTo, 720, 98s, Td9d", "JdTo", "AdKs, AQ", "AdAd", "22, 33, 76s, 97cd"
                "A4s, A5s, AK, ATs+, KQs, JJ+"
        };
        for(String input : testInputs) {
            System.out.println(input + ": " + new Range(input).getCombinationsString());
        }
    }

    public Range (String rangeString) {
        Set<HoleCards> combinations = getCombinationsFromRangeString(rangeString);
        combinations.forEach(this::addToRange);
    }

    public static Set<HoleCards> getCombinationsFromRangeString(String rangeString) {
        Set<HoleCards> combinations = new HashSet<>();
        List<String> expressions = parseAllExpressionsFromRangeString(rangeString);
        expressions.forEach(expression -> combinations.addAll(generateHoleCardsFromExpression(expression)));
        return combinations;
    }

    //Keep logic to add and remove from the range in one API each - all logic is replaced here
    public void addToRange(HoleCards holeCards) {
        rangeCombinations.add(holeCards);
    }

    public void removeFromRange(HoleCards holeCards) {
        rangeCombinations.remove(holeCards);
    }

    public int getNumberOfCombinations() {
        return rangeCombinations.size();
    }

    public String getCombinationsString() {
        List<HoleCards> combinationsList = new ArrayList<>(rangeCombinations);
        combinationsList.sort(Comparator.reverseOrder());
        return combinationsList.toString();
    }

    public Set<HoleCards> getRangeCombinations() {
        return rangeCombinations;
    }

    //DO NOT assume input is valid
    //really just need to convert all the XX+, XXx+ terms into all enumerations. I.e. JJ+ -> [JJ, QQ, KK, AA]
    private static List<String> parseAllExpressionsFromRangeString(String rangeString) {
        List<String> expressions = new ArrayList<>();
        String validRangeString = Range.getValidRangeString(rangeString);
        if(validRangeString.trim().length() == 0) return new ArrayList<>();

        List<String> eachTerm = Arrays.asList(validRangeString.split(", "));
        eachTerm.forEach(term -> {
            expressions.addAll(getAllExpressionsFromTerm(term));
        });
        return expressions;
    }

    public static List<String> getAllExpressionsFromTerm(String term) {
        List<String> expressions = new ArrayList<>();
        if(term.contains("+")) {
            //handle "+" notation here - convert term into list of expressions
            List<String> expressionsFromPlusTerm = convertPlusTermToListOfExpressions(term);
            expressions.addAll(expressionsFromPlusTerm);
        } else { //if no "+", term must be an expression
            expressions.add(term.trim());
        }
        return expressions;
    }

    private static List<String> convertPlusTermToListOfExpressions(String term) {
        String initialExpression = term.replace("+", "");
        ExpressionType expressionType = determineExpressionType(initialExpression);
        if(expressionType == null) {
            System.out.println("Expression not valid: " + initialExpression);
            return new ArrayList<>();
        }
        switch(expressionType) {
            case PocketPair: return getAllPocketPairExpressionsAbove(initialExpression);
            case Offsuit:
            case Suited:
                return getAllExpressionsAboveUnpaired(initialExpression);
            case AllSuits: return getAllSuitedAndUnsuitedExpressionsAbove(initialExpression);
            default: return new ArrayList<>();
        }
    }

    private static List<String> getAllPocketPairExpressionsAbove(String pocketPair) {
        List<String> pocketPairs = new ArrayList<>();
        String pocketPairRank = pocketPair.substring(0, 1);
        boolean restOfRanks = false;
        for(int i = 0; i < Card.ranks.length; i++) {
            String rank = Card.ranks[i];
            if(restOfRanks || rank.equals(pocketPairRank)) {
                restOfRanks = true;
                pocketPairs.add(rank + rank);
            }
        }
        return pocketPairs;
    }

    private static List<String> getAllExpressionsAboveUnpaired(String unsuitedExpression) {
        List<String> expressions = new ArrayList<>();
        String higherRank = unsuitedExpression.substring(0, 1);
        String lowerRank = unsuitedExpression.substring(1,2);
        String suitedness = unsuitedExpression.length() > 2 ? unsuitedExpression.substring(2, 3) : "";
        for(int i = Card.getValue(lowerRank) - 2; i < Card.ranks.length; i++) {
            String currentRank = Card.ranks[i];
            if(currentRank.equals(higherRank))
                break;
            if(Card.getValue(currentRank).compareTo(Card.getValue(lowerRank)) > -1 ) { //TODO: make "Rank" a comparable object itself
                expressions.add(higherRank + currentRank + suitedness);
            }
        }
        return expressions;
    }

    private static List<String> getAllSuitedAndUnsuitedExpressionsAbove(String expression) {
        List<String> allExpressions = new ArrayList<>();
        allExpressions.addAll(getAllExpressionsAboveUnpaired(expression + "o"));
        allExpressions.addAll(getAllExpressionsAboveUnpaired(expression + "s"));
        return allExpressions;
    }

    public static String getValidRangeString(String initialRangeString) {
        StringBuilder validTerms = new StringBuilder();
        boolean appendComma = false;
        String[] terms = initialRangeString.split(", ");
        for(String term : terms) {
            if(term.trim().length() > 0 && passesTermValidation(term)) {
                if(appendComma)
                    validTerms.append(", ");
                validTerms.append(term);
                appendComma = true;
            }
        }
        return validTerms.toString();
    }

    //All term validations will be included here
    private static boolean passesTermValidation(String term) {

        //must match the regex.
        if(!term.matches(rangeTermPattern))
            return false;

        //Commenting below to allow terms like T8+ for now, since T8 is valid.
        //if(term.length() == 3 && term.charAt(2) == '+' && term.charAt(0) != term.charAt(1))
        //  return false;

        //AdAd condition - cannot use same card twice
        if(term.length() == 4 && term.substring(0, 2).equals(term.substring(2, 4)))
            return false;

        //prohibit terms like 7As, 7A+, 7A
        //all non combinations (length is not 4 or last character is +) must not have 2nd char larger than 1st
        if(term.length() != 4 || term.charAt(3) == '+' || term.charAt(2) == '+') {
            int rank1 = Card.getValue(term.substring(0,1));
            int rank2 = Card.getValue(term.substring(1,2));
            if(rank2 > rank1)
                return false;
        }

        //prohibit terms like QQo
        //terms that are longer than 2 characters that are pocket pairs can only have + after them.
        if(term.length() > 2 && term.substring(0,1).equals(term.substring(1,2)) && term.charAt(2) != '+')
            return false;

        return true;
    }

    /**
     * Construct new range based off initial range - similar to clone
     * @param initialRange
     */
    public Range (Range initialRange) {
    }

    /**
     * Construct empty Range
     */
    public Range() {

    }

    /**
     * Generates the collection of combinations (suit specific) from a given implicit hand expression. I.e., from "AA", will return AcAd, AcAh, AcAs... etc.
     * DOES NOT SUPPORT "XX+" FORMAT. Designed only for a specific expressions i.e. AKo, JTs, 33.
     * Assumes expression is valid.
     * @param expression
     * @return
     */
    public static Set<HoleCards> generateHoleCardsFromExpression(String expression) {
        if(expression.contains("+")) {
            throw new UnsupportedOperationException("Does not support \"+\" formatted expressions.");
        }
        //determine condition - is this pocket pair, suited or unsuited combo?
        ExpressionType expressionType = determineExpressionType(expression);
        if(expressionType == null) {
            System.out.println("No suitable case found for expression " + expression);
            return new HashSet<>();
        }
        switch(expressionType) {
            case PocketPair: return generatePocketPairCombinations(expression);
            case Offsuit: return generateUnsuitedCombinations(expression);
            case Suited: return generateSuitedCombinations(expression);
            case AllSuits: return generateCombinationsAllSuits(expression);
            case Combination: return new HashSet<HoleCards>(Arrays.asList(HoleCards.getHoleCardsFromCombination(expression)));
            default: return new HashSet<>();
        }
    }


    /**
     * Generates all combinations of pocket pairs
     * @param pocketPairExpression
     * @return
     */
    private static Set<HoleCards> generatePocketPairCombinations(String pocketPairExpression) {
        String rank = pocketPairExpression.substring(0, 1);
        return generateGenericCombinations(rank, rank, pocketPairSuits);
    }

    private static Set<HoleCards> generateCombinationsAllSuits(String unpairedExpression) {
        String rank1 = String.valueOf(unpairedExpression.charAt(0));
        String rank2 = String.valueOf(unpairedExpression.charAt(1));
        Set<HoleCards> allSuitCombos = generateGenericCombinations(rank1, rank2, suitedSuits);
        allSuitCombos.addAll(generateGenericCombinations(rank1, rank2, unsuitedSuits));
        return allSuitCombos;
    }

    private static Set<HoleCards> generateSuitedCombinations(String unpairedExpression) {
        String rank1 = String.valueOf(unpairedExpression.charAt(0));
        String rank2 = String.valueOf(unpairedExpression.charAt(1));
        return generateGenericCombinations(rank1, rank2, suitedSuits);
    }

    private static Set<HoleCards> generateUnsuitedCombinations(String suitedCombinations) {
        String rank1 = String.valueOf(suitedCombinations.charAt(0));
        String rank2 = String.valueOf(suitedCombinations.charAt(1));
        return generateGenericCombinations(rank1, rank2, unsuitedSuits);
    }


    /**
     * Generates all combinations of rank and suit for specified ranks and suits. Assumes valid input.
     * Calling ("K", "A", ["sh", "cd"]) will return a set with {AsKh} and {AcKd}
     * @param rank1 Card 1 rank
     * @param rank2 Card 2 rank
     * @param suitCombinations Array of two-character strings specifying possible combinations of suits
     * @return
     */
    private static Set<HoleCards> generateGenericCombinations(String rank1, String rank2, String[] suitCombinations) {
        Set<HoleCards> combinations = new HashSet<>();
        Arrays.asList(suitCombinations).forEach(suitCombo -> combinations.add(new HoleCards(new Card(rank1 + suitCombo.charAt(0)),
                new Card(rank2 + suitCombo.charAt(1)))));
        return combinations;
    }

    private static ExpressionType determineExpressionType(String expression) {
        if(expression.length() == 2 && expression.charAt(0) == expression.charAt(1)) {
            return ExpressionType.PocketPair;
        } else if(expression.length() == 3 && expression.toLowerCase().charAt(2) == 'o') {
            return ExpressionType.Offsuit;
        } else if(expression.length() == 3 && expression.toLowerCase().charAt(2) == 's') {
            return ExpressionType.Suited;
        } else if(expression.length() == 2) {//pocket pair condition already tested- "AK" or "76". suited and unsuited
            return ExpressionType.AllSuits;
        } else if(expression.length() == 4) { //must be combination such as AdKd
            return ExpressionType.Combination;
        }
        return null;
    }
}
