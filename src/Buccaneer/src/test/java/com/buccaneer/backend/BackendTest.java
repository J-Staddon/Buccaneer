package com.buccaneer.backend;

import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.Island;
import com.buccaneer.models.Ship;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.Treasure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * Class containing backend tests
 * SE-F-074, SE-F-075 and SE-F-104 - SE-F-131 are all UI tests
 */

public class BackendTest {

    private MapSquare[][] MapGrid;
    private ArrayList<Ship> ships;
    private ArrayList<Island> islands;
    private ArrayList<CrewCard> crewCardDeck;
    private ArrayList<ChanceCardBase> chanceCardDeck;

    public BackendTest() {
        ReloadMap();
    }


    private void ReloadMap() {
        MapGrid = new MapSquare[20][20];
        for (int i = 0; i < 20; i++)
            for (int e = 0; e < 20; e++)
                MapGrid[e][i] = new MapSquare();

        ObjectLoader obj = new ObjectLoader();
        obj.loadMap(new String[]{"Jay", "Ali", "Eric", "Thomas"}, MapGrid);
        crewCardDeck = obj.getCrewCardDeck();
        chanceCardDeck = obj.getChanceCardDeck();
        islands = obj.getIslands();
        ships = obj.getShips();
    }

    private ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Jay");
        names.add("Ali");
        names.add("Eric");
        names.add("Thomas");
        return names;
    }

    // transforms coordinates from the indexes in the grid
    // to actual positions on the board
    private int[] transformCoordinatesBoardToGrid(int a, int b) {
        int[] coords = new int[2];
        coords[0] = b - 1;
        coords[1] = MapGrid.length - a;

        return coords;
    }



    private ArrayList<Treasure> getTreasureInGame() {
        ArrayList<Treasure> treasures = new ArrayList<>();
        String prev = "";

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && !prev.equals(island.getName()) && island.isTradingPost()) {
                    prev = island.getName();
                    treasures.addAll(List.of(island.getTreasure()));
                }
            }
        }
        System.out.println(Arrays.toString(MapGrid[7][7].getIsland().getTreasure()));
        treasures.addAll(List.of(MapGrid[7][7].getIsland().getTreasure()));
        return treasures;
    }

    private ArrayList<CrewCard> cardStorage() {
        ArrayList<CrewCard> crewCards = new ArrayList<>(crewCardDeck);
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                Ship[] ship = MapGrid[e][i].getShips();
                if (island != null)
                    crewCards.addAll(Arrays.asList(island.getCrewCards()));
                if (ship.length > 0)
                    crewCards.addAll(Arrays.asList(ship[0].getCrewCards()));
            }
        }
        return crewCards;
    }

    /*
     * Returns an array of example treasure
     */

    Treasure[] populateTestTreasure(){
        Treasure[] treasures = new Treasure[4];

        for(int i = 1; i < 5; i++){
            treasures[i- 1] = (new Treasure("test treasure " + i, i));
        }
        return treasures;
    }

    /*
     * Returns an array of all possible Crew Cards
     */

    CrewCard[] populateTestCrewCards() {
        CrewCard[] crewCards = new CrewCard[6];

        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < 4; j++) {
                crewCards[j * (i + 1) - 1] = new CrewCard(j, false);
            }
        }
        return crewCards;
    }



    /**
     * SE-F-001
     * FR1
     * Checks if correct names of have been stored on the board.
     */
    @Test
    public void namesStored() {
        ArrayList<String> names = getNames();

        for (Island island : islands) {
            if (island != null && island.getOwner() != null) {
                Ship ship = island.getOwner();
                Assertions.assertTrue(names.contains(ship.getPlayerName()), "Ship has incorrect name.");
            }
        }
    }

    /*
     * SE-F-002
     * FR1
     * UI Test
     */

    /**
     * SE-F-003
     * FR2
     *
     * Test if the ports are correctly assigned to the players
     */
    @Test
    public void testPortAssignment() {
        ArrayList<String> names = getNames();

        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getOwner() != null) {
                    Ship ship = island.getOwner();
                    Assertions.assertTrue(names.contains(ship.getPlayerName()), "Player Name does not match port owner.");
                    names.remove(ship.getPlayerName());
                }
            }
        }
    }

    /**
     * SE-F-004
     * FR2
     *
     * Test if the ports are uniquely assigned to players
     */
    @Test
    public void testUniquePortAssignment() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Jay");
        names.add("Ali");
        names.add("Eric");
        names.add("Thomas");

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getOwner() != null) {
                    Ship ship = island.getOwner();
                    Assertions.assertTrue(names.contains(ship.getPlayerName()), "Player Name does not match port owner.");
                    names.remove(ship.getPlayerName());
                }
            }
        }
        Assertions.assertEquals(0, names.size(), "Names remain in array, they were not assigned to ports uniquely.");
    }




    /**
     * SE-F-005
     * FR3
     *
     * Check if the number of Crew Cards in the game is equal to 36
     */
    @Test
    public void testNumberOfCrewCardsInGame() {
        ReloadMap();
        ArrayList<CrewCard> crewCards = cardStorage();
        Assertions.assertEquals(36, crewCards.size(), "Board is not storing 36 crew cards.");
    }

    /**
     * SE-F-006
     * FR3
     *
     * Test if there are 2 cards of value 1, 2 and 3 in black and in red
     */
    @Test
    public void testCrewCardColorsAndValues() {
        ArrayList<CrewCard> crewCards = cardStorage();

        for (int i = 0; i < 2; i++)
            for (int e = 1; e < 4; e++) {
                int value = e;
                boolean red = i == 0;
                Assertions.assertEquals(6, crewCards.stream().filter(card ->
                                card.getValue() == value && card.isRed() == red).count(),
                        "There's not 6 cards, each of black and red with value 1-3");
            }
    }

    /**
     * SE-F-007
     * FR3
     *
     * Test if the order of crew cards in the deck is random
     */
    @Test
    public void testRandomStoreCrewCards() {
        ArrayList<CrewCard> crewCards = cardStorage();
        ReloadMap();
        ArrayList<CrewCard> crewCards2 = cardStorage();

        Assertions.assertNotEquals(crewCards, crewCards2, "Cards are not shuffled.");
    }

    /**
     * SE-F-008
     * FR3
     *
     * Test if card returned to deck is stored at bottom of the deck
     */
    @Test
    public void testReturnedCardStoredAtTheBottomOfTheDeck() {
        ArrayList<CrewCard> crewCards = cardStorage();
        CrewCard drawn = crewCards.remove(0);
        crewCards.add(drawn);

        Assertions.assertEquals(drawn, crewCards.get(crewCards.size() - 1), "Card isn't returned to bottom.");
    }

    /**
     * SE-F-009
     * FR3
     *
     * Test if card drawn is from the top of the deck
     */
    @Test
    public void testCardDrawnFromTheTopOfTheDeck() {
        ArrayList<CrewCard> crewCards = cardStorage();
        CrewCard topCard = crewCards.get(0);
        CrewCard drawn = crewCards.remove(0);

        Assertions.assertEquals(topCard, drawn, "Top card is not the drawn card.");
    }

    /**
     * SE-F-10
     * FR3
     *
     *Test if retrieved card is removed from the deck
     */
    @Test
    public void testCrewCardRemovedFromTheBottomOfTheDeck() {
        ArrayList<CrewCard> crewCards = cardStorage();
        CrewCard drawn = crewCards.remove(0);

        Assertions.assertFalse(crewCards.contains(drawn), "Deck contains removed card.");
    }

    /**
     * SE-F-011
     * FR4
     *
     * Test if there are 28 Chance Cards in the deck
     */
    @Test
    public void testNumberOfChanceCardsInGame() {
        Assertions.assertEquals(28, chanceCardDeck.size(), "Not 28 Chance Cards.");
    }

    /**
     * SE-F-012
     * FR4
     *
     * Test if the game sorts the cards randomly at the beginning of each game
     */
    @Test
    public void testRandomStoreChanceCards() {
        ArrayList<ChanceCardBase> chanceCards = chanceCardDeck;
        ReloadMap();
        ArrayList<ChanceCardBase> chanceCards2 = chanceCardDeck;

        Assertions.assertNotEquals(chanceCards, chanceCards2, "Cards are not shuffled.");
    }

    /**
     * SE-F-013
     * FR4
     *
     * Test if the chance cards are returned to the bottom of the deck
     */
    @Test
    public void chanceCardReturn() {
        ArrayList<ChanceCardBase> chanceCardBases = chanceCardDeck;
        ChanceCardBase drawn = chanceCardBases.remove(0);
        chanceCardBases.add(drawn);

        Assertions.assertEquals(drawn, chanceCardBases.get(chanceCardBases.size() - 1), "Card isn't returned to bottom.");
    }

    /**
     * SE-F-014
     * FR4
     *
     * Test if the game assigns the cards that are meant to be kept by the players
     */

    @Test
    public void chanceCardDrawn() {
        ArrayList<CrewCard> assignedCards = new ArrayList<>();
        ArrayList<Island> island = islands;
        ArrayList<Ship> players = ships;

        for (Ship ship : players)
            assignedCards.addAll(Arrays.asList(ship.getCrewCards()));
        for (Island is : island)
            assignedCards.addAll(Arrays.asList(is.getCrewCards()));

        int sum = 0;
        for(Ship p: players){
            if(p.getCrewCards().length > 0){
                sum += p.getCrewCards().length;
            }
        }
        Assertions.assertEquals(20, sum, "The number of cards kept by the players is incorrect");
    }

    /*
     * SE-F-015
     *
     * has been implemented as an UI test
     */

    /*
     * SE-F-016
     * has been implemented as an UI test
     */


    /*
     * Tests 017 - 0.20 have been removed
     * The ordering has been maintained
     */

    /**
     * SE-F-021
     * FR5
     *
     * Test if the number of diamonds in the game is equal to 4
     */
    @Test
    public void testNumberOfDiamondsInTheGame() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        long num = treasures.stream().filter(i -> i.getName().equals("Diamonds")).count();
        Assertions.assertEquals(4, num, "There is not 4 diamonds.");
    }

    /**
     * SE-F-022
     * FR5
     *
     * Test if the number of rubies in the game is equal to 4
     */
    @Test
    public void testNumberOfRubiesInTheGame() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        long num = treasures.stream().filter(i -> i.getName().equals("Rubies")).count();
        Assertions.assertEquals(4, num, "There is not 4 rubies.");
    }

    /**
     * SE-F-023
     * FR5
     *
     * Test if the gold of diamonds in the game is equal to 4
     */
    @Test
    public void testNumberOfGoldInTheGame() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        long num = treasures.stream().filter(i -> i.getName().equals("Gold")).count();
        Assertions.assertEquals(4, num, "There is not 4 gold.");
    }

    /**
     * SE-F-024
     * FR5
     *
     * Test if the number of pearls in the game is equal to 4
     */
    @Test
    public void testNumberOfPearlsInTheGame() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        long num = treasures.stream().filter(i -> i.getName().equals("Pearls")).count();
        Assertions.assertEquals(4, num, "There is not 4 pearls.");
    }

    /**
     * SE-F-025
     * FR5
     *
     * Test if the number of barrels of rum in the game is equal to 4
     */
    @Test
    public void testNumberOfBarrelsOfRumInTheGame() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        long num = treasures.stream().filter(i -> i.getName().equals("Barrels of Rum")).count();
        Assertions.assertEquals(4, num, "There is not 4 barrels of rum.");
    }


    /*
     * SE-F-026
     * FR5
     *
     * This test has been removed
     * The ordering has been maintained
     */


    /**
     * SE-F-027
     * FR5
     *
     * Test if the value of Diamond is correct
     */
    @Test
    public void testDiamondValue() {

        ArrayList<Treasure> treasures = getTreasureInGame();
        for (Treasure t : treasures) {
            if (t.getName().equals("Diamond")) {
                Assertions.assertEquals(5, t.getValue(), "The value of a diamond is not 5.");
            }
        }

    }

    /**
     * SE-F-028
     * FR5
     *
     * Test if the value of Ruby is correct
     */
    @Test
    public void testRubyValue() {

        ArrayList<Treasure> treasures = getTreasureInGame();
        for (Treasure t : treasures) {
            if (t.getName().equals("Ruby")) {
                Assertions.assertEquals(5, t.getValue(), "The value of a ruby is not 5.");
            }
        }

    }

    /**
     * SE-F-029
     * FR5
     *
     * Test if the value of Gold Bar is correct
     */
    @Test
    public void testGoldBarValue() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        for (Treasure t : treasures) {
            if (t.getName().equals("Gold bar")) {
                Assertions.assertEquals(5, t.getValue(), "The value of a gold bar is not 4.");
            }
        }
    }

    /**
     * SE-F-030
     * FR5
     *
     * Test if the value of Pearl is correct
     */
    @Test
    public void testPearlValue() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        for (Treasure t : treasures) {
            if (t.getName().equals("Pearl")) {
                Assertions.assertEquals(3, t.getValue(), "The value of a pearl is not 3.");
            }
        }
    }

    /**
     * SE-F-031
     * FR5
     *
     * Test if the value of Barrel of Rum is correct
     */
    @Test
    public void testBarrelOfRumValue() {
        ArrayList<Treasure> treasures = getTreasureInGame();
        for (Treasure t : treasures) {
            if (t.getName().equals("Barrel of Rum")) {
                Assertions.assertEquals(2, t.getValue(), "The value of a barrel of rum is not 2.");
            }
        }
    }

    /**
     * SE-F-032
     * FR5
     *
     * Test if the Crew Card pointing system works as intended
     */
    @Test
    public void testOnePointCrewCardValue() {
        Ship testShip = new Ship("Test ship", 0);
        testShip.addCommodity(new CrewCard(1, true));
        Assertions.assertEquals(1, testShip.attackPower(), "The value of the crew cards is not stored correctly");
    }

    /**
     * SE-F-033
     * FR6
     *
     * Test if the Crew Card pointing system works as intended
     */
    @Test
    public void testTwoPointsCrewCardValue() {
        Ship testShip = new Ship("Test ship", 0);
        testShip.addCommodity(new CrewCard(2, true));
        Assertions.assertEquals(2, testShip.attackPower(), "The value of the crew cards is not stored correctly");
    }


    /**
     * SE-F-034
     * FR6
     *
     * Test if the Crew Card pointing system works as intended
     */
    @Test
    public void testThreePointsCrewCardValue() {
        Ship testShip = new Ship("Test ship", 0);
        testShip.addCommodity(new CrewCard(3, true));
        Assertions.assertEquals(3, testShip.attackPower(), "The value of the crew cards is not stored correctly");
    }


    /**
     * SE-F-035
     * FR6
     *
     * Test if the Combat Value is calculated correctly
     */
    @Test
    public void testCombatValue() {
        Ship testShip = new Ship("Test ship", 0);
        testShip.addCommodity(new CrewCard(3, false));
        testShip.addCommodity(new CrewCard(1, true));
        Assertions.assertEquals(2, testShip.attackPower(), "The value of the attack power is not calculated correctly");
    }


    /**
     * SE-F-036
     * FR7
     *
     * Test that the number of card in Port London is stored correctly
     */
    @Test
    public void testNumberOfCardsInPortLondon() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getName().equals("Port of London")) {
                    Ship testShip = island.getOwner();

                    CrewCard[] toBeRemoved = testShip.getCrewCards();
                    for(CrewCard c : toBeRemoved) testShip.removeCommodity(c);

                    for(int k = 0; k< 5; k++) testShip.addCommodity(crewCards[k]);

                    Assertions.assertEquals(5, testShip.getCrewCards().length, "The number of crew card stored in Port London is incorrect");
                }
            }
        }
    }

    /**
     * SE-F-037
     * FR7
     *
     * Test that the number of card in Port Marseilles is stored correctly
     */
    @Test
    public void testNumberOfCardsInPortMarseilles() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Marseilles")) {

                    for(int k = 0; k < 6; k++){
                        island.addCommodity(crewCards[i]);
                    }
                    Assertions.assertEquals(6, island.getCrewCards().length, "The number of crew card stored in Port Marseilles is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-038
     * FR7
     *
     * Test that the number of card in Port Cadiz is stored correctly
     */
    @Test
    public void testNumberOfCardsInPortCadiz() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Cadiz")) {
                    for(int k = 0; k < 3; k++){
                        island.addCommodity(crewCards[k]);
                    }
                    Assertions.assertEquals(3, island.getCrewCards().length, "The number of crew card stored in Port Cadiz is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-039
     * FR7
     *
     * Test that the number of card in Port Genoa is stored correctly
     */
    @Test
    public void testNumberOfCardsInPortGenoa() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Genoa")) {
                    island.addCommodity(new CrewCard(1, false));
                    Assertions.assertEquals(1, island.getCrewCards().length, "The number of crew card stored in Port Genoa is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-040
     * FR7
     *
     * Test that the value of card in Port London is stored correctly
     */
    @Test
    public void testValueOfCardsInPortLondon() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of London")) {
                    island.addCommodity(new CrewCard(2, false));
                    island.addCommodity(new CrewCard(2, false));
                    island.addCommodity(new CrewCard(2, true));

                    int sum = 0;
                    for (CrewCard c : island.getCrewCards()) {
                        sum += c.getValue();
                    }

                    Assertions.assertEquals(6, sum, "The number of crew card stored in Port Genoa is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-041
     * FR7
     *
     * Test that the value of card in Port London is stored correctly after removing some of them
     */
    @Test

    public void testValueOfCardsInPortLondonAfterRemoving() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of London")) {
                    CrewCard redTwo = new CrewCard(2, true);
                    CrewCard blackTwo = new CrewCard(2, false);

                    island.addCommodity(redTwo);
                    island.addCommodity(redTwo);
                    island.addCommodity(blackTwo);

                    island.removeCommodity(blackTwo);
                    int sum = 0;
                    CrewCard[] crewCards = island.getCrewCards();
                    for (CrewCard c: crewCards) {
                        sum += c.getValue();
                    }

                    Assertions.assertEquals(4, sum, "The value of crew card stored in Port London after removing is incorrect");
                }
            }
        }
    }



    /**
     * SE-F-042
     * FR7
     *
     * Test that the number of treasure in Port London is stored correctly
     */
    @Test
    public void testNumberOfTreasureStoredInLondon() {
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getName().equals("Port of London")) {
                    island.addCommodity(new Treasure("test treasure", 1));
                    Assertions.assertEquals(1, island.getTreasure().length, "The number of treasure stored in London is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-043
     * FR7
     *
     *  Test that the number of treasure in Port Marseilles is stored correctly
     */
    @Test
    public void testNumberOfTreasureStoredInMarseilles() {
        Treasure[] treasure = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Marseilles")) {
                    for(int k = 0; k < 2; k++){
                        island.addCommodity(treasure[k]);
                    }
                    Assertions.assertEquals(2, island.getTreasure().length, "The number of treasure stored in Marseilles     is incorrect");
                }
            }
        }
    }

    /**
     * SE-F-044
     * FR7
     *
     *  Test that the number of treasure in Port Cadiz is stored correctly
     */
    @Test
    public void testNumberOfTreasureStoredInCadiz() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Cadiz")) {
                    island.addCommodity(new Treasure("test treasure", 1));
                    island.addCommodity(new Treasure("test treasure 2", 2));
                    island.addCommodity(new Treasure("test treasure 3", 3));

                    Assertions.assertEquals(3, island.getTreasure().length, "The number of treasure stored in Cadiz is incorrect");
                }
            }
        }
    }

    /**
     * SE-F-045
     * FR7
     *
     *  Test that the number of treasure in Genoa London is stored correctly
     */
    @Test
    public void testNumberOfTreasureStoredInGenoa() {
        Treasure[] treasures = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Genoa")) {

                    for(int k = 0 ; k < 4; k++)island.addCommodity(treasures[k]);

                    Assertions.assertEquals(4, island.getTreasure().length, "The number of treasure stored in Genoa is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-046
     * FR7
     *
     *  Test that the number of treasure in Port London is stored correctly after removing some of it
     */
    @Test
    public void testNumberOfTreasureStoredInLondonAfterRemoving() {
        Treasure[] treasures = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of London")) {
                    for (int k = 0; k < 1; k++) island.addCommodity(treasures[k]);

                    for (int l = 0; l < 1; l++) island.removeCommodity(treasures[l]);

                    Assertions.assertEquals(0, island.getTreasure().length, "The number of treasure stored in London is incorrect");
                }
            }
        }
    }


    /**
     * SE-F-047
     * FR7
     *
     *  Test that the number of treasure in Port Marseilles is stored correctly after removing some of it
     */
    @Test
    public void testNumberOfTreasureStoredInMarseillesAfterRemoving() {
        Treasure[] treasures = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Marseilles")) {

                    for (int k = 0; k < 2; k++) island.addCommodity(treasures[k]);

                    for (int l = 0; l < 1; l++) island.removeCommodity(treasures[l]);

                    Assertions.assertEquals(1, island.getTreasure().length, "The number of treasure stored in Marseilles is incorrect");
                }
            }
        }
    }

    /**
     * SE-F-048
     * FR7
     *
     * Test that the number of treasure in Port Cadiz is stored correctly after removing some of it
     */
    @Test
    public void testNumberOfTreasureStoredInCadizAfterRemoving() {
        Treasure[] treasures = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Cadiz")) {

                    for (int k = 0; k < 3; k++) island.addCommodity(treasures[k]);

                    for (int l = 0; l < 2; l++) island.removeCommodity(treasures[l]);

                    Assertions.assertEquals(1, island.getTreasure().length, "The number of treasure stored in Cadiz is incorrect");
                }
            }
        }
    }

    /**
     * SE-F-049
     * FR7
     *
     * Test that the number of treasure in Port Genoa is stored correctly after removing some of it
     */
    @Test
    public void testNumberOfTreasureStoredInGenoaAfterRemoving() {
        Treasure[] treasures = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Port of Genoa")) {

                    for (int k = 0; k < 4; k++) island.addCommodity(treasures[k]);

                    for (int l = 0; l < 4; l++) island.removeCommodity(treasures[l]);

                    Assertions.assertEquals(0, island.getTreasure().length, "The number of treasure stored in Genoa is incorrect");
                }
            }
        }
    }

    /*
     * Tests 50 - 53 have been deleted
     * The ordering has been maintained
     */

    /**
     * SE-F-054
     * FR8
     *
     * Test if the game can correctly keep track of the nr of cards at Flat Island when cards are added
     **/
    @Test
    public void testAddingCardsToFlatIsland() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {

                    CrewCard[] toBeRemoved = island.getCrewCards();
                    for (CrewCard c : toBeRemoved) island.removeCommodity(c);

                    for(int k = 0; k < 6 ; k++)island.addCommodity(crewCards[k]);

                    Assertions.assertEquals(6, island.getCrewCards().length, "The number of crew card stored in Island is incorrect, Expecting: 10");
                }
            }
        }

    }

    /**
     * SE-F-055
     * FR8
     *
     *Test if the game can correctly keep track of the nr of cards at Flat Island when cards are removed
     **/
    @Test
    public void testRemovingCardsFromFlatIsland() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {

                    CrewCard[] toBeRemoved = island.getCrewCards();
                    for (CrewCard c : toBeRemoved) island.removeCommodity(c);

                    for(int k = 0; k < 6 ; k++)island.addCommodity(crewCards[k]);

                    for(int l = 0; l < 3 ; l++)island.removeCommodity(crewCards[l]);

                    Assertions.assertEquals(3, island.getCrewCards().length, "The number of crew card stored in Island is incorrect, Expecting: 7");
                }
            }
        }

    }

    /**
     * SE-F-056
     * FR8
     *
     * Test if the game can correctly keep track of the nr of cards at Flat Island when more cards are removed than available
     **/
    @Test
    public void testRemoveMoreTreasureThanAvailableFromFlatIsland() {
        CrewCard[] crewCards = populateTestCrewCards();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {

                    CrewCard[] toBeRemoved = island.getCrewCards();

                    for (CrewCard c : toBeRemoved) island.removeCommodity(c);

                    for(int k = 0; k < 5 ; k++)island.addCommodity(crewCards[k]);

                    for(int l = 0; l < 6 ; l++)island.removeCommodity(crewCards[l]);

                    Assertions.assertEquals(0, island.getCrewCards().length, "The number of crew card stored at Flat Island is incorrect");
                }
            }
        }

    }

    /**
     * SE-F-057
     * FR8
     *
     * Test if the game can correctly keep track of the nr of treasure at Flat Island when treasure is added
     **/
    @Test
    public void testAddingTreasureToFlatIsland() {
        Treasure[] testTreasure = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {
                    for (int k = 0; k < 4; k++) {
                        island.addCommodity(testTreasure[k]);
                    }

                    Assertions.assertEquals(4, island.getTreasure().length, "The number of treasures stored are incorrect");

                }
            }
        }
    }

    /**
     * SE-F-058
     * FR8
     *
     * Test if the game can correctly keep track of the nr of treasure at Flat Island when treasure is removed
     **/
    @Test
    public void testRemovingTreasureFromFlatIsland() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {
                    Treasure diamond = new Treasure("Diamond", 5);

                    Treasure[] toBeRemoved = island.getTreasure();
                    for(Treasure t : toBeRemoved) island.removeCommodity(t);

                    for (int k = 0; k < 10; k++) island.addCommodity(diamond);

                    for (int l = 0; l < 3; l++) island.removeCommodity(diamond);

                    Assertions.assertEquals(7, island.getTreasure().length, "The number of treasures stored at Flat Island is incorrect");
                }
            }
        }

    }

    /**
     * SE-FE-59
     * FR8
     *
     * Test if the game can correctly keep track of the nr of treasure at Flat Island when more treasure is removed than available
     **/
    @Test
    public void testRemoveMoreCardsThanAvailableFromFlatIsland() {
        Treasure[] testTreasure = populateTestTreasure();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Island island = MapGrid[j][i].getIsland();
                if (island != null && island.getName().equals("Flat Island")) {
                    CrewCard[] toBeRemoved = island.getCrewCards();
                    for(CrewCard c : toBeRemoved) island.removeCommodity(c);


                    Treasure diamond = new Treasure("Diamond", 5);

                    for (int k = 0; k < 5; k++) island.addCommodity(diamond);

                    for (int l = 0; l < 6; l++) island.removeCommodity(diamond);

                    Assertions.assertEquals(0, island.getTreasure().length, "The number of treasures stored are incorrect");
                }
            }
        }

    }


    /**
     * SE-F-060
     * FR9
     *
     * Test if port London is at the correct coordinates
     */
    @Test
    public void testLondonCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(1, 14);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Port of London")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Port of London not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }

    /**
     * SE-F-061
     * FR9
     *
     * Test if port Cadiz is at the correct coordinates
     */
    @Test
    public void testCadizCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(14, 20);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Port of Cadiz")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Port of Cadiz not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }

    /**
     * SE-F-062
     * FR9
     *
     * Test if port Marseilles is at the correct coordinates
     */
    @Test
    public void testMarseillesCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(20, 7);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Port of Marseilles")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Port of Marseilles not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }

    /**
     * SE-F-063
     * FR9
     *
     * Test if port Genoa is at the correct coordinates
     */
    @Test
    public void testGenoaCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(7, 1);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Port of Genoa")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Port of Genoa not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }


    /**
     * SE-F-064
     * FR9
     *
     * Test if Mud Bay is at the correct coordinates
     */
    @Test
    public void testMudBayCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(1, 1);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Mud Bay")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Mud Bay is not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }

    /**
     * SE-F-065
     * FR9
     *
     * Test if Anchor Bay is at the correct coordinates
     */
    @Test
    public void testAnchorBayCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(20, 1);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Anchor Bay")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Anchor Bay is not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }


    /**
     * SE-F-065
     * FR9
     *
     * Test if Anchor Bay is at the correct coordinates
     */
    @Test
    public void testCliffCreekCoordinates() {
        int[] expectedCoords = transformCoordinatesBoardToGrid(20, 20);
        boolean islandFound = false;

        for(int i = 0; i < MapGrid.length; i++){
            for(int j = 0; j < MapGrid[0].length; j++){
                Island island = MapGrid[i][j].getIsland();
                if(island != null && island.getName().equals("Cliff Creek")) {
                    Assertions.assertArrayEquals(expectedCoords, island.getGroundCoords(), "Cliff Creek is not in correct grid space");
                    islandFound = true;
                }
            }
        }
        if(!islandFound)fail("Island not found");
    }



    /*
     * Test SE-F-067 to SE-F-069
     * Have been implemented as UI tests
     */


    /**
     * SE-F-070
     * FR10
     *
     * Test if each player is dealt 5 Crew Cards from the pack
     **/
    @Test
    public void testAllPlayersDealtFiveCrewCardsAtStart() {
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getOwner() != null) {
                    Ship ship = island.getOwner();
                    Assertions.assertEquals(5, ship.getCrewCards().length, "Amount of crew cards deal not correct.");
                }
            }
        }

    }



    /**
     * SE-F-071
     * FR10
     *
     * Test if all the Trading Ports are dealt 2 crew cards
     **/
    @Test
    public void testAllTradingPortsDealtTwoCrewCardsAtStart() {
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getOwner() == null && island.isTradingPost()) {
                    Assertions.assertEquals(2, island.getCrewCards().length, "Amount of cew cards dealt not correct.");
                }
            }
        }

    }

    /**
     * SE-F-072
     * FR10
     *
     * Test if for all Trading Ports the sum of crew cards and treasure is equal to 8 at the beginning of the game
     **/
    @Test
    public void testSumOfCrewCardAndTreasureEqualsEightAtStart() {

        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.getOwner() == null && island.isTradingPost()) {
                    CrewCard[] crewCardArray = island.getCrewCards();
                    Treasure[] treasureArray = island.getTreasure();
                    int total = 0;
                    for (int l = 0; l < 2; l++) {
                        total += crewCardArray[l].getValue();
                    }
                    total += treasureArray[0].getValue();
                    Assertions.assertEquals(8, total, "Sum of commodities not correct.");
                }
            }
        }

    }
    /**
     * SE-F-73
     * FR10
     *
     * Already implemented in SE-F-004
     */

    /**
     * SE-F-74
     * FR10
     *
     * Test if the start position for each ship is their Home Port
     * **/
    @Test
    public void testShipAndIslandCoordsMatchAtTheStart() {
        for (int i = 0; i < 20; i++) {
            for (int e = 0; e < 20; e++) {
                Island island = MapGrid[e][i].getIsland();
                if (island != null && island.isTradingPost() && island.getOwner() != null) {
                    Ship ship = island.getOwner();
                    Assertions.assertArrayEquals(ship.getCoords(), island.getDockCoords()[0], "Ship and dock coords do not match");
                }
            }
        }
    }

    /*
     * Tests 77 - 93 have been implemented as UI tests
     */

    /**
     * SE-F-94
     * FR11
     *  Has already been implemented in SE-F-36
     * **/

    /**
     * SE-F-98
     * FR12
     *
     * Test if when the loser doesn’t have treasure and has only one crew card it is given to the winner
     **/


    @Test
    void testWinnerTakesTreasureFromLoser() {
        Treasure[] treasures = populateTestTreasure();
        CrewCard[] crewCards = populateTestCrewCards();
        Ship player1 = new Ship();
        Ship player2 = new Ship();

        for (int i = 0; i < 2; i++) player1.addCommodity(treasures[i]);

        for (int i = 0; i < 3; i++) player1.addCommodity(crewCards[i]);
    }

    /*
     *  Test 98 - 142 have been implemented as UI tests
     */
}



