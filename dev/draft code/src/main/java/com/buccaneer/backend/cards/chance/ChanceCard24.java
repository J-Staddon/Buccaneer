package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;

import java.util.ArrayList;

public class ChanceCard24 extends ChanceCardBase implements ICommodity {

    public ChanceCard24() {
        super.name = "Pieces of eight (Keep this card). This card may be traded for crew or treasure up to value 4 in any port you visit.";
        super.description = "This card should be recorded as held by the player. When they sail to a port that is not their Home Port, then it should be offered as a trading option, and returned to the pack of Chance cards after use.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        return true;
    }

    @Override
    public int getValue() {
        return 4;
    }
}
