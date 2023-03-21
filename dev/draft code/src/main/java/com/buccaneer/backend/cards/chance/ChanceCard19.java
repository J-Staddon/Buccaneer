package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

public class ChanceCard19 extends ChanceCardBase {

    public ChanceCard19() {
        super.name = "Exchange all crew cards in your hand as far as possible for the same number of crew cards from Pirate Island.";
        super.description = "Return all player's cards to bottom of pack, and deal same number from top of pack. If the pack was smaller than the number of crew cards in your hand, then it’s possible that you might end up getting some, or all of your original cards back.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {

        CrewCard[] crewCards1 = ship.getCrewCards();

        for (CrewCard c : crewCards1) {
            ship.removeCommodity(c);
            crewCards.add(c);
        }

        ArrayList<CrewCard> toRemove = new ArrayList<>();
        for (int i = 0; i < crewCards1.length; i++) {
            CrewCard c = crewCards.get(i);
            ship.addCommodity(c);
            toRemove.add(c);
        }
        crewCards.removeAll(toRemove);
        return false;
    }

}
