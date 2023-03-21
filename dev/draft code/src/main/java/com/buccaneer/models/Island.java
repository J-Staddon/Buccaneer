package com.buccaneer.models;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.models.commodities.Treasure;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Island extends TradeBase {

    private String name;
    private int[] groundCoords;
    private int[][] dockCoords;
    private Ship owner;
    private boolean isTradingPost;
    private ArrayList<Treasure> secureStorage;

    /**
     * Docking coordinates for islands
     */
    private final static int[][] treasureDock = {{7,7}, {8,7}, {9,7}, {10,7}, {11,7},
                                                 {12,7}, {12,8},  {12,9}, {12,10}, {12,11},
                                                 {12,12}, {11,12}, {10,12}, {9,12}, {8,12},
                                                 {7,12}, {7,11},  {7,10}, {7,9}, {7,8}};

    private final static int[][] flatDock = {{15,14}, {16,14}, {17,14}, {18,14}, {19,14}, {19,15},
                                             {19,16}, {19,17}, {19,18}, {19,19}, {18,19}, {17,19},
                                             {16,19}, {15,19}, {15,18}, {15,17}, {15,16}, {15,15}};

    private final static int[][] pirateDock = {{1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {5,1},
                                               {5,2}, {5,3}, {5,4}, {4,4}, {3,4}, {2,4},
                                               {1,4}, {0,4}, {0,3}, {0,2}};

    public Island() {}

    /**
     * Holds information about an island
     * @param table
     * @param name
     * @param groundCoords
     * @param dockCoords
     * @param owner
     * @param isTradingPost
     */
    public Island(MapSquare[][] table, String name, int[] groundCoords, int[] dockCoords, Ship owner, boolean isTradingPost) {
        super();
        this.name = name;
        this.groundCoords = groundCoords;
        this.dockCoords = new int[][] {dockCoords};
        this.owner = owner;
        this.isTradingPost = isTradingPost;
        this.secureStorage = new ArrayList<>();

        if (owner != null)
            owner.setCoords(dockCoords, table);
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int[] getGroundCoords() {
        return groundCoords;
    }

    /**
     * @return
     */
    public Ship getOwner() {
        return owner;
    }

    /**
     * @return
     */
    public int[][] getDockCoords() {
        return sortDock();
    }

    /**
     * @return
     */
    public boolean isTradingPost() {
        return isTradingPost;
    }

    /**
     * Sorts docks into an island or a port
     * @return
     */
    private int[][] sortDock() {
        return switch (this.getName()) {
            case "Treasure Island" -> treasureDock;
            case "Flat Island" -> flatDock;
            case "Pirate Island" -> pirateDock;
            default -> this.dockCoords;
        };
    }

    /**
     * Gets treasure securely stored in the dock
     * @return
     */
    public Treasure[] getSecureStorage() {
        ArrayList<Treasure> sorted = secureStorage;
        Treasure[] treasure = new Treasure[sorted.size()];
        for (int i = 0; i < treasure.length; i++) {
            treasure[i] = sorted.get(i);
        }
        return treasure;
    }

    /**
     * Adds treasure to the secure storage of a dock
     * @param secureStorage
     */
    public void addSecureStorage(Treasure secureStorage) {
        this.secureStorage.add(secureStorage);
    }
}