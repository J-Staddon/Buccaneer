package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard15 extends ChanceCardBase {

    public ChanceCard15() {
        super.name = "Take 2 crew cards from Pirate Island.";
        super.description = "";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        super.takeCrew(mapGrid, ship, crewCards, 2, false);
        return false;
    }

}
