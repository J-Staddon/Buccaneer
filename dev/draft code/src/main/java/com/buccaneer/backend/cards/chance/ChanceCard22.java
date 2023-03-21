package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChanceCard22 extends ChanceCardBase {

    public ChanceCard22() {
        super.name = "Yellow fever! An epidemic of yellow fever strikes all ships and reduces the number of crew. Every player with more than 7 crew cards in their hand must bury the surplus crew cards at once on Pirate Island.";
        super.description = "The number of cards in each player's hand is checked. Any players with more than 7 cards will have the remainder returned to the pack. The game should select the lowest cards and return them.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {

        //Add all ships to a list
        ArrayList<Ship> ships = new ArrayList<>();
        for (MapSquare[] mapSquares : mapGrid) {
            for (MapSquare mapSquare : mapSquares) {
                Ship[] current = mapSquare.getShips();
                if (current != null) {
                    ships.addAll(Arrays.asList(current));
                }
            }
        }

        for (Ship s : ships) {
            List<CrewCard> sortedCrew = Arrays.stream(s.getCrewCards()).sorted(Comparator.comparing(CrewCard::getValue)).toList();
            if (sortedCrew.size() > 7) {
                for (CrewCard c : sortedCrew) {
                    if (s.getCrewCards().length <= 7) {
                        break;
                    }
                    s.removeCommodity(c);
                    crewCards.add(c);
                }
            }
        }
        return false;
    }
}
