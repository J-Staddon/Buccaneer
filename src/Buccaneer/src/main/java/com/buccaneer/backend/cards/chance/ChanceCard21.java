package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;

import java.util.ArrayList;

public class ChanceCard21 extends ChanceCardBase implements ICommodity {

    public ChanceCard21() {
        super.name = "Long John Silver (Keep this card). When you arrive at a port where there are crew for sale, you may exchange Long John for up to 5 crew in value. If you land at a Port where Long John has been left, you may take him on payment of one treasure to the Port. Once Long John has been played, he is not returned to the pack.";
        super.description = "This card should be recorded as held by the player. When they sail to a port that is not their Home Port, then it should be offered as a trading option, but only for crew. I would simplify it by returning the card to the pack of Chance cards after use.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        return true;
    }

    @Override
    public int getValue() {
        return 5;
    }
}
