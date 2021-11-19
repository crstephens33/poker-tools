package poker.game;

import java.util.ArrayList;
import java.util.List;

public class Card implements Comparable<Card> {

    public static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
    public final static String[] suits = {"c", "d", "h", "s"};
    public final static String[] names = {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king", "ace"};
    public final static String[] pluralNames = {"twos", "threes", "fours", "fives", "sixes", "sevens", "eights", "nines", "tens", "jacks", "queens", "kings", "aces"};

    private String rank;
    private String suit;

    public Card(String rankSuit) {
        rankSuit = rankSuit.trim();
        rankSuit = rankSuit.replace("10", "T");
        if(rankSuit.length() != 2) {
            System.out.println("Card input bad: [" + rankSuit + "]");
            System.exit(1);            
        }
        rank = "" + rankSuit.charAt(0);
        suit = "" + rankSuit.charAt(1);
        rank = rank.toUpperCase();
        suit = suit.toLowerCase();
    }

    public String getRank() {
        return rank;
    }

    public int getRankInt() { return getValue(rank); }

    public String getSuit() {
        return suit;
    }

    public int getSuitInt() {
        switch(suit) {
            case "c": return 0;
            case "d": return 1;
            case "h": return 2;
            case "s": return 3;
            default: return -1;
        }
    }

    public String toString() {
        return rank + suit;
    }

    public int compareTo(Card otherCard) {
        int thisRank = getValue(rank);
        int otherRank = otherCard.getRankInt();
        String otherSuit = otherCard.getSuit();
        if(thisRank > otherRank ) {
            return 1;
        } else if (thisRank < otherRank) {
            return -1;
        } else if (!suit.equals(otherSuit)) {
            return suit.compareTo(otherSuit) * -1; //if same rank, split ties by suit. So Ac will always come before Ah
        }
        return 0;
    }

    public int compareRank(Card other) {
        return getValue(rank).compareTo(getValue(other.getRank()));
    }

    //do some refactoring later on to compare just on rank
    public static Integer getValue(String rank) {
        switch( rank ) {
            case "A" : return 14;
            case "K" : return 13;
            case "Q" : return 12;
            case "J" : return 11;
            case "T" : return 10;
            default : return Integer.parseInt(rank);
        }
    }

    public static String getRankFromInt(int value) {
        return ranks[value - 2];
    }

    public static String getName(int value, boolean plural) {
        String[] nameArray = plural ? pluralNames : names;
        return nameArray[value - 2];
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Card) {
            Card otherCard = (Card) other;
            return otherCard.getRank().equals(rank) && otherCard.getSuit().equals(suit);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return rank.hashCode() * 52 + suit.hashCode();
    }

    public static String convertToStartingHand(String cards) {
        String[] arr = cards.split(",");
        Card a = new Card(arr[0]);
        Card b = new Card(arr[1]);
        Card higher = a.compareTo(b) > 0 ? a : b;
        Card lower = a.equals(higher) ? b : a;
        String suited = a.getSuit().equals(b.getSuit()) ? "s" : "o";    
        if(higher.getRank().equals(lower.getRank()))
            suited = "";  
        return higher.getRank() + lower.getRank() + suited;
    }

    public static List<Card> readCardsFromString(String handString) {
        List<Card> cards = new ArrayList<>();
        for(int i = 0; i < handString.length(); i += 2) {
            cards.add(new Card(handString.substring(i, i+2)));
        }
        return cards;
    }

    public static String getHandStringFromCards(List<Card> cards) {
        StringBuilder builder = new StringBuilder();
        cards.forEach(builder::append);
        return builder.toString();
    }
}