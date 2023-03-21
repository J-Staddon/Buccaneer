package com.buccaneer.backend;

import com.buccaneer.backend.cards.chance.*;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;

import java.util.*;
import java.util.stream.Stream;

/**
 * Class to manage correct loading of game data
 * on to the board.
 */
public class ObjectLoader {

    private final ArrayList<Ship> ships;
    private final ArrayList<Treasure> treasures;
    private ArrayList<Island> islands;
    private ArrayList<CrewCard> crewCards;
    private ArrayList<ChanceCardBase> chanceCardBases;
    private ArrayList<int[]> outOfBounds;

    public ObjectLoader() {
        ships = new ArrayList<>(4);
        islands = new ArrayList<>();
        crewCards = new ArrayList<>(36);
        chanceCardBases = new ArrayList<>(28);
        treasures = new ArrayList<>(20);
        outOfBounds = new ArrayList<>();
    }

    /**
     * Loads all data in correct order
     * and adds it to the map
     * @param names
     * contains player names
     * @param table
     * mapsquare grid
     */
    public void loadMap(String[] names, MapSquare[][] table) {

        try {
            loadShips(names);
            loadIslands(table);
            loadCrewCards();
            loadChanceCards();
            loadTreasure();

            for (Island island : islands) {
                int[] coords = island.getGroundCoords();
                int[][] dockCoords = island.getDockCoords();
                outOfBounds.add(coords);
                table[coords[0]][coords[1]].setIsland(island);
                table[dockCoords[0][0]][dockCoords[0][1]].setDock(island);
            }
        } catch (Exception e) { //Recursive call to reload, in rare situations treasure allocation causes crashes
            ships.clear();
            islands.clear();
            crewCards.clear();
            chanceCardBases.clear();
            treasures.clear();
            loadMap(names, table);
        }
    }

    /**
     * Loads the last game
     * @param saveData
     */

    public void loadSave(SaveData saveData) {
        MapSquare[][] mapGrid = saveData.getMapGrid();
        for (MapSquare[] mapSquares : mapGrid) {
            for (MapSquare mapSquare : mapSquares) {
                for (Ship ship : mapSquare.getShips()) {
                    ship.setCoords(ship.getCoords(), mapGrid);
                    ship.setDirection(ship.getDirection(), mapGrid);
                }
            }
        }
        for (Island island : saveData.getIslands()) {
            int[] coords = island.getGroundCoords();
            int[][] dockCoords = island.getDockCoords();
            mapGrid[coords[0]][coords[1]].setIsland(island);
            mapGrid[dockCoords[0][0]][dockCoords[0][1]].setDock(island);
        }

        for (ICommodity c : saveData.getTreasureIsland()) {
            mapGrid[7][7].getIsland().addCommodity(c);
        }
        for (ICommodity c : saveData.getFlatIsland()) {
            mapGrid[15][14].getIsland().addCommodity(c);
        }
        for (ICommodity c : saveData.getPirateIsland()) {
            mapGrid[1][0].getIsland().addCommodity(c);
        }

        mapGrid[7][7].getStyleClass().removeIf(i -> i.equals("backgroundIsland"));
        mapGrid[15][14].getStyleClass().removeIf(i -> i.equals("backgroundIsland"));
        mapGrid[1][0].getStyleClass().removeIf(i -> i.equals("backgroundIsland"));

        islands = saveData.getIslands();
        crewCards = saveData.getCrewCardDeck();
        chanceCardBases = saveData.getChanceCardDeck();
        outOfBounds = saveData.getOutOfBounds();
    }

    /**
     * Get User Data for Ships
     * Load into array list
     * @param names
     * names of the ships
     */
    private void loadShips(String[] names) {
        for (int i = 0; i < 4; i++)
            ships.add(new Ship(names[i], i));
    }

    /**
     * Load islands with randomised ships
     * @param table
     * mapsquare grid
     */
    private void loadIslands(MapSquare[][] table) {
        Collections.shuffle(ships);
        islands.add(new Island(table,"Port of Cadiz", new int[] {19, 6}, new int[] {18, 6}, ships.get(0), true));
        islands.add(new Island(table,"Port of Genoa", new int[] {0, 13}, new int[] {1, 13}, ships.get(1), true));
        islands.add(new Island(table,"Port of London", new int[] {13, 19}, new int[] {13, 18}, ships.get(2), true));
        islands.add(new Island(table,"Port of Marseilles", new int[] {6, 0}, new int[] {6, 1}, ships.get(3), true));
        islands.add(new Island(table,"Port of Venice", new int[] {6, 19}, new int[] {6, 18}, null, true));
        islands.add(new Island(table,"Port of Amsterdam", new int[] {13, 0}, new int[] {13, 1}, null, true));
        islands.add(new Island(table,"Mud Bay", new int[] {0, 19}, new int[] {0, 18}, null, false));
        islands.add(new Island(table,"Anchor Bay", new int[] {0, 0}, new int[] {0, 1}, null, false));
        islands.add(new Island(table,"Cliff Creek", new int[] {19, 0}, new int[] {19, 1}, null, false));
        multiIsland(table,"Flat Island", 15, 18, 18, 16);
        multiIsland(table,"Pirate Island", 1, 3, 4, 1);
        multiIsland(table,"Treasure Island", 8, 11, 11, 8);
    }

    /**
     * Load big islands with using corner coords
     * @param table
     * the mapsquare grid
     * @param name
     * names of the ships
     * @param x
     * @param y
     * @param z
     * @param w
     */
    private void multiIsland(MapSquare[][] table, String name, int x, int y, int z, int w) {
        int bigger1 = Math.max(x, y);
        int smaller1 = Math.min(x, y);
        int bigger2 = Math.max(z, w);
        int smaller2 = Math.min(z, w);

        for (int i = smaller1; i <= bigger1; i++)
            for (int e = smaller2; e <= bigger2; e++)
                islands.add(new Island(table, name, new int[] {e, i}, null, null, false));
    }

    /**
     * Allocates crew cards to islands and ships
     * Returns the remaining deck.
     */
    private void loadCrewCards() {
        for (int i = 0; i < 2; i++)
            for (int e = 0; e < 6; e++)
                for (int f = 1; f < 4; f++)
                    crewCards.add(new CrewCard(f, i == 0));
        Collections.shuffle(crewCards);

        for (Island island : islands) {
            if (island.isTradingPost() && island.getOwner() == null)
                for (int i = 0; i < 2; i++)
                    island.addCommodity(crewCards.remove(0));
            if (island.getOwner() != null)
                for (int i = 0; i < 5; i++)
                    island.getOwner().addCommodity(crewCards.remove(0));
        }
    }

    /**
     * Load treasure into trade posts
     * according to number of crew card values.
     * Then load remaining into treasure island.
     */
    private void loadTreasure() {
        for (int i = 0; i < 4; i++) {
            treasures.add(new Treasure("Diamonds", 5));
            treasures.add(new Treasure("Rubies", 5));
            treasures.add(new Treasure("Gold", 4));
            treasures.add(new Treasure("Pearls", 3));
            treasures.add(new Treasure("Barrels of Rum", 2));
        }
        Collections.shuffle(treasures);

        for (Island island : islands) {
            if (island.isTradingPost() && island.getOwner() == null) {
                int treasureValue = 8 - Arrays.stream(island.getCrewCards()).mapToInt(CrewCard::getValue).sum();

                if (treasureValue == 6) {
                    multiTreasure(island);
                } else {
                    Treasure item = treasures.stream().filter(i -> i.getValue() == treasureValue).findFirst().get();
                    treasures.remove(item);
                    island.addCommodity(item);
                }
            }
        }

        Island treasureIsland = islands.stream().filter(i -> i.getName().equals("Treasure Island")).findFirst().get();
        for (Treasure t : treasures)
            treasureIsland.addCommodity(t);
    }

    /**
     * Algorithm to handle rare instances where more than one item
     * of treasure would be required to make the value 8.
     * @param island
     */
    private void multiTreasure(Island island) {
        Treasure item, item2;
        Stream<Treasure> stream1 = treasures.stream().filter(i -> i.getValue() == 3);
        Stream<Treasure> stream2 = treasures.stream().filter(i -> i.getValue() == 2);
        Stream<Treasure> stream3 = treasures.stream().filter(i -> i.getValue() == 4);

        if (stream1.count() > stream2.count() && stream1.count() > stream3.count()) {
            List<Treasure> items = stream1.toList();
            item = items.remove(0);
            item2 = items.remove(0);
        } else {
            item = treasures.stream().filter(i -> i.getValue() == 2).findFirst().get();
            item2 = treasures.stream().filter(i -> i.getValue() == 4).findFirst().get();
        }
        treasures.remove(item);
        treasures.remove(item2);
        island.addCommodity(item);
        island.addCommodity(item2);
    }

    /**
     * Load all the chance cards into a deck.
     */
    private void loadChanceCards() {
        ChanceCardBase[] base = {
                new ChanceCard1(),  new ChanceCard2(),  new ChanceCard3(),  new ChanceCard4(),
                new ChanceCard5(),  new ChanceCard6(),  new ChanceCard7(),  new ChanceCard8(),
                new ChanceCard9(),  new ChanceCard10(), new ChanceCard11(), new ChanceCard12(),
                new ChanceCard13(), new ChanceCard14(), new ChanceCard15(), new ChanceCard16(),
                new ChanceCard17(), new ChanceCard18(), new ChanceCard19(), new ChanceCard20(),
                new ChanceCard21(), new ChanceCard22(), new ChanceCard23(), new ChanceCard24(),
                new ChanceCard25(), new ChanceCard26(), new ChanceCard27(), new ChanceCard28()
        };
        chanceCardBases.addAll(Arrays.asList(base));
        Collections.shuffle(chanceCardBases);
    }

    /**
     * @return
     */
    public ArrayList<CrewCard> getCrewCardDeck() {
        return crewCards;
    }

    /**
     * @return
     */
    public ArrayList<ChanceCardBase> getChanceCardDeck() {
        return chanceCardBases;
    }

    /**
     * @return
     */
    public ArrayList<int[]> getOutOfBounds() {
        return outOfBounds;
    }

    /**
     * Only use getter, don't use this to update islands on the board.
     * Use for tests
     * @return
     */
    public ArrayList<Island> getIslands() {
        return islands;
    }

    /**
     * Only use getter, don't use this to update ships on the board.
     * Use for tests
     * @return
     */
    public ArrayList<Ship> getShips() { return ships; }
}
