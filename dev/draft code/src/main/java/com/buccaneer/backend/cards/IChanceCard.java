package com.buccaneer.backend.cards;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;

import java.util.ArrayList;

/**
 * Chance card interface
 */
public interface IChanceCard {
    boolean execute(MapSquare[][] mapGrid, Ship ship, ArrayList<CrewCard> crewCards, ArrayList<Island> islands, ArrayList<ChanceCardBase> chanceCardDeck);
}
