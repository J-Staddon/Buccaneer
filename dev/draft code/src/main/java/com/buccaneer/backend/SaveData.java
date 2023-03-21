package com.buccaneer.backend;

import com.buccaneer.models.Island;
import com.buccaneer.backend.cards.ChanceCardBase;
import com.buccaneer.models.commodities.CrewCard;
import com.buccaneer.models.commodities.ICommodity;
import com.buccaneer.models.commodities.Treasure;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SaveData {
    private MapSquare[][] mapGrid;
    private GameLoop gameLoop;
    private ArrayList<Island> islands;
    private ArrayList<int[]> outOfBounds;
    private ArrayList<CrewCard> crewCardDeck;
    private ArrayList<ChanceCardBase> chanceCardDeck;
    private ArrayList<Treasure> treasureIsland;
    private ArrayList<Treasure> flatIsland;
    private ArrayList<Treasure> pirateIsland;
    private ArrayList<CrewCard> treasureCrew;
    private ArrayList<CrewCard> flatCrew;
    private ArrayList<CrewCard> pirateCrew;
    public SaveData() {}
    public SaveData(MapSquare[][] mapGrid, GameLoop gameLoop, ArrayList<Island> islands, ArrayList<int[]> outOfBounds, ArrayList<CrewCard> crewCardDeck, ArrayList<ChanceCardBase> chanceCardDeck, MapSquare[][] MapGrid) {
        this.mapGrid = mapGrid;
        this.gameLoop = gameLoop;
        this.islands = islands;
        this.outOfBounds = outOfBounds;
        this.crewCardDeck = crewCardDeck;
        this.chanceCardDeck = chanceCardDeck;

        this.treasureCrew = new ArrayList<>(Arrays.asList(MapGrid[7][7].getIsland().getCrewCards()));
        this.flatCrew = new ArrayList<>(Arrays.asList(MapGrid[15][14].getIsland().getCrewCards()));
        this.pirateCrew = new ArrayList<>(Arrays.asList(MapGrid[1][0].getIsland().getCrewCards()));
        this.treasureIsland = new ArrayList<>(Arrays.asList(MapGrid[7][7].getIsland().getTreasure()));
        this.flatIsland = new ArrayList<>(Arrays.asList(MapGrid[15][14].getIsland().getTreasure()));
        this.pirateIsland = new ArrayList<>(Arrays.asList(MapGrid[1][0].getIsland().getTreasure()));
    }

    /**
     * Saves game data in event of crash.
     * @throws IOException
     */
    public static void save(SaveData save) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setAnnotationIntrospector(new IgnoreStackPane());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.writerWithDefaultPrettyPrinter().writeValue(new File("data.json"), save);

        FileOutputStream file = new FileOutputStream("save.dat");
        Writer writer = new OutputStreamWriter(new GZIPOutputStream(file), StandardCharsets.UTF_8);
        mapper.writeValue(writer, save);
        writer.close();
        file.close();
    }

    /**
     * Loads saved game data and returns object.
     * @return
     * @throws IOException
     */
    public static SaveData load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setAnnotationIntrospector(new IgnoreStackPane());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        FileInputStream file = new FileInputStream("save.dat");
        Reader reader = new InputStreamReader(new GZIPInputStream(file), StandardCharsets.UTF_8);
        SaveData save = mapper.readValue(reader, SaveData.class);
        reader.close();
        file.close();
        return save;
    }

    /**
     * @return
     */
    public ArrayList<ICommodity> getTreasureIsland() {
        ArrayList<ICommodity> data = new ArrayList<>();
        if (treasureCrew != null)
            data.addAll(treasureCrew);
        if (treasureIsland != null)
            data.addAll(treasureIsland);
        return data;
    }

    /**
     * @return
     */
    public ArrayList<ICommodity> getFlatIsland() {
        ArrayList<ICommodity> data = new ArrayList<>();
        if (flatCrew != null)
            data.addAll(flatCrew);
        if (flatIsland != null)
            data.addAll(flatIsland);
        return data;
    }

    /**
     * @return
     */
    public ArrayList<ICommodity> getPirateIsland() {
        ArrayList<ICommodity> data = new ArrayList<>();
        if (pirateCrew != null)
            data.addAll(pirateCrew);
        if (pirateIsland != null)
            data.addAll(pirateIsland);
        return data;
    }

    /**
     * Ignores a stack pane
     */
    private static class IgnoreStackPane extends JacksonAnnotationIntrospector {
        @Override
        public boolean hasIgnoreMarker(final AnnotatedMember m) {
            return m.getDeclaringClass() == Node.class ||
                    m.getDeclaringClass() == Parent.class ||
                    m.getDeclaringClass() == Region.class ||
                    m.getDeclaringClass() == StackPane.class ||
                    super.hasIgnoreMarker(m);
        }
    }

    /**
     * @return
     */
    public MapSquare[][] getMapGrid() {
        return mapGrid;
    }

    /**
     * @return
     */
    public GameLoop getGameLoop() {
        return gameLoop;
    }

    /**
     * @return
     */
    public ArrayList<Island> getIslands() {
        return islands;
    }

    /**
     * @return
     */
    public ArrayList<int[]> getOutOfBounds() {
        return outOfBounds;
    }

    /**
     * @return
     */
    public ArrayList<CrewCard> getCrewCardDeck() {
        return crewCardDeck;
    }

    /**
     * @return
     */
    public ArrayList<ChanceCardBase> getChanceCardDeck() {
        return chanceCardDeck;
    }
}