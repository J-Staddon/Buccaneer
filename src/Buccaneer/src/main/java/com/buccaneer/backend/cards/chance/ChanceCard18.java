package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard18 extends ChanceCardBase {

    public ChanceCard18() {
        super.name = "Take treasure up to 4 in total value, and if your crew total is 7 or less, take 2 crew cards from Pirate Island.";
        super.description = "Limit of 2 treasures applies. If crew total is 7 or less, C1 applies.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        super.takeTreasureOrCard(mapGrid, ship, crewCards, 2, 4);
        if (ship.getCrewCards().length <= 7)
            super.takeCrew(mapGrid, ship, crewCards, 2, false);
        return false;
    }

}
