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

public class ChanceCard4 extends ChanceCardBase {

    public ChanceCard4() {
        super.name = "You are blown to Cliff Creek. If your crew total is 3 or less, take 4 crew cards from Pirate Island.";
        super.description = "Move player's ship to nominated square. C1 applies. Allow player to select orientation of ship.";
    }

    /* Unique Override, code applied to unique chance cards. Remove if chance card has no unique attributes */
    @Override
    public boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck) {
        ship.setCoords(new int[]{19,1}, mapGrid);
        if (ship.getCrewCards().length <= 3)
            super.takeCrew(mapGrid, ship, crewCards, 4, false);
        return false;
    }


}
