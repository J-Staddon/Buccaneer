package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ChanceCard9 extends ChanceCardBase {

    public ChanceCard9() {
        super.name = "Your most valuable treasure on board or if no treasure, the best crew card from your hand is washed overboard to Flat Island.";
        super.description = "Take most valuable treasure if any, otherwise take highest value cards from player's hand and assign to Flat Island.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        Island flatIsland = mapGrid[15][14].getIsland();
        Optional<Treasure> sortedTreasure = Arrays.stream(ship.getTreasure()).max((f, s) -> Math.max(f.getValue(), s.getValue()));
        if (sortedTreasure.isPresent()) {
            Treasure treasure = sortedTreasure.get();
            ship.removeCommodity(treasure);
            flatIsland.addCommodity(treasure);
            return false;
        }

        Optional<CrewCard> sortedCrewCards = Arrays.stream(ship.getCrewCards()).max((f, s) -> Math.max(f.getValue(), s.getValue()));
        if (sortedCrewCards.isPresent()) {
            CrewCard c = sortedCrewCards.get();
            ship.removeCommodity(c);
            flatIsland.addCommodity(c);
        }
        return false;
    }
}
