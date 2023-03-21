package com.buccaneer.backend;

import com.buccaneer.backend.cards.chance.ChanceCard21;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.*;
import java.util.function.IntBinaryOperator;

/**
 * Class to manage the turn system, movement,
 * trading and attacking of ships on the board.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameLoop {

    private boolean moved, turned, fight;
    private Ship ShipTurn;
    private ArrayList<int[]> possibleMoves;
    private int index;

    private final String[] order = {"Port of London", "Port of Genoa", "Port of Marseilles", "Port of Cadiz"};
    private ArrayList<int[]> outOfBounds;
    private ArrayList<Island> islands;

    public GameLoop() {}

    /**
     * @param islands
     * @param outOfBounds
     * @param MapGrid 20x20 Grid
     */
    public GameLoop(ArrayList<Island> islands, ArrayList<int[]> outOfBounds, MapSquare[][] MapGrid, AnchorPane move_scene) {
        this.islands = islands;
        this.outOfBounds = outOfBounds;
        this.fight = false;
        index = 0;
        run(MapGrid, move_scene);
    }

    /**
     * Checks if current player has moved and starts the next turn
     * @param MapGrid 20x20 Grid
     */
    public void endTurn(MapSquare[][] MapGrid, AnchorPane move_scene) {
        if (moved) {
            index = index == 3 ? 0 : index+1;
            run(MapGrid, move_scene);
        }
    }

    /**
     * Main initialisation of player turn, shows potential move spots.
     * @param MapGrid 20x20 Grid
     */
    public void run(MapSquare[][] MapGrid, AnchorPane move_scene) {
        for (Island island : islands)
            if (island.getName().equals(order[index]) && !fight)
                ShipTurn = island.getOwner();

        possibleMoves = new ArrayList<>();
        int value = ShipTurn.moveDistance();

        Island island = isIslandDock(MapGrid, ShipTurn.getCoords());
        if (island != null && island.getDockCoords().length < 2)
            ShipTurn.setDirection("Any", MapGrid);

        switch (ShipTurn.getDirection()) {
            case "N" -> calculateMove(MapGrid, value, null, (a, b) -> a - b);
            case "S" -> calculateMove(MapGrid, value, null, Integer::sum);
            case "E" -> calculateMove(MapGrid, value, Integer::sum, null);
            case "W" -> calculateMove(MapGrid, value,(a, b) -> a - b, null);
            case "NE" -> calculateMove(MapGrid, value, Integer::sum, (a, b) -> a - b);
            case "NW" -> calculateMove(MapGrid, value, (a, b) -> a - b, (a, b) -> a - b);
            case "SE" -> calculateMove(MapGrid, value, Integer::sum, Integer::sum);
            case "SW" -> calculateMove(MapGrid, value, (a, b) -> a - b, Integer::sum);
            default -> calculateAll(MapGrid, value);
        }

        moved = false;
        turned = false;
        moveStarted(MapGrid, move_scene);
    }

    /**
     * Calculates the positions a ship can move to using
     * direction data of the ship.
     * @param value
     * @param x
     * @param y
     */
    private void calculateMove(MapSquare[][] MapGrid, int value, IntBinaryOperator x, IntBinaryOperator y) {
        int[] coords = ShipTurn.getCoords();

        for (int i = 1; i <= value; i++) {
            int coordOne = x != null ? x.applyAsInt(coords[0], i) : coords[0];
            int coordTwo = y != null ? y.applyAsInt(coords[1], i) : coords[1];
            int[] newCoord = new int[]{coordOne, coordTwo};

            //Prevent moving to invalid or same square as ship on port
            if (coordOne < 0 || coordTwo < 0 || coordOne > 19 || coordTwo > 19 ||
                outOfBounds.stream().anyMatch(k -> Arrays.equals(k, newCoord)))
                return;
            //Prevent ships from jumping over eachother
            if (MapGrid[coordOne][coordTwo].getShips().length > 0 ) {
                possibleMoves.add(newCoord);
                return;
            }
            possibleMoves.add(newCoord);
        }
    }

    /**
     * Calculates all possible movements in all directions in situations
     * where a ship is leaving a port.
     * @param MapGrid 20x20 Grid
     * @param value
     */
    private void calculateAll(MapSquare[][] MapGrid, int value) {
        calculateMove(MapGrid, value, null, (a, b) -> a - b);
        calculateMove(MapGrid, value, null, Integer::sum);
        calculateMove(MapGrid, value, Integer::sum, null);
        calculateMove(MapGrid, value, (a, b) -> a - b, null);
        calculateMove(MapGrid, value, Integer::sum, (a, b) -> a - b);
        calculateMove(MapGrid, value, (a, b) -> a - b, (a, b) -> a - b);
        calculateMove(MapGrid, value, Integer::sum, Integer::sum);
        calculateMove(MapGrid, value, (a, b) -> a - b, Integer::sum);
    }

    /**
     * Changes the direction of a ship if it isn't in a port.
     * @param direction
     * @param MapGrid 20x20 Grid
     */
    public void direction(String direction, MapSquare[][] MapGrid) {
        if (turned || fight)
            return;

        Island island = isIslandDock(MapGrid, ShipTurn.getCoords());
        if (island == null || island.getDockCoords().length > 1) {
            moveDone(MapGrid);
            ShipTurn.setDirection(direction, MapGrid);
            moved = true;
            turned = true;
        }
    }

    /**
     * Allows a ship to move providing the square picked is
     * an allowed coordinate.
     * @param newCoords
     * @param MapGrid 20x20 Grid
     */
    public boolean move(int[] newCoords, MapSquare[][] MapGrid) {

        if (moved || possibleMoves.stream().noneMatch(x -> Arrays.equals(x, newCoords)))
            return false;

        int[] coords = ShipTurn.getCoords();
        StringBuilder sb = new StringBuilder();

        if (newCoords[1] > coords[1]) sb.append("S");
        else if (newCoords[1] < coords[1]) sb.append("N");

        if (newCoords[0] > coords[0]) sb.append("E");
        else if (newCoords[0] < coords[0]) sb.append("W");

        moveDone(MapGrid);

        ShipTurn.setDirection(sb.toString(), MapGrid);
        ShipTurn.setCoords(newCoords, MapGrid);
        fight = false;
        moved = true;

        return true;
    }

    /**
     * Used to handle Chance Card scenarios and execution
     * @param newCoords
     * @param chanceCards
     */
    public ChanceCardBase chanceCard(MapSquare[][] MapGrid, int[] newCoords, ArrayList<ChanceCardBase> chanceCards) {
        //Flat Island Detection
        Island flatIsland = MapGrid[15][14].getIsland();
        if (Arrays.stream(flatIsland.getDockCoords()).anyMatch(i -> Arrays.equals(i, newCoords))) {
            int limit = 2;
            for (Treasure t : flatIsland.getTreasure()) {
                if (limit == 0)
                    break;
                if (ShipTurn.getTreasure().length < 2) {
                    flatIsland.removeCommodity(t);
                    ShipTurn.addCommodity(t);
                    limit--;
                }
            }
            for (CrewCard c : flatIsland.getCrewCards()) {
                flatIsland.removeCommodity(c);
                ShipTurn.addCommodity(c);
            }
        }

        //Treasure Island Chance Card Detection
        Island treasureIsland = MapGrid[7][7].getIsland();
        for (int[] treasureIslandCoords : treasureIsland.getDockCoords())
            if (Arrays.equals(treasureIslandCoords, newCoords))
                return chanceCards.get(0);
        return null;
    }

    /**
     * Called when two ships are on the same map square.
     * @param MapGrid 20x20 Grid
     * @param newCoords
     */
    public Ship fight(MapSquare[][] MapGrid, int[] newCoords, AnchorPane move_scene) {

        if (isIslandDock(MapGrid, newCoords) != null)
            return null;

        Ship[] ships = MapGrid[newCoords[0]][newCoords[1]].getShips();
        Ship winner, loser;
        if (ships[0].attackPower() == ships[1].attackPower()) {
            winner = ships[1];
            loser = ships[0];
        } else {
            winner = ships[0].attackPower() > ships[1].attackPower() ? ships[0] : ships[1];
            loser = ships[0].attackPower() < ships[1].attackPower() ? ships[0] : ships[1];
        }
        loser.setDirection("Any", MapGrid);

        //Transfer treasure to ship or treasure island, if unavailable transfer crew cards
        Treasure[] loserTreasure = loser.getTreasure();
        List<CrewCard> sortedLoserCrew = Arrays.stream(loser.getCrewCards()).sorted(Comparator.comparing(CrewCard::getValue).reversed()).toList();
        Island treasureIsland = MapGrid[7][7].getIsland();
        if (loserTreasure.length > 0) {
            for (Treasure t : loserTreasure) {
                loser.removeCommodity(t);
                if (winner.getTreasure().length < 2)
                    winner.addCommodity(t);
                else
                    treasureIsland.addCommodity(t);
            }
        } else {
            for (int i = 0, e = sortedLoserCrew.size(); i < 2; i++) {
                if (e > 0) {
                    CrewCard crew = sortedLoserCrew.get(i);
                    loser.removeCommodity(crew);
                    winner.addCommodity(crew);
                    e--;
                }
            }
        }

        fight = true;
        ShipTurn = loser;
        run(MapGrid, move_scene);
        return winner;
    }

    /**
     * Populates UI with move possibilities
     * @param MapGrid 20x20 Grid
     */
    public void moveStarted(MapSquare[][] MapGrid, AnchorPane move_scene) {
        if (!moved && move_scene.isVisible()) {
            for (int[] item : possibleMoves) {
                int x = item[0];
                int y = item[1];

                //if there's a player at the coordinate, and there's not an island there
                if(MapGrid[x][y].getShips().length>0 && isIslandDock(MapGrid, new int[]{x, y})==null){
                    MapGrid[x][y].getStyleClass().add("fightImage");
                } else {
                    MapGrid[x][y].getStyleClass().add("shipImageTranslucent");
                }

            }
        }
    }

    /**
     * Clears UI move prompts when a move is made.
     * @param MapGrid 20x20 Grid
     */
    public void moveDone(MapSquare[][] MapGrid) {
        for (int[] item : possibleMoves) {
            int x = item[0];
            int y = item[1];
            MapGrid[x][y].getStyleClass().removeIf(i -> i.contains("shipImageTranslucent"));
            MapGrid[x][y].getStyleClass().removeIf(i -> i.contains("fightImage"));
        }
    }

    /**
     * Checks if coords given is an island and returns a pointer to it.
     * @param MapGrid 20x20 Grid
     * @param newCoords
     * @return
     */
    public Island isIslandDock(MapSquare[][] MapGrid, int[] newCoords) {
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null) {
                    int[][] dockCoords = island.getDockCoords();
                    for (int[] coords : dockCoords) {
                        if (Arrays.equals(coords, newCoords))
                            return island;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Function used to check and apply ship/island trades.
     * @param islandTrade
     * @param shipTrade
     * @param island
     * @return boolean indicating whether trade was successful
     */
    public boolean trade(ArrayList<ICommodity> islandTrade, ArrayList<ICommodity> shipTrade, ArrayList<ChanceCardBase> chanceCardDeck, Island island) {
        int islandSum = islandTrade.stream().mapToInt(ICommodity::getValue).sum();
        int shipSum = shipTrade.stream().mapToInt(ICommodity::getValue).sum();
        long islandTreasure = islandTrade.stream().filter(i -> i instanceof Treasure).count();
        int shipTreasure = ShipTurn.getTreasure().length;

        //Chance Card 21 can be traded for crew only
        if (shipTrade.stream().anyMatch(i -> i instanceof ChanceCard21) && islandTrade.stream().anyMatch(i -> i instanceof Treasure)) {
            islandSum -= islandTrade.stream().filter(i -> i instanceof CrewCard).mapToInt(ICommodity::getValue).sum() - 5;
        }

        //Make trade fail if treasure capacity is exceeded or trade is unequal
        if (islandSum > shipSum || islandTreasure + shipTreasure > 2)
            return false;

        for (ICommodity i : islandTrade) {
            ShipTurn.addCommodity(i);
            island.removeCommodity(i);
        }

        for (ICommodity i : shipTrade) {
            if (i instanceof ChanceCardBase) {
                ShipTurn.removeCommodity(i);
                chanceCardDeck.add((ChanceCardBase) i);
            } else {
                ShipTurn.removeCommodity(i);
                island.addCommodity(i);
            }
        }

        return true;
    }

    /**
     * Checks the win condition.
     * @param MapGrid 20x20 Grid
     * @return Ship if it has won or null if none have.
     */
    public Ship hasWon(MapSquare[][] MapGrid) {
        for (int i = 0; i < 20; i++)
            for (int e = 0; e < 20; e++) {
                MapSquare mapSquare = MapGrid[e][i];
                Island island = mapSquare.getIsland();

                if (island != null && island.getOwner() != null &&                                  //Check if Island and Owner is null
                    Arrays.asList(mapSquare.getShips()).contains(island.getOwner()) &&              //Check if owner is currently at island
                    Arrays.stream(island.getTreasure()).mapToInt(Treasure::getValue).sum() +
                    Arrays.stream(island.getSecureStorage()).mapToInt(Treasure::getValue).sum() >= 20)   //Check the value of treasure is greater or equal to 20
                    return island.getOwner();
            }
        return null;
    }

    /**
     * @return this.getStyleClass().add("bg"+ship.getIndex());
     */
    public Ship getCurrentPlayer() {
        return ShipTurn;
    };
}
