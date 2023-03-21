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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChanceCard20 extends ChanceCardBase {

    public ChanceCard20() {
        super.name = "If the ship of another player is anchored at Treasure Island, exchange 2 of your crew cards with that player. Both turn your cards face down and take 2 cards from each others hands without looking at them. If there is no other player at Treasure Island, place 2 of your crew cards on Pirate Island.";
        super.description = "If more than one other player is adjacent to Treasure Island, let player with the Chance card choose which other player to swap with. Game will then randomly select two cards from each player's hand and exchange them. If one of the players has less than two cards, then they will give the maximum number of cards that they have available. If no other player is anchored at Treasure Island, then the game will return the player's lowest two cards to the pack.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        ArrayList<Ship> options = new ArrayList<>();
        int[][] treasureCoords = mapGrid[7][7].getIsland().getDockCoords();
        for (int[] coords : treasureCoords) {
            Ship[] ships = mapGrid[coords[0]][coords[1]].getShips();
            if (ships != null) {
                for (Ship s : ships) {
                    if (!s.equals(ship)) {
                        options.add(s);
                    }
                }
            }
        }

        if (!options.isEmpty()) {

            //Handle selected exchange
            Dialog<String> dialog = new Dialog<>();
            VBox vbox = new VBox(new Label("Pick Ship to exchange crew cards with: "));
            for (Ship s : options) {
                Button t = new Button(s.getPlayerName());
                t.setOnAction(i -> exchangeItems(dialog, ship, s));
                vbox.getChildren().add(t);
            }
            dialog.getDialogPane().setContent(vbox);
            dialog.show();
        } else {

            //Return to Pirate Island
            List<CrewCard> sortedCrew = Arrays.stream(ship.getCrewCards()).sorted(Comparator.comparing(CrewCard::getValue)).toList();
            int limit = 2;
            for (CrewCard c : sortedCrew) {
                if (limit == 0) {
                    break;
                }
                ship.removeCommodity(c);
                crewCards.add(c);
                limit--;
            }
        }
        return false;
    }

    void exchangeItems(Dialog<String> dialog, Ship ship1, Ship ship2) {
        int limit = 2;
        for (CrewCard c : ship1.getCrewCards()) {
            if (limit == 0) {
                limit = 2;
                break;
            }
            ship2.addCommodity(c);
            ship1.removeCommodity(c);
            limit--;
        }

        for (CrewCard c : ship2.getCrewCards()) {
            if (limit == 0) {
                break;
            }
            ship1.addCommodity(c);
            ship2.removeCommodity(c);
            limit--;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.close();
    }

}
