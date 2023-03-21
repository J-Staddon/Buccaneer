package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;

import java.util.*;

public class ChanceCard7 extends ChanceCardBase {

    public ChanceCard7() {
        super.name = "One treasure from your ship or 2 crew cards from your hand are lost and washed overboard to the nearest ship. If 2 ships are equidistant from yours you may ignore this instruction.";
        super.description = "The game must calculate nearest ship from the number of non-land squares needed to reach the other ship. If two ships are equally nearest, then nothing should be done. If there is treasure in the player's ship and room for extra treasure in the other player's ship, assign the least valuable treasure to the other player, otherwise take two lowest value cards from the player's hand and assign them to the other player (or one card if only one card in player's hand).";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        ArrayList<Ship> ships = new ArrayList<>();
        for (MapSquare[] mapSquares : mapGrid) {
            for (MapSquare mapSquare : mapSquares) {
                if (mapSquare.getShips() != null) {
                    ships.addAll(Arrays.asList(mapSquare.getShips()));
                }
            }
        }
        ships.remove(ship);

        Ship nearest = nearestShip(ship.getCoords(), ships);
        if (nearest == null) {
            return false;
        }

        Optional<Treasure> sortedTreasure = Arrays.stream(ship.getTreasure()).min(Comparator.comparing(Treasure::getValue));
        if (sortedTreasure.isPresent()) {
            Treasure treasure = sortedTreasure.get();
            ship.removeCommodity(treasure);
            nearest.addCommodity(treasure);
            return false;
        }

        List<CrewCard> sortedCrewCards = Arrays.stream(ship.getCrewCards()).sorted(Comparator.comparing(CrewCard::getValue)).toList();
        if (sortedCrewCards.size() > 0) {
            int limit = 2;
            for (CrewCard c : sortedCrewCards) {
                if (limit == 0)
                    break;
                ship.removeCommodity(c);
                nearest.addCommodity(c);
                limit--;
            }
        }
        return false;
    }

    private Ship nearestShip(int[] coords, ArrayList<Ship> ships) {
        boolean same = false;
        Ship ship = null;
        double prev = 0;
        for (Ship s : ships) {
            int[] shipCoords = s.getCoords();
            double distance = Math.sqrt(Math.pow(coords[0] - shipCoords[0], 2) + Math.pow(coords[1] - shipCoords[1], 2));
            if (ship == null || distance <= prev) {
                same = prev != distance;
                ship = s;
                prev = distance;
            }
        }
        return same ? ship : null;
    }

}
