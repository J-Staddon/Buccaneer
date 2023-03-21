package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;

import java.util.ArrayList;

public class ChanceCard5 extends ChanceCardBase {

    public ChanceCard5() {
        super.name = "You are blown to your Home Port. If your crew total is 3 or less, take 4 crew cards from Pirate Island. Empty ship of any treasure.";
        super.description = "Move player's ship to nominated square. C1 applies. Empty ship of any treasure.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        for (int i = 0; i < 20; i++)
            for (int e = 0; e < 20; e++) {
                Island island = mapGrid[i][e].getIsland();
                Ship owner = island == null ? null : island.getOwner();
                if (owner != null && ship.getPlayerName().equals(island.getOwner().getPlayerName())) {
                    ship.setCoords(island.getDockCoords()[0],mapGrid);
                }
            }

        if (ship.getCrewCards().length <= 3)
            super.takeCrew(mapGrid, ship, crewCards, 4, false);
        Treasure[] removeTreasure = ship.getTreasure();
        Island treasureIsland = mapGrid[7][7].getIsland();
        for(Treasure T: removeTreasure){
            ship.removeCommodity(T);
            treasureIsland.addCommodity(T);
        }
        return false;
    }


}
