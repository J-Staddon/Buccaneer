package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class ChanceCard10 extends ChanceCardBase {

    public ChanceCard10() {
        super.name = "The best crew card in your hand deserts for Pirate Island. The card must be placed there immediately.";
        super.description = "Take highest value card from player's hand and return to crew card pack.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        Optional<CrewCard> sortedCrewCards = Arrays.stream(ship.getCrewCards()).min(Comparator.comparing(CrewCard::getValue));
        if (sortedCrewCards.isPresent()) {
            CrewCard c = sortedCrewCards.get();
            ship.removeCommodity(c);
            crewCards.add(c);
        }
        return false;
    }
}
