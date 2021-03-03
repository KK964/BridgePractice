package net.justminecraft.minigames.bridgepractice;

import net.justminecraft.minigames.minigamecore.Game;
import net.justminecraft.minigames.minigamecore.MG;
import net.justminecraft.minigames.minigamecore.Minigame;
import net.justminecraft.minigames.minigamecore.worldbuffer.WorldBuffer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BridgePractice extends Minigame implements Listener {
    private static BridgePractice bridgePractice;
    private static File DATA_FOLDER;
    private static File SCHEMATIC_FOLDER;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        bridgePractice = this;
        DATA_FOLDER = getDataFolder();
        SCHEMATIC_FOLDER = new File(DATA_FOLDER.getPath() + System.getProperty("file.separator") +"schematics");
        SCHEMATIC_FOLDER.mkdir();
        MG.core().registerMinigame(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Bridge Practice Enabled");
    }
    @Override
    public void onDisable() {
        getLogger().info("Bridge Practice Disabled");
    }

    @Override
    public int getMaxPlayers() {return 1;}
    @Override
    public int getMinPlayers() {return 1;}
    @Override
    public String getMinigameName() {return "BridgePractice";}
    @Override
    public Game newGame() {return new BridgePracticeGame(this);}

    @Override
    public void startGame(Game game) {
        Random random = new Random();
        BridgePracticeGame g = (BridgePracticeGame) game;
        Objective d = g.scoreboard.registerNewObjective("distance", "dummy");
        d.setDisplaySlot(DisplaySlot.SIDEBAR);
        d.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Bridge Practice");
        int color = random.nextInt(15);
        ItemStack i = new ItemStack(Material.WOOL, 64, (short) color);
        i.setAmount(64);
        d.getScore(" ").setScore(99999);
        d.getScore(ChatColor.AQUA + "Distance").setScore(1);
        d.getScore(ChatColor.YELLOW + "justminecraft.net").setScore(1);
        for(Player p : g.players) {
            TopScore topScore = new TopScore(p, 0);
            int topDistance = topScore.getScore();
            d.getScore(ChatColor.GREEN + "Top Distance").setScore(topDistance);
            p.setScoreboard(g.scoreboard);
            g.distanceTraveled.put(p, 0);
            g.topDistance.put(p, topDistance);
            p.teleport(new Location(g.world, 0, 64, 0));
            p.getInventory().setItem(0, i);
        }
    }

    @Override
    public void generateWorld(Game game, WorldBuffer w) {
        BridgePracticeGame g = (BridgePracticeGame) game;
        String key = g.getMap();
        g.disablePvP = true;
        g.moneyPerDeath = 0;
        g.moneyPerWin = 0;
        g.disableBlockBreaking = false;
        g.disableBlockPlacing = false;
        g.disableHunger = true;

        Map m = new Map();
        Location l = new Location(g.world, 0, 64, 0);
        m.placeSchematic(w,l,key);
    }

    @EventHandler
    public void onBlockUse(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Game g = MG.core().getGame(p);
        if(g == null || g.minigame != this) return;
        byte iData = e.getBlock().getData();
        ItemStack itemStack = new ItemStack(e.getBlock().getType(), 1, iData);
        getServer().getScheduler().runTaskLater(this, () -> {p.getInventory().addItem(itemStack);}, 1);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Game g = MG.core().getGame(p);
        if(g == null || g.minigame != this) return;
        BridgePracticeGame game = (BridgePracticeGame) g;
        if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        int x = (int) Math.round(e.getTo().getX());
        int z = (int) Math.round(e.getTo().getZ());
        int dis = Math.max(x, z);
        dis = (dis < 0 ? -dis : dis);
        game.distanceTraveled.replace(p, dis);
        game.updateScoreboard(p);
    }

    public File getSchematicFolder() {
        return SCHEMATIC_FOLDER;
    }

    public static BridgePractice getPlugin() {
        return bridgePractice;
    }
}
