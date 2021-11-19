import game.HoleCards;
import game.Simulation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

public class SimulationTest {

    private static final double ACCURACY_THRESHOLD = 0.01; // this is .01%, not 1%

    @Test
    public void testSanity() {
        HoleCards aces = HoleCards.getHoleCardsFromCombination("AcAd");
        HoleCards sevenTwoOff = HoleCards.getHoleCardsFromCombination("Ah2c");
        Simulation.getFlopHitChart(Arrays.asList(aces, sevenTwoOff));
        /*
        double[] expectedOutcomes = {88.74, 0.47, 10.78};
        //pre straight flush: [88.77080238088564, 0.47947093506760485, 10.749726684046758]
        double[] actualOutcomes = Simulation.determineHandEquity(aces, sevenTwoOff, new HashSet<>());
        System.out.println("Actual: " + Arrays.toString(actualOutcomes));
        System.out.println("Expected: " + Arrays.toString(expectedOutcomes));
        for(int i = 0; i < actualOutcomes.length; i++) {
            assert(Math.abs(actualOutcomes[i] - expectedOutcomes[i]) < ACCURACY_THRESHOLD);
        }*/
    }
}
