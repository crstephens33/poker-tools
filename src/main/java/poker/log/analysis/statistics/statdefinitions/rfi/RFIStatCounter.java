package poker.log.analysis.statistics.statdefinitions.rfi;

import poker.log.analysis.statistics.StatCounter;
import poker.log.analysis.statistics.statdefinitions.rfi.conditions.*;

import java.util.List;

public class RFIStatCounter extends StatCounter {

    /**
     * Statistics engine object for counting whether player had the chance to raise first in, and whether they open folded,
     * limped, or raised first in.
     *
     */

    public RFIStatCounter() {
        super("RFI");
        this.opportunityCondition = new RFIOpportunityCondition();
        this.potentialOutcomeConditions = List.of(new OpenFoldPreCondition(), new OpenLimpPreCondition(), new PreflopCheckCondition(), new RFICondition());
    }
}
