package com.buccaneer.controllers;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.GameLoop;
import com.buccaneer.backend.ObjectLoader;
import com.buccaneer.backend.SaveData;
import com.buccaneer.backend.cards.chance.ChanceCard21;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.TradeBase;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;

import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;
import com.buccaneer.custom.CommodityImageView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardController {

    private GameLoop game;
    private MapSquare[][] MapGrid;
    private ArrayList<Island> islands;
    private ArrayList<int[]> outOfBounds;
    private ArrayList<CrewCard> crewCardDeck;
    private ArrayList<ChanceCardBase> chanceCardDeck;
    private ArrayList<ICommodity> islandTrade;
    private ArrayList<ICommodity> shipTrade;
    private Scene win;
    private WinController controller;

    /**
     * Loads the game board from a save
     * @param data
     */
    public void loadSave(SaveData data) {
        ObjectLoader obj = new ObjectLoader();

        obj.loadSave(data);

        islands = obj.getIslands();
        outOfBounds = obj.getOutOfBounds();
        crewCardDeck = obj.getCrewCardDeck();
        chanceCardDeck = obj.getChanceCardDeck();
        shipTrade = new ArrayList<>();
        islandTrade = new ArrayList<>();

        MapGrid = data.getMapGrid();
        game = data.getGameLoop();

        loadGrid(true);

        game.run(MapGrid, move_scene);
        currentPlayer.setText(game.getCurrentPlayer().getPlayerName());
        currentPlayer.getStyleClass().clear();
        currentPlayer.getStyleClass().add("bg"+game.getCurrentPlayer().getIndex());

        updateInventory(game.getCurrentPlayer());
    }

    /**
     * Loads the game board
     * @param names
     */
    public void startGame(String[] names) {
        ObjectLoader obj = new ObjectLoader();
        //Load map, ports, ships with given names
        obj.loadMap(names, MapGrid);

        //Get remaining decks of cards that haven't been placed on the map.
        crewCardDeck = obj.getCrewCardDeck();
        chanceCardDeck = obj.getChanceCardDeck();
        outOfBounds = obj.getOutOfBounds();
        islands = obj.getIslands();
        islandTrade = new ArrayList<>();
        shipTrade = new ArrayList<>();

        this.game = new GameLoop(islands, outOfBounds, MapGrid, move_scene);

        currentPlayer.setText(game.getCurrentPlayer().getPlayerName());
        currentPlayer.getStyleClass().clear();
        currentPlayer.getStyleClass().add("bg"+game.getCurrentPlayer().getIndex());

        updateInventory(game.getCurrentPlayer());
    }

    // transforms coordinates from the indexes in the grid
    // to actual positions on the board
    private int[] transformCoordinates(int a, int b) {
        int[] coords = new int[2];
        coords[1] = b + 1;
        coords[0] = MapGrid.length - a;
        return coords;
    }

    @FXML
    private Button tradeButton;

    @FXML
    private Label inventoryText;

    @FXML
    private Label islandText;

    @FXML
    private Text playerText;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label currentPlayer;

    @FXML
    public AnchorPane move_scene;

    @FXML
    private VBox inventory_scene1;

    @FXML
    private VBox inventory_scene2;

    @FXML
    private AnchorPane instructions_scene;

    @FXML
    void initialize() {
        loadGrid(false);
    }

    /**
     * Loads in the game board grid
     * @param save
     */
    private void loadGrid(boolean save) {

        if (!save)
            MapGrid = new MapSquare[20][20];
        else
            gridPane.getChildren().clear();

        //calculate the percentage size of a row and a column
        RowConstraints rc = new RowConstraints();
        ColumnConstraints cc = new ColumnConstraints();
        rc.setPercentHeight(100d / 20);
        cc.setPercentWidth(100d / 20);

        if(!save) {
            gridPane.getColumnConstraints().set(0, cc);
            gridPane.getRowConstraints().set(0, rc);

            for (int x = 0; x < 19; x++) {
                gridPane.getColumnConstraints().add(cc);
            }
            for (int y = 0; y < 19; y++) {
                gridPane.getRowConstraints().add(rc);
            }
        }

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {

                MapSquare cell = save ? MapGrid[x][y] : new MapSquare();

                //Set id to coords
                cell.setId(x + " " + y);

                //Handle board coord clicked
                cell.setOnMouseClicked(this::OnBoardPressed);

                //colour the background tiles
                cell.getStyleClass().add(x % 2 != y % 2 ? "backgroundTile" : "backgroundTileAlt");

                MapGrid[x][y] = cell;

                //add the stack pane, from the public array to the grid.
                gridPane.add(MapGrid[x][y], x, y, 1, 1);
            }
        }
    }

    /**
     * Lets the player interact with the board
     * @param event
     */
    public void OnBoardPressed(MouseEvent event) {
        String id = ((StackPane)event.getSource()).getId();
        int[] coords = Arrays.stream(id.split(" ")).mapToInt(Integer::valueOf).toArray(); //Split and convert array to integers

        playerText.setText(Arrays.toString(transformCoordinates(coords[1], coords[0])));

        Ship owner = game.getCurrentPlayer();
        if(!move_scene.isVisible() || !game.move(coords, MapGrid))
            return;

        //Handle Treasure Island Chance Cards
        ChanceCardBase card = game.chanceCard(MapGrid, coords, chanceCardDeck);
        if (card != null) {
            playerText.setText("Chance card\n" + card.getName());
            chanceCardDeck.remove(card);

            //Determine whether card is store-able or not
            if (card.execute(MapGrid, owner, crewCardDeck, islands, chanceCardDeck)) {
                owner.addCommodity((ICommodity) card);
            } else {
                chanceCardDeck.add(card);
            }
        }
        //Handle Chance Card Triggers for held cards
        Arrays.asList(owner.getChanceCards()).forEach(c -> c.execute(MapGrid, owner, crewCardDeck, islands, chanceCardDeck));

        //Trade UI
        Island island;
        if ((island = game.isIslandDock(MapGrid, coords)) != null && island.isTradingPost()) {
            playerText.setText(island.getName());
            //Handle transfer of treasure if owned by player
            Ship islandOwner = island.getOwner();
            if (islandOwner != null && owner.getPlayerName().equals(islandOwner.getPlayerName())) {
                for (Treasure t : owner.getTreasure()) {
                    owner.removeCommodity(t);
                    island.addCommodity(t);
                }
            }
            //Handle secure storage of treasure
            for (Treasure t : island.getTreasure()) {
                List<Treasure> treasure = Arrays.asList(island.getTreasure());
                if (treasure.stream().filter(c -> c.getName().equals(t.getName())).count() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        island.removeCommodity(treasure.get(i));
                        island.addSecureStorage(treasure.get(i));
                    }
                }
            }
            updateInventory(island);
        }

        //Handle fight scenario
        Ship[] ships = MapGrid[coords[0]][coords[1]].getShips();
        if (ships.length > 1 && card == null) {
            Ship winner = game.fight(MapGrid, coords, move_scene);
            if (winner != null)
                playerText.setText("Battle!\n\n" + ships[0].getPlayerName() + " VS " + ships[1].getPlayerName() + "\n\n" + winner.getPlayerName() + " has won!");
            currentPlayer.setText(game.getCurrentPlayer().getPlayerName());
            currentPlayer.getStyleClass().clear();
            currentPlayer.getStyleClass().add("bg"+game.getCurrentPlayer().getIndex());
        }
        updateInventory(game.getCurrentPlayer());

        //Win Condition
        Ship winner;
        if ((winner = game.hasWon(MapGrid)) != null) {
            playerText.setText(winner.getPlayerName()+" has won the game!");
            new File("save.dat").delete();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            controller.setWinner(winner.getPlayerName());
            stage.setScene(win);
        }
    }

    /**
     *Changes direction of the ships
     * @param event
     */
    @FXML
    void OnCompassPress(MouseEvent event) {
        game.direction(((Label)event.getSource()).getText(), MapGrid);
    }

    /**
     * Handles the end of a players turn
     * @param event
     */
    @FXML
    void OnTurnEnded(MouseEvent event) {
        game.endTurn(MapGrid, move_scene);

        Ship current = game.getCurrentPlayer();
        currentPlayer.setText(current.getPlayerName());
        currentPlayer.getStyleClass().clear();
        currentPlayer.getStyleClass().add("bg"+current.getIndex());

        islandText.setVisible(false);
        tradeButton.setVisible(false);
        shipTrade.clear();
        islandTrade.clear();

        updateInventory(current);
        inventory_scene2.getChildren().removeIf(i -> i instanceof ScrollPane);

        try {
            SaveData.save(new SaveData(MapGrid, game, islands, outOfBounds, crewCardDeck, chanceCardDeck, MapGrid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to display ship and island inventories on the UI
     * @param tradeItem
     */
    private void updateInventory(TradeBase tradeItem) {
        ArrayList<ICommodity> items = new ArrayList<>();
        items.addAll(Arrays.asList(tradeItem.getCrewCards()));
        items.addAll(Arrays.asList(tradeItem.getTreasure()));

        HBox hbox = new HBox();
        HBox hbox2 = new HBox();

        //Store whether item is ship or island
        if (tradeItem instanceof Ship) {
            hbox.setId("+");
            hbox2.setId("+");
            for (ChanceCardBase i : tradeItem.getChanceCards()) {
                items.add((ICommodity) i);
            }
            inventory_scene1.getChildren().removeIf(i -> i instanceof ScrollPane);
        } else {
            inventory_scene2.getChildren().removeIf(i -> i instanceof ScrollPane);
        }

        for (ICommodity c : items) {

            CommodityImageView imageView = new CommodityImageView(c, false);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setOnMouseClicked(this::tradeItemClicked);

            if (c instanceof Treasure)
                hbox.getChildren().add(imageView);
            else
                hbox2.getChildren().add(imageView);
        }

        if (tradeItem instanceof Island) {
            for (Treasure t : ((Island) tradeItem).getSecureStorage()) {
                CommodityImageView imageView = new CommodityImageView(t, true);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                hbox.getChildren().add(imageView);
            }
        }

        ScrollPane scroll1 = new ScrollPane(hbox);
        ScrollPane scroll2 = new ScrollPane(hbox2);
        scroll1.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        scroll2.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        scroll1.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        if (tradeItem instanceof Ship) {
            inventoryText.setText(((Ship) tradeItem).getPlayerName() + "'s items:");
            inventory_scene1.getChildren().add(1, scroll1);
            inventory_scene1.getChildren().add(2, scroll2);
        } else {
            islandText.setVisible(true);
            tradeButton.setVisible(true);
            islandText.setText(((Island)tradeItem).getName() + ":");
            inventory_scene2.getChildren().add(scroll1);
            inventory_scene2.getChildren().add(scroll2);
        }
    }

    /**
     * Indicated item has been selected for trade
     * @param mouseEvent
     */
    private void tradeItemClicked(MouseEvent mouseEvent) {
        CommodityImageView image = ((CommodityImageView)mouseEvent.getSource());
        String url = image.getId();
        ArrayList<ICommodity> trade = image.getParent().getId() != null ? shipTrade : islandTrade;

        if (inventory_scene2.getChildren().stream().noneMatch(i -> i instanceof ScrollPane))
            return;

        if (!url.endsWith("+")) {
            image.setId(image.getId()+"+");
            image.setOpacity(0.5);
            trade.add(image.getCommodity());
        } else {
            image.setId(url.substring(0, url.length()-1));
            image.setOpacity(1);
            trade.remove(image.getCommodity());
        }

        int islandValue = islandTrade.stream().mapToInt(ICommodity::getValue).sum();
        int shipValue = shipTrade.stream().mapToInt(ICommodity::getValue).sum();

        //Chance Card 21 trades for crew only
        if (shipTrade.stream().anyMatch(i -> i instanceof ChanceCard21) && islandTrade.stream().anyMatch(i -> i instanceof Treasure)) {
            islandValue -= islandTrade.stream().filter(i -> i instanceof CrewCard).mapToInt(ICommodity::getValue).sum() - 5;
        }

        int difference = shipValue - islandValue;
        tradeButton.setText("Trade: " + difference);
        if (difference >= 0)
            tradeButton.setStyle("-fx-background-color: green;");
        else
            tradeButton.setStyle("-fx-background-color: red;");
    }

    /**
     * Checks to see if it is possible to trade items
     * @param event
     */
    @FXML
    void tradeClicked(ActionEvent event) {
        Ship current = game.getCurrentPlayer();
        int[] coords = current.getCoords();
        Island island = MapGrid[coords[0]][coords[1]].getIsland();
        if (game.trade(islandTrade, shipTrade, chanceCardDeck, island)) {
            updateInventory(current);
            updateInventory(island);
            shipTrade.clear();
            islandTrade.clear();
        }
    }

    /**
     * Displays movement screen
     * @param event
     */
    @FXML
    void movePressed(ActionEvent event) {
        move_scene.setVisible(true);
        inventory_scene1.setVisible(false);
        inventory_scene2.setVisible(false);
        instructions_scene.setVisible(false);

        game.moveStarted(MapGrid, move_scene);
    }

    /**
     * Displays instruction screen
     * @param event
     */
    @FXML
    void inventoryPressed(ActionEvent event) {
        move_scene.setVisible(false);
        inventory_scene1.setVisible(true);
        inventory_scene2.setVisible(true);
        instructions_scene.setVisible(false);

        game.moveDone(MapGrid);
    }

    /**
     * Displays instruction screen
     * @param event
     */
    @FXML
    void instructionsPressed(ActionEvent event) {
        move_scene.setVisible(false);
        inventory_scene1.setVisible(false);
        inventory_scene2.setVisible(false);
        instructions_scene.setVisible(true);

        game.moveDone(MapGrid);
    }

    /**
     * @param win
     * @param controller
     */
    public void setWin(Scene win, WinController controller) {
        this.win = win;
        this.controller = controller;
    }
}
