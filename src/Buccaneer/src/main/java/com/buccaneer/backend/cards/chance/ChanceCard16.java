package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard16 extends ChanceCardBase {

    public ChanceCard16() {
        super.name = "Take treasure up to 7 in total value and reduce your ship's crew to 10, by taking crew cards from your hand and placing them on Pirate Island.";
        super.description = "Limit of 2 treasures applies. If crew total is greater than 10, then it should be reduced until it is less than or equal to 10 by returning cards to the pack. It should be as high as possible after this operation (i.e. if it is 11, then crew card with value 1 should be removed rather than a crew card of value 2, for example).";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        super.takeTreasure(ship, mapGrid[7][7].getIsland(), 7);
        if (ship.getCrewCards().length > 10) {
            for (CrewCard c : ship.getCrewCards()) {
                if (ship.getCrewCards().length == 10)
                    break;
                ship.removeCommodity(c);
                crewCards.add(c);
            }
        }
        return false;
    }

}
