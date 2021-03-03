package net.justminecraft.minigames.bridgepractice;

import net.justminecraft.minigames.minigamecore.Game;
import net.justminecraft.minigames.minigamecore.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BridgePracticeGame extends Game {
    private final BridgePractice bridgePractice;
    public HashMap<Player, Integer> distanceTraveled = new HashMap<>();
    public HashMap<Player, Integer> topDistance = new HashMap<>();
    Scoreboard scoreboard;

    public BridgePracticeGame(Minigame mg) {
        super(mg, false);
        bridgePractice = (BridgePractice) mg;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public String getMap() {
        try {
            ArrayList<String> maps = new ArrayList<>();
            for(File file : BridgePractice.getPlugin().getSchematicFolder().listFiles()) {
                if(file.isFile())
                    maps.add(file.getName());
            }
            if(maps.size() == 0) new IOException("Schematic File is missing, please add maps.");
            Random rand = new Random();
            return maps.get(rand.nextInt(maps.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPlayerDeath(Player p) {
        TopScore topScore = new TopScore(p, topDistance.get(p));
        topScore.save();
        playerLeave(p);
    }

    public void updateScoreboard(Player player) {
        if(distanceTraveled.get(player) > topDistance.get(player)) {
            scoreboard.resetScores(ChatColor.GREEN + "Top Distance");
            topDistance.replace(player, distanceTraveled.get(player));
            scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GREEN + "Top Distance").setScore(topDistance.get(player));
        }
        scoreboard.resetScores(ChatColor.AQUA + "Distance");
        scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.AQUA + "Distance").setScore(distanceTraveled.get(player));
    }
}
