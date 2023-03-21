package com.buccaneer.models;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.commodities.CrewCard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ship extends TradeBase {
    private String playerName;
    private String direction;
    private int[] coords;
    private int index; //used to color code the ships

    public Ship() {}
    public Ship(String playerName, int index) {
        super();
        this.playerName = playerName;
        this.direction = "Any";
        this.coords = null;
        this.index = index;
    }

    /**
     * @return
     */
    public int[] getCoords() {
        return coords;
    }

    /**
     * Sets the coordinates of a ship
     * @param newCoords
     * @param MapGrid
     */
    public void setCoords(int[] newCoords, MapSquare[][] MapGrid) {
        if (coords != null) {
            MapGrid[coords[0]][coords[1]].removeShip(this);
        }
        MapGrid[newCoords[0]][newCoords[1]].addShip(this);
        coords = newCoords;
    }

    /**
     * @return
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets the direction of a ship
     * @param direction
     * @param MapGrid
     */
    public void setDirection(String direction, MapSquare[][] MapGrid) {

        //Remove the old styles
        MapGrid[coords[0]][coords[1]].getStyleClass().removeIf(x -> x.contains("shipImage"));
        MapGrid[coords[0]][coords[1]].getStyleClass().removeIf(x -> x.contains("bg"));


        MapGrid[coords[0]][coords[1]].getStyleClass().addAll(Arrays.asList(
                "shipImage"+direction,
                "bg"+index
        ));

        this.direction = direction;
    }

    /**
     * Calculates the move distance
     * @return
     */
    public int moveDistance() {
        return crew.size() > 0 ? crew.size() : 1;
    }

    /**
     * Calculates the attack power
     * @return
     */
    public int attackPower() {
        int red = crew.stream().filter(CrewCard::isRed).mapToInt(CrewCard::getValue).sum();
        int black = crew.stream().filter(i -> !i.isRed()).mapToInt(CrewCard::getValue).sum();
        return Math.abs(red - black);
    }

    /**
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Override for equating ships
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return this.playerName.equals(ship.playerName);
    }
}
