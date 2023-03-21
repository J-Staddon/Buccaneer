package com.buccaneer.backend;

import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Useful class that serves a bridge between the backend and frontend.
 * Holds key backend data while also updating the stackpane that it extends.
 * These functions should not be called by anything that isn't in the data file or object loader.
 */
@JsonIgnoreProperties(value = {"styleableNode"})
public class MapSquare extends StackPane {
    private final ArrayList<Ship> ships;
    private Island dock;

    public MapSquare() {
        ships = new ArrayList<>();
    }

    /**
     * @return
     */
    public Island getIsland() {
        return dock;
    }

    /**
     * Sets UI elements for squares where an island exists.
     */
    public void setIsland(Island island) {
        if (!isJUnitTest()) {
            loadIslandUI("backgroundIsland", island);
        }
    }

    public void setDock(Island island) {
        this.dock = island;

        if (!isJUnitTest() && island.getDockCoords().length == 1) {
            Ship owner = island.getOwner();
            loadIslandUI(owner != null ? "backgroundDock" + owner.getIndex() : "backgroundDock", island);
        }
    }

    /**
     * Updates UI elements for a ship.
     * This function should only be called by the ship class.
     * It ensures the position it is on the board gets updated.
     * Use the ship or gameloop class for normal movement.
     * @param ship
     */
    public void addShip(Ship ship) {
        ships.add(ship);
        this.getStyleClass().add("shipImage"+ship.getDirection());
        this.getStyleClass().add("bg"+ship.getIndex());
    }

    /**
     * Updates UI elements for a ship.
     * Similar to the add function it should only be called
     * from the ship class.
     * @param ship
     */
    public void removeShip(Ship ship) {
        ships.removeIf(i -> i.getPlayerName().equals(ship.getPlayerName()));
        //remove all ship images
        this.getStyleClass().removeIf(x -> x.contains("shipImage"));
        this.getStyleClass().removeIf(x -> x.contains("bg"));

        //if there's still a ship remaining, add it
        if (ships.size() > 0)
            //adding separately causes crash, unsure why.
            this.getStyleClass().addAll(Arrays.asList(
                    "shipImage"+ships.get(0).getDirection(),
                    "bg"+ships.get(0).getIndex()
            ));
    }

    /**
     * Get a copy of all the ship data in a square.
     * @return
     */
    public Ship[] getShips() {
        Ship[] ship = new Ship[ships.size()];
        for (int i = 0; i < ship.length; i++) {
            ship[i] = ships.get(i);
        }
        return ship;
    }

    /**
     * Loads tooltip name to indicate island names when hovering
     * @param background
     * @param island
     */
    private void loadIslandUI(String background, Island island) {
        Tooltip tooltip = new Tooltip(island.getName());
        Tooltip.install(this, tooltip);
        this.getStyleClass().removeIf(x -> x.contains("backgroundIsland"));
        this.getStyleClass().add(background);
    }

    /**
     * Function to mitigate UI API calls if a test is being performed.
     * @return
     */
    private static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }
}
