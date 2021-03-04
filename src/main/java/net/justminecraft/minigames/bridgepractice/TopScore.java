package net.justminecraft.minigames.bridgepractice;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TopScore {

    private static final String SCORES_CSV = "scores.csv";
    private final UUID uuid;
    private int score;

    public TopScore(Player player, int score) {
        File file = new File(BridgePractice.getPlugin().getDataFolder(), SCORES_CSV);
        try {
            if(file.createNewFile()) {
                BridgePractice.getPlugin().getLogger().info("Made new topscores file");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.uuid = player.getUniqueId();
        this.score = score;
    }

    public int getScore() {
        String part = getLine();
        if(part == null) return 0;
        String[] parts = part.split(",");
        return Integer.parseInt(parts[1]);
    }

    private String getLine() {
        List<String> lines = getLines();
        if(lines.isEmpty()) return null;
        for(String l : lines) {
            String[] parts = l.split(",");
            if(parts[0].equals(uuid.toString())) {
                return l;
            }
        }
        return null;
    }

    private List getLines() {
        File file = new File(BridgePractice.getPlugin().getDataFolder(), SCORES_CSV);
        if(file.isFile()) {
            try {
                return Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void save() {
        List<String> lines = getLines();
        List<String> line = new ArrayList<>();
        boolean hasSet = false;
        if(lines.isEmpty()) {
            hasSet = true;
            line.add(uuid.toString() + "," + score);
        } else {
            for(String l : lines) {
                String[] part = l.split(",");
                if(part[0].equals(uuid.toString())) {
                    hasSet = true;
                    if(Integer.parseInt(part[1]) < score) {
                        line.add(uuid.toString() + "," + score);
                    } else {
                        line.add(l);
                    }
                } else {
                    line.add(l);
                }
            }
        }
        if(!hasSet)
            line.add(uuid.toString() + "," + score);
        try {
            Files.write(new File(BridgePractice.getPlugin().getDataFolder(), SCORES_CSV).toPath(), line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
