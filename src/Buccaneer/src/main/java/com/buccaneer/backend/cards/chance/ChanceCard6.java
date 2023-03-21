package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;
import java.util.Arrays;

public class ChanceCard6 extends ChanceCardBase {

    public ChanceCard6() {
        super.name = "You are blown to the nearest port in the direction you are heading. If your crew total is 3 or less, take 4 crew cards from Pirate Island.";
        super.description = "You would need to use the orientation of the player's ship to calculate which port. A simpler possibility would be to replace this card with \"You are blown to Amsterdam...\".";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {

        int[] coords = Arrays.copyOf(ship.getCoords(), ship.getCoords().length);
        switch (ship.getDirection()) {
            case "N" -> coords[1] = 1;
            case "S" -> coords[1] = 18;
            case "E" -> coords[0] = 18;
            case "W" -> coords[0] = 1;
            case "NW" -> {coords[1] -= 7; coords[0] -= 7;}
            case "NE" -> {coords[1] -= 7; coords[0] += 7;}
            case "SE" -> {coords[1] += 7; coords[0] += 7;}
            case "SW" -> {coords[1] += 7; coords[0] -= 7;}
        }
        int[] closestPort = goToNearestPort(coords, islands);
        ship.setCoords(closestPort, mapGrid);

        if (ship.getCrewCards().length <= 3)
            super.takeCrew(mapGrid, ship, crewCards, 4, false);
        return false;
    }

    private int[] goToNearestPort(int[] coords, ArrayList<Island> islands) {
        Island island = null;
        double prev = 0;
        for (Island is : islands) {
            int[][] dock = is.getDockCoords();
            if (dock.length == 1) {
                int[] dockCoords = dock[0];
                double distance = Math.sqrt(Math.pow(coords[0] - dockCoords[0], 2) + Math.pow(coords[1] - dockCoords[1], 2));
                if (island == null || distance <= prev) {
                    island = is;
                    prev = distance;
                }
            }
        }
        assert island != null;
        return island.getDockCoords()[0];
    }
}
