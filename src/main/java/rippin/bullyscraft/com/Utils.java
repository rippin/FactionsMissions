package rippin.bullyscraft.com;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static final String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "BullyMissions" + ChatColor.GRAY + "]";
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
            flags.put(new StateFlag("tnt", false), StateFlag.State.DENY);
            reg.setFlags(flags);
            plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(reg);
        }
        else {
            player.sendMessage(ChatColor.RED + "Selection is null.");
        }
    }
}
