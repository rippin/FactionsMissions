package rippin.bullyscraft.com;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Utilss {
    public static final String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "BullyMissions" + ChatColor.GRAY + "] ";
    public static Location parseLoc(String string){

        String split[] = string.split(":");

        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]),Double.parseDouble(split[2]), Double.parseDouble(split[3]));

    }

    public static String serializeLoc(Location loc){
        return new String(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ());
    }

    public static List<Location> getSpawns(List<String> strings){
        List<Location> locs = new ArrayList<Location>();

        for (String s : strings){
            locs.add(parseLoc(s));
        }
        return locs;
    }

    public static void createRegion(Player player, Mission m, FactionsMissions plugin){
    Selection sel = plugin.getWorldEdit().getSelection(player);
        if (sel != null){
            ProtectedRegion reg = new ProtectedCuboidRegion("Mission_" + m.getName(), new BlockVector(sel.getNativeMinimumPoint()), new BlockVector(sel.getNativeMaximumPoint()));
            HashMap<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
            Set<String> cmds = new HashSet<String>();
            cmds.add("/chest");
            flags.put(DefaultFlag.TNT, StateFlag.State.DENY);
            flags.put(DefaultFlag.ALLOWED_CMDS, cmds);
            flags.put(DefaultFlag.BUILD, StateFlag.State.DENY);
            flags.put(DefaultFlag.OTHER_EXPLOSION, StateFlag.State.DENY);
            flags.put(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
            reg.setFlags(flags);
            plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(reg);
            try {
                plugin.getWorldGuard().getRegionManager(player.getWorld()).save();
                player.sendMessage(Utilss.prefix + ChatColor.GREEN + "Region has been set for " + m.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Selection is null.");
        }
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static List<Player> getNearbyPlayers(Entity ent) {
        List<Player> players = new ArrayList<Player>();
        for (Entity entity : ent.getNearbyEntities(20, 15, 20)){
            if (entity instanceof Player){
                players.add((Player)entity);
            }
        }
        return players;
    }

    public static List<Player> getNearbyPlayers(Player player) {
        List<Player> players = new ArrayList<Player>();
        for (Entity entity : player.getNearbyEntities(20, 15, 20)){
            if (entity instanceof Player){
                players.add((Player)entity);
            }
        }
        return players;
    }
    public static void setNight(FactionsMissions plugin, final String world){
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().getWorld(world).setTime(14000);
            }
        },1L, (400*20L));
    }
}
