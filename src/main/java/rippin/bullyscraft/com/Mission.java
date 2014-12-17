package rippin.bullyscraft.com;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.Configs.MissionsConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Mission {
    private MissionType type;
    private MissionStatus status;
    private String name;
    private File schematic;
    private Location schematicLoc;
    private ProtectedRegion missionRegion;
    private File revertSchematic;
    private List<String> rewards = new ArrayList<String>();
    private List<Location> spawns = new ArrayList<Location>(); //spawns for regular entities
    private List<String> customEntities = new ArrayList<String>(); // Make string parser.
    private List<String> customEntitiesUUID = new ArrayList<String>();
    private List<String> importantEntitiesUUID = new ArrayList<String>();
    private HashMap<String, Location> importantEntities = new HashMap<String, Location>(); //important entites have specific locations.
    private Map<LivingEntity, String> importantBarEntities = new HashMap<LivingEntity, String>();
    private WorldEdit worldEdit;
    private FactionsMissions plugin;
    private String enterMessage;
    private List<Player> nearbyPlayers = new ArrayList<Player>();
    private BukkitTask task;

    public Mission(String name){
        this.name = name;
        plugin = FactionsMissions.instance;
        worldEdit = plugin.getWorldEdit().getWorldEdit();
        this.status = MissionStatus.INACTIVE;
        loadMissionData();
    }

    public void start(){
    this.status = MissionStatus.ACTIVE;
        try {
            pasteSchematic(schematic); //Mobs are now spawned in this method.
            if (!MissionManager.containsMission(MissionManager.getActiveMissions(), this.getName())){
                MissionManager.getActiveMissions().add(this);
                MissionManager.addActiveToConfig(this);
             }
            if (MissionManager.containsMission(MissionManager.getQueuedMissions(), this.getName())){
               MissionManager.removeMission(MissionManager.getQueuedMissions(), this.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        runBarRepeatingTask(); //Run repeating bar task
    }

    public void end(){
    this.status = MissionStatus.ENDING;
        try {
            revertSchematic(revertSchematic);
            deleteEntities();
                MissionManager.removeMission(MissionManager.getActiveMissions(), this.getName());
                MissionManager.removeActiveToConfig(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
     if (!MissionManager.containsMission(MissionManager.getActiveMissions(), this.getName())){
         MissionManager.getQueuedMissions().add(this);
     }
     //Just in case
     getCustomEntitiesUUID().clear();
     getImportantEntities().clear();
     getImportantBarEntities().clear();
     removeBar(); // Remove boss bar
        //cancel repeating task
        if (task != null) {
        task.cancel();
     }
        this.status = MissionStatus.INACTIVE;
    }

    public void pasteSchematic(File schematic) throws IOException, WorldEditException {
        try {
            if (schematic == null || worldEdit == null){
                System.out.println("Schematic or WorlEdit is null.");
                return;
            }
            if (plugin.getAsyncWorldEdit() != null) {
                AsyncWorldEditHook.revertHookPaste(plugin, this); // prevent dumb noclassdef exception
            }
            else {
            CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);

            EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(schematicLoc.getWorld()), -1);
            cc.paste(editSession, BukkitUtil.toVector(schematicLoc), false);

                spawnCustomEntities();
                spawnImportantEntities();
                setUUIDSToConfig();
                // spawn mobs

            }
        }
            catch (DataException e) {
            throw new IOException("Failed to parse schematic file", e);
        }
    }

    public void revertSchematic(File schematic) throws IOException, WorldEditException {
        try {
            if (schematic == null || worldEdit == null){
                System.out.println("Schematic or WorlEdit is null.");
                return;
            }
            if (plugin.getAsyncWorldEdit() != null) {
                AsyncWorldEditHook.revertHookPaste(plugin, this); // prevent dumb noclassdef exception
            }
            else {
                CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);

                EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(schematicLoc.getWorld()), -1);
                cc.paste(editSession, BukkitUtil.toVector(schematicLoc), false);

            }
        }
        catch (DataException e) {
            throw new IOException("Failed to parse schematic file", e);
        }
    }

    public void loadMissionData(){
        List<String> holder= new ArrayList<String>();
        String schematicName = MissionsConfig.getConfig().getString("Missions." + name + ".Schematic");
        if (schematicName != null) {
        schematic = new File (plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator, schematicName);
       if (schematic == null ||!schematic.exists()){
           System.out.println("Mission schematic for " + name + " does not exist. Create one and put in schematics folder.");
        holder.add("schematic");
            }
        }
        enterMessage = MissionsConfig.getConfig().getString("Missions." + name + ".Enter-Message");

       String revertSchematicName = MissionsConfig.getConfig().getString("Missions." + name + ".Revert-Schematic");
        if (revertSchematicName != null) {
        revertSchematic = new File (plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator, revertSchematicName);
        if (revertSchematic == null || !revertSchematic.exists()){
            System.out.println("Revert schematic for " + name + " does not exist. Create one and put in schematics folder.");
            holder.add("Revert");
            }

        }
        rewards = MissionsConfig.getConfig().getStringList("Missions." + name + ".Rewards");
        if (rewards == null || rewards.isEmpty()){
            System.out.println("Rewards for " + name + " do no exist please put rewards in command format.");
        holder.add("rewards");
        }

        spawns = Utils.getSpawns(MissionsConfig.getConfig().getStringList("Missions." + name + ".Spawns"));
        if (spawns == null || spawns.isEmpty()){
            System.out.println("Spawns for " + name + "do not exist do /setspawn [name] to set a spawn ");
            holder.add("spawn");
        }
        if (MissionsConfig.getConfig().getString("Missions." + name + ".Schematic-Location") != null)
        schematicLoc = Utils.parseLoc(MissionsConfig.getConfig().getString("Missions." + name + ".Schematic-Location"));
        if (schematicLoc != null) {
            if (plugin.getWorldGuard().getRegionManager(schematicLoc.getWorld()).getRegion("Mission_" + name) != null) {
            missionRegion = plugin.getWorldGuard().getRegionManager(schematicLoc.getWorld()).getRegion("Mission_" + name);
         }
       if (missionRegion == null){
            System.out.println("Region for " + name + "does not exist select a region with WE wand and do /bullymission setRegion name");
            holder.add("Region");
        }
        }
        type = MissionType.valueOf(MissionsConfig.getConfig().getString("Missions." + name + ".Type"));
        if (type == null){
           System.out.println("Mission type for " + name + " is null.");
            holder.add("type");
        }
        customEntities = MissionsConfig.getConfig().getStringList("Missions." + name + ".Custom-Entities");
        if (customEntities == null){
            System.out.println("Custom entities for " + name + " is empty / null.");
            holder.add("CustomEntities");
        }
        importantEntities = parseImportatantEntites();
        if (importantEntities == null || importantEntities.isEmpty()){
           System.out.println("Custom entities for " + name + " is empty/null");
        }

        if (holder.isEmpty()){
            MissionManager.getQueuedMissions().add(this);
            System.out.println(this.getName() + " has been added to queued missions");
        }
       }

    public boolean isLocationInMissionRegion(Location loc){
        if (MissionManager.playerInRegion(loc, missionRegion.getMinimumPoint(), missionRegion.getMaximumPoint())){
            return true;
        }
        return false;
    }

    public MissionType getType() {
        return type;
    }

    public void setType(MissionType type) {
        this.type = type;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getSchematic() {
        return schematic;
    }

    public void setSchematic(File schematic) {
        this.schematic = schematic;
    }

    public Location getSchematicLoc() {
        return schematicLoc;
    }

    public void setSchematicLoc(Location schematicLoc) {
        this.schematicLoc = schematicLoc;
        MissionsConfig.getConfig().set("Missions." + name + ".Schematic-Location", Utils.serializeLoc(schematicLoc));
        MissionsConfig.saveFile();
    }

    public ProtectedRegion getMissionRegion() {
        return missionRegion;
    }

    public void setMissionRegion(ProtectedRegion missionRegion) {
        this.missionRegion = missionRegion;
    }

    public File getRevertSchematic() {
        return revertSchematic;
    }

    public void setRevertSchematic(File revertSchematic) {
        this.revertSchematic = revertSchematic;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(List<Location> spawns) {
        this.spawns = spawns;
    }

    public List<String> getCustomEntities() {
        return customEntities;
    }

    public void setCustomEntities(List<String> customEntities) {
        this.customEntities = customEntities;
    }

    public HashMap<String, Location> getImportantEntities() {
        return importantEntities;
    }

    public void setImportantEntities(HashMap<String, Location> importantEntities) {
        this.importantEntities = importantEntities;
    }

    public String getEnterMessage(){ return enterMessage; }

    private HashMap<String, Location> parseImportatantEntites(){
        HashMap<String, Location> imp = new HashMap<String, Location>();
        if (MissionsConfig.getConfig().getConfigurationSection("Missions." + name + ".Important-Entities") != null) {
        for (String key : MissionsConfig.getConfig().getConfigurationSection("Missions." + name + ".Important-Entities").getKeys(false)) {
          String s = MissionsConfig.getConfig().getString("Missions." + name + ".Important-Entities." + key + ".Location");
           imp.put(key, Utils.parseLoc(s));
            }
        }
        return  imp;
    }
   public List<String> getCustomEntitiesUUID(){
       return  customEntitiesUUID;
   }
    public List<String> getImportantEntitiesUUID(){
        return  importantEntitiesUUID;
    }
    public Map<LivingEntity, String> getImportantBarEntities() { return importantBarEntities; }

    public void spawnCustomEntities(){
        List<Location> l = new ArrayList<Location>(spawns);

        for (String s : customEntities){
            if (MobsManager.containsMob(s)){
            Mob m = MobsManager.getMob(s);
                if (m != null){
                   int r = getRandom(l);
                   Location loc = l.get(r);
                    m.spawnMob(loc, this, "CustomEntity");
                }
            }
        }
    }

    public void spawnImportantEntities(){
        Iterator it = importantEntities.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Location> pairs = (Map.Entry<String, Location>) it.next();
            if (MobsManager.containsMob(pairs.getKey())){
                Mob m = MobsManager.getMob(pairs.getKey());
                if (m != null){
                    m.spawnMob(pairs.getValue(), this, "ImportantEntity");
                }
            }
        }

    }

    private int getRandom(List<Location> l){
        int min = 0;
        int max = (l.size() - 1);
        Random r = new Random();
        int randomInt = r.nextInt((max - min) + 1) + min;

        return  randomInt;
      }

    // finish dis
   public void deleteEntities(){
      for (Entity e : schematicLoc.getWorld().getEntities()){
        if (getCustomEntitiesUUID().contains(e.getUniqueId().toString()) || getImportantEntitiesUUID().contains(e.getUniqueId().toString())){
            e.remove();
        }
      }
        removeUUIDSConfig();
   }

    public void giveRewards(Player p){
        for (String s : rewards){
            s = s.replace("%player%", p.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }
    public void setUUIDSToConfig(){
        MissionsConfig.getConfig().set("Missions." + name + ".Custom-Entities-UUIDS", customEntitiesUUID);
        MissionsConfig.getConfig().set("Missions." + name + ".Important-Entities-UUIDS", importantEntitiesUUID);
        MissionsConfig.saveFile();
    }
    public void removeUUIDSConfig(){
        MissionsConfig.getConfig().set("Missions." + name + ".Custom-Entities-UUIDS", null);
        MissionsConfig.getConfig().set("Missions." + name + ".Important-Entities-UUIDS", null);
        MissionsConfig.saveFile();
    }

    public List<Player> setBarForNearbyPlayers(LivingEntity entity, String name){
        for (Player player : nearbyPlayers){
            BarAPI.removeBar(player);
        }
        nearbyPlayers.clear();
        List<Entity> ents = entity.getNearbyEntities(10.0, 10.0, 10.0);
        if (!ents.isEmpty()) {
        for (Entity ent : ents){
            if (ent instanceof Player){
                nearbyPlayers.add((Player)ent);
                float percentage = (float)((entity.getHealth() / entity.getMaxHealth())* 100);
                BarAPI.setMessage((Player)ent,name,percentage);
            }
        }
       }
        return nearbyPlayers;
    }

    public void updateBarHealth(LivingEntity ent){
        for (Player player : nearbyPlayers){
            float percentage = (float)((ent.getHealth() / ent.getMaxHealth())* 100);
            BarAPI.setHealth(player, percentage);
        }
    }
    public void removeBar(){
        for (Player p: nearbyPlayers){
            BarAPI.removeBar(p);
        }
    }

    private void runBarRepeatingTask(){
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Iterator it = importantBarEntities.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry<LivingEntity, String> ents = (Map.Entry<LivingEntity, String>) it.next();
                    setBarForNearbyPlayers(ents.getKey(), ents.getValue());
                }
            }
        },5L, 100L);
    }
    public void cancelBarTask(){
        task.cancel();
    }
}
