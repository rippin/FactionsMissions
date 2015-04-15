package rippin.bullyscraft.com;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.MetadataValue;
import rippin.bullyscraft.com.Configs.MobsConfig;

import java.util.*;

public class MobsManager {
    private static List<Mob> mobs = new ArrayList<Mob>();
    private static HashMap<EntityType, List<String>> replaceEnts = new HashMap<EntityType, List<String>>();
    private static List<String> replaceEntUUIDS = new ArrayList<String>();
    public static void loadMobs(FactionsMissions plugin){
        mobs.clear();
        for (String key : MobsConfig.getConfig().getConfigurationSection("Mobs").getKeys(false)){
            Mob m = new Mob(key);
            mobs.add(m);
            plugin.logger.info(m.getName() + " mob has been loaded from file");

        }
        loadReplaceMobs(); // the mobs that are replaced and not in a mission
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

    public static void loadReplaceMobs(){
        replaceEnts.clear();
        List<String> l = MobsConfig.getConfig().getStringList("Replace-Mobs-In-Mission-World");

        for (String key : l ){
            if (key.contains(":")) {
                String split[] = key.split(":");
                if (EntityType.valueOf(split[0].toUpperCase()) != null){
                    if (isEntityInReplaceMap(EntityType.valueOf(split[0].toUpperCase()))) {
                        List temp = replaceEnts.get(EntityType.valueOf(split[0].toUpperCase()));
                        temp.add(split[1]);
                        replaceEnts.put(EntityType.valueOf(split[0].toUpperCase()), temp);

                        }
                    else {


                    replaceEnts.put(EntityType.valueOf(split[0].toUpperCase()), Arrays.asList(split[1]));
                 }
               }
            }

        }


    }



    public static boolean isEntityInReplaceMap(EntityType type){

        if (replaceEnts.containsKey(type)){
            return true;
        }
        return false;
    }

    public static List<String> getReplaceEntUUIDS(){
        return  replaceEntUUIDS;
    }

    public static void addReplaceEntityUUIDToListAndFile(String uuid, String name){
        replaceEntUUIDS.add(uuid + ":" + name);
        MobsConfig.getConfig().set("Alive-Mobs-Replaced", replaceEntUUIDS);
        MobsConfig.saveFile();
    }

    public static Mob removeReplaceEntUUID(String uuid){
        Iterator<String> it = getReplaceEntUUIDS().listIterator();
        while (it.hasNext()){
            String split[] = it.next().split(":");
            if (split[0].equalsIgnoreCase(uuid)){
                it.remove();
                MobsConfig.getConfig().set("Alive-Mobs-Replaced", replaceEntUUIDS);
                MobsConfig.saveFile();
                return MobsManager.getMob(split[1]);
            }
        }
        return  null;
    }

    public static boolean isInReplaceEntUUID(String uuid){
        for (String key : replaceEntUUIDS){
            String parse[] = key.split(":");
            if (parse[0].equalsIgnoreCase(uuid)){
                return true;
            }
        }
        return  false;
    }
    public static void clearReplaceEntUUIDs(){
        getReplaceEntUUIDS().clear();
        MobsConfig.getConfig().set("Alive-Mobs-Replaced", replaceEntUUIDS);
        MobsConfig.saveFile();

    }

    public static List<String> getReplaceMobDataList(EntityType e){
      return replaceEnts.get(e);
    }

    public static Mob getReplaceMob(EntityType e){
        List<String> l = getReplaceMobDataList(e);
        int rand = Utilss.randInt(0, 100);
        ListIterator<String> it = l.listIterator();

        while (it.hasNext()){
            String s = it.next();
            String split[] = s.split("@");
            int i = Integer.parseInt(split[1]);
            if (rand > i){
                l.listIterator().remove();
            }

        }

        int rand2 = Utilss.randInt(0, l.size() - 1);

        String ss = l.get(rand2);
        String split[] = ss.split("@");

        return getMob(split[0]);

    }


    public static List<Mob> getAllMobs(){
        return  mobs;
    }
}
