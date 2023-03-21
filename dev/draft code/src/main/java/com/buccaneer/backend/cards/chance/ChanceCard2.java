package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard2 extends ChanceCardBase {

    public ChanceCard2() {
        super.name = "Present this card to any player who must then give you 3 crew cards. This card must be used at once then returned to the Chance card pack.";
        super.description = "The player is given a choice of which of the other three players gives them crew cards.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        super.takeCrew(mapGrid, ship, crewCards, 3, true);
        return false;
    }


}
