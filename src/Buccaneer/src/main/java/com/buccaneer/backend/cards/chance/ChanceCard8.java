package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;

import java.util.*;

public class ChanceCard8 extends ChanceCardBase {

    public ChanceCard8() {
        super.name = "One treasure from your ship or 2 crew cards from your hand are lost and washed overboard to Flat Island.";
        super.description = "Take the least valuable treasure if any, otherwise take the two lowest value cards from the player's hand and assign to Flat Island.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {

        Island flatIsland = mapGrid[15][14].getIsland();
        Optional<Treasure> sortedTreasure = Arrays.stream(ship.getTreasure()).min(Comparator.comparing(Treasure::getValue));
        if (sortedTreasure.isPresent()) {
            Treasure treasure = sortedTreasure.get();
            ship.removeCommodity(treasure);
            flatIsland.addCommodity(treasure);
            return false;
        }

        List<CrewCard> sortedCrewCards = Arrays.stream(ship.getCrewCards()).sorted(Comparator.comparing(CrewCard::getValue)).toList();
        if (sortedCrewCards.size() > 0) {
            int limit = 2;
            for (CrewCard c : sortedCrewCards) {
                if (limit == 0)
                    break;
                ship.removeCommodity(c);
                flatIsland.addCommodity(c);
                limit--;
            }
        }
        return false;
    }
}
