package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
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

}
