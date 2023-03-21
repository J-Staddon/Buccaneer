package com.buccaneer.backend.cards;

import com.buccaneer.backend.MapSquare;
import com.buccaneer.backend.cards.chance.*;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChanceCard1.class, name = "chancecard1"),
        @JsonSubTypes.Type(value = ChanceCard2.class, name = "chancecard2"),
        @JsonSubTypes.Type(value = ChanceCard3.class, name = "chancecard3"),
        @JsonSubTypes.Type(value = ChanceCard4.class, name = "chancecard4"),
        @JsonSubTypes.Type(value = ChanceCard5.class, name = "chancecard5"),
        @JsonSubTypes.Type(value = ChanceCard6.class, name = "chancecard6"),
        @JsonSubTypes.Type(value = ChanceCard7.class, name = "chancecard7"),
        @JsonSubTypes.Type(value = ChanceCard8.class, name = "chancecard8"),
        @JsonSubTypes.Type(value = ChanceCard9.class, name = "chancecard9"),
        @JsonSubTypes.Type(value = ChanceCard10.class, name = "chancecard10"),
        @JsonSubTypes.Type(value = ChanceCard11.class, name = "chancecard11"),
        @JsonSubTypes.Type(value = ChanceCard12.class, name = "chancecard12"),
        @JsonSubTypes.Type(value = ChanceCard13.class, name = "chancecard13"),
        @JsonSubTypes.Type(value = ChanceCard14.class, name = "chancecard14"),
        @JsonSubTypes.Type(value = ChanceCard15.class, name = "chancecard15"),
        @JsonSubTypes.Type(value = ChanceCard16.class, name = "chancecard16"),
        @JsonSubTypes.Type(value = ChanceCard17.class, name = "chancecard17"),
        @JsonSubTypes.Type(value = ChanceCard18.class, name = "chancecard18"),
        @JsonSubTypes.Type(value = ChanceCard19.class, name = "chancecard19"),
        @JsonSubTypes.Type(value = ChanceCard20.class, name = "chancecard20"),
        @JsonSubTypes.Type(value = ChanceCard21.class, name = "chancecard21"),
        @JsonSubTypes.Type(value = ChanceCard22.class, name = "chancecard22"),
        @JsonSubTypes.Type(value = ChanceCard23.class, name = "chancecard23"),
        @JsonSubTypes.Type(value = ChanceCard24.class, name = "chancecard24"),
        @JsonSubTypes.Type(value = ChanceCard25.class, name = "chancecard25"),
        @JsonSubTypes.Type(value = ChanceCard26.class, name = "chancecard26"),
        @JsonSubTypes.Type(value = ChanceCard27.class, name = "chancecard27"),
        @JsonSubTypes.Type(value = ChanceCard28.class, name = "chancecard28")
})
public abstract class ChanceCardBase implements IChanceCard {

    protected String name;
    protected String description;

    private ArrayList<Treasure> treasures;

    //C1
    protected void takeCrew(MapSquare[][] MapGrid, Ship ship, ArrayList<CrewCard> crewCardDeck, int limit, boolean player) {
        ArrayList<Ship> ships = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            for (int e = 0; e < 20; e++)
                ships.addAll(Arrays.asList(MapGrid[i][e].getShips()));
        ships.removeIf(i -> i.getPlayerName().equals(ship.getPlayerName()));

        Dialog<String> dialog = new Dialog<>();
        if (player) {
            VBox vbox = new VBox();
            vbox.getChildren().add(new Label("Select Ship to take " + limit + " Crew Cards from."));
            for (Ship s : ships) {
                Button b = new Button(s.getPlayerName());
                b.setOnAction(i -> takeCrewAction(dialog, ship, s, crewCardDeck, limit, true));
                vbox.getChildren().add(b);
            }
            dialog.getDialogPane().setContent(vbox);
            dialog.show();
        } else {
            takeCrewAction(dialog, ship, null, crewCardDeck, limit, false);
        }
    }

    private void takeCrewAction(Dialog<String> dialog, Ship winner, Ship loser, ArrayList<CrewCard> crewCards, int limit, boolean player) {
        int count = limit;

        if (player) {
            CrewCard[] crewCards1 = loser.getCrewCards();
            for (CrewCard c : crewCards1) {
                if (count == 0)
                    break;
                loser.removeCommodity(c);
                winner.addCommodity(c);
                count--;
            }
        }

        ArrayList<CrewCard> toRemove = new ArrayList<>();
        for (CrewCard c : crewCards) {
            if (count == 0)
                break;
            toRemove.add(c);
            winner.addCommodity(c);
            count--;
        }
        crewCards.removeAll(toRemove);

        closeDialog(dialog);
    }

    //C2

    /**
     * Gives the player the option to pick up treasure or a crew card
     * @param MapGrid
     * @param ship
     * @param crewCards
     * @param crewLimit
     * @param treasureLimit
     */
    protected void takeTreasureOrCard(MapSquare[][] MapGrid, Ship ship, ArrayList<CrewCard> crewCards, int crewLimit, int treasureLimit) {
        Island treasureIsland = MapGrid[7][7].getIsland();
        String message = "There is no treasure available, pick crew cards?";
        ArrayList<String> choices = new ArrayList<>();
        choices.add("Crew Cards");


        if (treasureIsland.getTreasure().length > 0) {
            choices.add("Treasure");
            message = "Would you like treasure or crew cards?";
        }

        Dialog<String> dialog = new Dialog<>();
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label(message));
        for (String s : choices) {
            Button b = new Button(s);
            b.setOnAction(i -> {
                closeDialog(dialog);
                switch (b.getText()) {
                    case "Crew Cards" -> takeCrew(MapGrid, ship, crewCards, crewLimit, false);
                    case "Treasure" -> takeTreasure(ship, treasureIsland, treasureLimit);
                }
            });
            vbox.getChildren().add(b);
        }
        dialog.getDialogPane().setContent(vbox);
        dialog.show();
    }

    /**
     * Lets players pick up treasure
     * Creates box for the player to select from
     * @param ship
     * @param treasureIsland
     * @param treasureLimit
     */
    public void takeTreasure(Ship ship, Island treasureIsland, int treasureLimit) {
        treasures = new ArrayList<>();

        Dialog<String> dialog = new Dialog<>();
        VBox vbox = new VBox();
        Label totalCount = new Label("Value to take: " + treasureLimit);
        vbox.getChildren().add(totalCount);

        List<Treasure> sortedTreasure = Arrays.stream(treasureIsland.getTreasure()).sorted(Comparator.comparing(Treasure::getName)).toList();
        String prev = sortedTreasure.get(0).getName();
        HBox hbox = new HBox();
        int count = 0;
        for (Treasure t : sortedTreasure) {
            if (!t.getName().equals(prev) || t.equals(sortedTreasure.get(sortedTreasure.size() - 1))) {
                if (t.equals(sortedTreasure.get(sortedTreasure.size() - 1)))
                    count++;

                final int finalCount = count;
                Label name = new Label(prev+": ");
                Label num = new Label(Integer.toString(count));
                num.setId(prev);

                Button remove = new Button("-");
                remove.setOnAction(i -> takeTreasureAction(treasureIsland, num, finalCount, totalCount, treasureLimit, false));

                Button add = new Button("+");
                add.setOnAction(i -> takeTreasureAction(treasureIsland, num, finalCount, totalCount, treasureLimit, true));

                hbox.getChildren().add(remove);
                hbox.getChildren().add(add);
                hbox.getChildren().add(name);
                hbox.getChildren().add(num);
                vbox.getChildren().add(hbox);

                prev = t.getName();
                hbox = new HBox();
                count = 0;
            }
            count++;
        }
        Label currentTreasure;
        if (ship.getTreasure().length != 2){
            currentTreasure = new Label("You can carry " + (2 - ship.getTreasure().length) + " more item(s) of treasure");
        }
        else{
            currentTreasure = new Label("You can not carry anymore treasure");
        }
        vbox.getChildren().add(currentTreasure);
        Button b = new Button("Confirm");
        b.setOnAction(i -> {
            int shipCount = ship.getTreasure().length;
            if (treasures.stream().mapToInt(Treasure::getValue).sum() <= treasureLimit && shipCount + treasures.size() <= 2) {
                for (Treasure t : treasures) {
                    treasureIsland.removeCommodity(t);
                    ship.addCommodity(t);
                }
                closeDialog(dialog);
            }
        });
        vbox.getChildren().add(b);
        dialog.getDialogPane().setContent(vbox);
        dialog.show();
    }

    /**
     * Lets players pick up treasure
     * @param treasureIsland
     * @param num
     * @param finalCount
     * @param totalCount
     * @param treasureLimit
     * @param addOrMinus
     */
    private void takeTreasureAction(Island treasureIsland, Label num, int finalCount, Label totalCount, int treasureLimit, boolean addOrMinus) {
        int number = Integer.parseInt(num.getText());

        if (addOrMinus && number == 0) {
            return;
        } else if (!addOrMinus && number == finalCount) {
            return;
        }

        if (addOrMinus) {
            Treasure t = Arrays.stream(treasureIsland.getTreasure()).filter(i -> i.getName().equals(num.getId())).findFirst().get();
            treasureIsland.removeCommodity(t);
            treasures.add(t);
        } else {
            Treasure t = treasures.stream().filter(i -> i.getName().equals(num.getId())).findFirst().get();
            treasures.remove(t);
            treasureIsland.addCommodity(t);
        }

        num.setText(Integer.toString(addOrMinus ? number - 1 : number + 1));
        totalCount.setText("Value to take: " + (treasureLimit - treasures.stream().mapToInt(Treasure::getValue).sum()));
    }

    /**
     * Closes the treasure slection menu
     * @param dialog
     */
    private void closeDialog(Dialog<String> dialog) {
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.close();
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
    public String getDescription() {
        return description;
    }
}
