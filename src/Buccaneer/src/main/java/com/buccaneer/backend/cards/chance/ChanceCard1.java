package com.buccaneer.backend.cards.chance;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class ChanceCard1 extends ChanceCardBase {

    private final HashMap<String, int[]> locations;

    public ChanceCard1() {
        super.name = "Your ship is blown 5 leagues (5 squares) off the coast of Treasure Island. If your crew total is 3 or less, take 4 crew cards from Pirate Island. If the square you are blown to is already occupied, move one square further";
        super.description = "The player is moved 5 squares away from the nearest side. If they are at a corner square of the island.png they are moved away diagonally. After the move, they are given a choice of which direction they wish to face.";

        locations = new HashMap<>();
        locations.put("7 7", new int[]{4,4});
        locations.put("7 8", new int[]{2,8});
        locations.put("7 9", new int[]{2,9});
        locations.put("7 1", new int[]{2,10});
        locations.put("7 11", new int[]{2,11});
        locations.put("7 12", new int[]{2,17});
        locations.put("8 12", new int[]{8,17});
        locations.put("9  12", new int[]{9,17});
        locations.put("10 12", new int[]{10,17});
        locations.put("11 12", new int[]{11,17});
        locations.put("12 12", new int[]{15,15});
        locations.put("12 11", new int[]{17,11});
        locations.put("12 10", new int[]{17,10});
        locations.put("12 9", new int[]{17,9});
        locations.put("12 8", new int[]{17,8});
        locations.put("12 7", new int[]{17,2});
        locations.put("11 7", new int[]{11,2});
        locations.put("10 7", new int[]{10,2});
        locations.put("9 7", new int[]{9,2});
        locations.put("8 7", new int[]{8,2});
    }


    /* Unique Override, code applied to unique chance cards.
    Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        int[] coords = ship.getCoords();
        int[] newLocation = locations.get(coords[0] + " " + coords[1]);
        ship.setCoords(newLocation, mapGrid);
        if (mapGrid[newLocation[0]][newLocation[1]].getShips().length > 1) {
            switch (ship.getDirection()) {
                case "N" -> newLocation[1]--;
                case "S" -> newLocation[1]++;
                case "E" -> newLocation[0]++;
                case "W" -> newLocation[0]--;
            }
            ship.setCoords(newLocation, mapGrid);
        }
        if (ship.getCrewCards().length <= 3)
            super.takeCrew(mapGrid, ship, crewCards, 4, false);

        Dialog<String> dialog = new Dialog<>();
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Select Ship direction:"));
        for (String s : new String[]{"N", "S", "E", "W", "NW", "SW", "SE", "NE"}) {
            Button b = new Button(s);
            b.setOnAction(i -> {
                ship.setDirection(s, mapGrid);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                dialog.close();
            });
            vbox.getChildren().add(b);
        }
        dialog.getDialogPane().setContent(vbox);
        dialog.show();
        return false;
    }
}
