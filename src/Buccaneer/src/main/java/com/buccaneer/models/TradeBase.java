package com.buccaneer.models;

import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;

import java.util.ArrayList;
public abstract class TradeBase {
    protected ArrayList<CrewCard> crew;
    protected ArrayList<Treasure> treasure;
    protected ArrayList<ChanceCardBase> chance;

    /**
     * Handles data in array lists
     */
    public TradeBase() {
        crew = new ArrayList<>();
        treasure = new ArrayList<>();
        chance = new ArrayList<>();
    }
    protected final static ArrayList<Treasure> treasureIsland = new ArrayList<>();
    protected final static ArrayList<Treasure> flatIsland = new ArrayList<>();
    protected final static ArrayList<Treasure> pirateIsland = new ArrayList<>();
    protected final static ArrayList<CrewCard> treasureCrew = new ArrayList<>();
    protected final static ArrayList<CrewCard> flatCrew = new ArrayList<>();
    protected final static ArrayList<CrewCard> pirateCrew = new ArrayList<>();

    /**
     * Gets all the treasure in array
     * @return
     */
    public Treasure[] getTreasure() {
        ArrayList<Treasure> sorted = this instanceof Island ? sortTreasure() : treasure;
        Treasure[] treasure = new Treasure[sorted.size()];
        for (int i = 0; i < treasure.length; i++) {
            treasure[i] = sorted.get(i);
        }
        return treasure;
    }

    /**
     * Gets crew cards in array
     * @return
     */
    public CrewCard[] getCrewCards() {
        ArrayList<CrewCard> sorted = this instanceof Island ? sortCrewCard() : crew;
        CrewCard[] crewCards = new CrewCard[sorted.size()];
        for (int i = 0; i < crewCards.length; i++) {
            crewCards[i] = sorted.get(i);
        }
        return crewCards;
    }

    /**
     * Gets chance cards in array
     * @return
     */
    public ChanceCardBase[] getChanceCards() {
        ChanceCardBase[] chanceCards = new ChanceCardBase[chance.size()];
        for (int i = 0; i < chanceCards.length; i++) {
            chanceCards[i] = chance.get(i);
        }
        return chanceCards;
    }

    /**
     * Adds data to array
     * @param commodity
     */
    public void addCommodity(ICommodity commodity) {
        if (commodity instanceof ChanceCardBase)
            chance.add((ChanceCardBase) commodity);
        else if (commodity instanceof Treasure)
            (this instanceof Island ? sortTreasure() : treasure).add((Treasure) commodity);
        else
            (this instanceof Island ? sortCrewCard() : crew).add((CrewCard) commodity);
    }

    /**
     * Removes data from array
     * @param commodity
     */
    public void removeCommodity(ICommodity commodity) {
        if (commodity instanceof ChanceCardBase)
            chance.remove((ChanceCardBase) commodity);
        else if (commodity instanceof Treasure)
            (this instanceof Island ? sortTreasure() : treasure).remove((Treasure) commodity);
        else
            (this instanceof Island ? sortCrewCard() : crew).remove((CrewCard) commodity);
    }

    /**
     * Sorts treasure array
     * @return
     */
    private ArrayList<Treasure> sortTreasure() {
        return switch (((Island)this).getName()) {
            case "Treasure Island" -> treasureIsland;
            case "Flat Island" -> flatIsland;
            case "Pirate Island" -> pirateIsland;
            default -> this.treasure;
        };
    }

    /**
     * Sorts crew card array
     * @return
     */
    private ArrayList<CrewCard> sortCrewCard() {
        return switch (((Island)this).getName()) {
            case "Treasure Island" -> treasureCrew;
            case "Flat Island" -> flatCrew;
            case "Pirate Island" -> pirateCrew;
            default -> this.crew;
        };
    }
}
