package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;

import java.util.ArrayList;
import java.util.Arrays;

public class ChanceCard26 extends ChanceCardBase implements ICommodity {

    public ChanceCard26() {
        super.name = "Kidd's chart (Keep this card). You may sail to the far side of Pirate Island, on to the square marked with an anchor. Land this chart there, and take treasure up to 7 in total value from Treasure Island.";
        super.description = "This card should be recorded as held by the player. When they arrive at Anchor Bay, treasure from Treasure Island should be put in their ship and the card returned to the pack.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        if (Arrays.equals(ship.getCoords(), new int[]{0, 1})) {
            super.takeTreasure(ship, mapGrid[7][7].getIsland(), 7);
            ship.removeCommodity(this);
            chanceCardDeck.add(this);
        }
        return true;
    }

    @Override
    public int getValue() {
        return 0;
    }
}
