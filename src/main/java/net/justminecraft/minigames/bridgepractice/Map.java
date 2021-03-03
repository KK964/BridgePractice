package net.justminecraft.minigames.bridgepractice;

import net.justminecraft.minigames.minigamecore.worldbuffer.WorldBuffer;
import org.bukkit.Location;

import java.io.File;

public class Map {
    public void placeSchematic(WorldBuffer w, Location l, String key) {
        try {
            File schem = new File(BridgePractice.getPlugin().getSchematicFolder(), key);
            if(schem.isFile()) {
                w.placeSchematic(l, schem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
