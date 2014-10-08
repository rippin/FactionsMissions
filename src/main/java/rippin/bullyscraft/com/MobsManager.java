package rippin.bullyscraft.com;


import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import rippin.bullyscraft.com.Configs.MobsConfig;

import java.util.ArrayList;
import java.util.List;

public class MobsManager {
    private static List<Mob> mobs = new ArrayList<Mob>();

    public static void loadMobs(FactionsMissions plugin){
        mobs.clear();
        for (String key : MobsConfig.getConfig().getConfigurationSection("Mobs").getKeys(false)){
            Mob m = new Mob(key);
            mobs.add(m);
            plugin.logger.info(m.getName() + " mob has been loaded from file");

        }
    }

     public static boolean containsMob(String name){
        for (Mob m : mobs){
            if (m.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
     }
    public static void removeMob(Entity e, Mission m){
    if (e.hasMetadata("CustomEntity")){
       List<MetadataValue> values = e.getMetadata("CustomEntity");
        if (values.contains(m.getName())){
            e.remove();
        }

    }
    else if (e.hasMetadata("ImportantEntity")){
        List<MetadataValue> values = e.getMetadata("ImportantEntity");
        if (values.contains(m.getName())){

            e.remove();
        }
    }
 }
    public static Mob getMob(String s){
        for (Mob m : getAllMobs()){
            if (m.getName().equalsIgnoreCase(s)){
                return m;
            }
        }
        return null;
    }

    public static List<Mob> getAllMobs(){
        return  mobs;
    }
}
