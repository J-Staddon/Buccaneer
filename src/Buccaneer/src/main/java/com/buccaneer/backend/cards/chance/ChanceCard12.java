package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard12 extends ChanceCardBase {

    public ChanceCard12() {
        super.name = "Take treasure up to 4 in total value, or 2 crew cards from Pirate Island.";
        super.description = "";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        super.takeTreasureOrCard(mapGrid, ship, crewCards, 2, 4);
        return false;
    }

}
