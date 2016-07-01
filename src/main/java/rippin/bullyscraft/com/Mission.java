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
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.Configs.MissionsConfig;
import rippin.bullyscraft.com.Events.WinFireWorksCountdown;

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
    private Map<String, Mob> mobs = new HashMap<String, Mob>();
    private int timeMissionLength = 600;
    private Map<String, String> secondForm = new HashMap<String, String>();
    private WorldEdit worldEdit;
    private FactionsMissions plugin;
    private String enterMessage;
    private List<Player> nearbyPlayers = new ArrayList<Player>();
    private BukkitTask task;
    private Mission m;
    private World w;
    private Location mainPoint;
    private List<String> multiBoss = new ArrayList<String>();
    public Mission(String name){
        this.name = name;
        plugin = FactionsMissions.instance;
        worldEdit = plugin.getWorldEdit().getWorldEdit();
        getCustomEntitiesUUID().addAll(MissionsConfig.getConfig().getStringList("Missions." + name + ".Custom-Entities-UUIDS"));
        getImportantEntitiesUUID().addAll(MissionsConfig.getConfig().getStringList("Missions." + name + ".Important-Entities-UUIDS"));
        m = this;
        this.status = MissionStatus.INACTIVE;
        loadMissionData();
    }

    public void start(){
    this.status = MissionStatus.ACTIVE;
        MissionManager.loadChunksinRegion(this);
        try {
            if (MissionManager.pasteSchematic()) {
            pasteSchematic(schematic);
            } else {
                if (!(getType() == MissionType.TIME)){
                spawnCustomEntities();
                spawnImportantEntities();
                setUUIDSToConfig();
                }
            }
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
       if (getType() == MissionType.TIME){
           new TimeMissionLength(plugin, this, timeMissionLength).startCountdown(); //time
       }
       // runBarRepeatingTask(); //Run repeating bar task


    }

        public void end(){
            this.status = MissionStatus.ENDING;
            MissionManager.loadChunksinRegion(this);
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    deleteEntities();
                    getCustomEntitiesUUID().clear();
                    getImportantEntitiesUUID().clear();
                    getImportantBarEntities().clear();
                    getMobs().clear();
                    new WinFireWorksCountdown(plugin, m).StartCountdown();
                   // removeBar(); // Remove boss bar
                }
            }, 10L);
            //Just in case

            //cancel repeating task
            if (task != null) {
                task.cancel();
            }
                   if (MissionManager.pasteSchematic()) {
                    try {
                    revertSchematic(revertSchematic);
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (WorldEditException e) {
                    e.printStackTrace();
                    }
                   }
                    MissionManager.removeMission(MissionManager.getActiveMissions(), getName());
                    MissionManager.removeActiveToConfig(m);
                    if (!MissionManager.containsMission(MissionManager.getActiveMissions(), getName())){
                        MissionManager.getQueuedMissions().add(m);
                    }
            MissionManager.messagePlayersInMission(this, Utilss.prefix + "&4&lYou have 5 minutes to leave the mission area or you will be teleported to spawn.");
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    for (Player p : MissionManager.getPlayersInMissionregionObject(m, MissionManager.getMissionWorld())){
                        if (m.getStatus() != MissionStatus.ACTIVE){
                        p.sendMessage(Utilss.prefix + ChatColor.RED + "Teleporting to spawn...");
                        p.teleport(Bukkit.getWorld("world").getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        }
                    }
                }
            },(5*60 * 20));
                     setStatus(MissionStatus.INACTIVE);
                }
    public void pasteSchematic(File schematic) throws IOException, WorldEditException {
        try {
            if (schematic == null || worldEdit == null){
                System.out.println("Schematic or WorlEdit is null.");
                return;
            }

            else {
            CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);

            EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(w), -1);
            cc.paste(editSession, BukkitUtil.toVector(schematicLoc), false);
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    // spawn mobs
                    spawnCustomEntities();
                    spawnImportantEntities();
                    setUUIDSToConfig();
                }
            }, 20L);

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
            else {
                CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);

                EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(w), -1);
                cc.paste(editSession, BukkitUtil.toVector(schematicLoc), false);
                MissionManager.getRevertMissions().remove(m.getName());
            }
        }
        catch (DataException e) {
            throw new IOException("Failed to parse schematic file", e);
        }
    }

    public void loadMissionData(){
        List<String> holder= new ArrayList<String>();

        if (MissionsConfig.getConfig().getString("Missions." + getName() + ".World") != null){
            w = Bukkit.getWorld(MissionsConfig.getConfig().getString("Missions." + getName() + ".World"));
        }

        if (MissionsConfig.getConfig().getString("Missions." + getName() + ".MainPoint") != null){
            mainPoint = Utilss.parseLoc(MissionsConfig.getConfig().getString("Missions." + getName() + ".MainPoint"));
        }
        else {
            holder.add("1");
            System.out.println("Main point location not found.");
        }

        if (MissionManager.pasteSchematic()){
        String schematicName = MissionsConfig.getConfig().getString("Missions." + name + ".Schematic");
        if (schematicName != null) {
        schematic = new File (plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator, schematicName);
       if (schematic == null ||!schematic.exists()){
           System.out.println("Mission schematic for " + name + " does not exist. Create one and put in schematics folder.");
        holder.add("schematic");
            }
        }
            String revertSchematicName = MissionsConfig.getConfig().getString("Missions." + name + ".Revert-Schematic");
            if (revertSchematicName != null) {
                revertSchematic = new File (plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator, revertSchematicName);
                if (revertSchematic == null || !revertSchematic.exists()){
                    System.out.println("Revert schematic for " + name + " does not exist. Create one and put in schematics folder.");
                    holder.add("Revert");
                }

            }
        }
        //move dis above
        if (MissionsConfig.getConfig().getString("Missions." + name + ".Schematic-Location") != null)
            schematicLoc = Utilss.parseLoc(MissionsConfig.getConfig().getString("Missions." + name + ".Schematic-Location"));

        enterMessage = MissionsConfig.getConfig().getString("Missions." + name + ".Enter-Message");

        rewards = MissionsConfig.getConfig().getStringList("Missions." + name + ".Rewards");
        if (rewards == null || rewards.isEmpty()){
            System.out.println("Rewards for " + name + " do no exist please put rewards in command format.");
        holder.add("rewards");
        }

        spawns = Utilss.getSpawns(MissionsConfig.getConfig().getStringList("Missions." + name + ".Spawns"));
        if (spawns == null || spawns.isEmpty()){
            System.out.println("Spawns for " + name + " do not exist do /setspawn [name] to set a spawn ");
            holder.add("spawn");
        }

         if (w != null) {
         if (plugin.getWorldGuard().getRegionManager(w).getRegion("Mission_" + name) != null) {
                missionRegion = plugin.getWorldGuard().getRegionManager(w).getRegion("Mission_" + name);
            }
        }
         if (missionRegion == null) {
             System.out.println("Region for " + name + " does not exist select a region with WE wand and do /bullymission setRegion name");
             holder.add("Region");
         }
        if (MissionsConfig.getConfig().getString("Missions." + name + ".Type") != null) {
        type = MissionType.valueOf(MissionsConfig.getConfig().getString("Missions." + name + ".Type"));
            if (type == MissionType.TIME){
                if (MissionsConfig.getConfig().getString("Missions." + name + ".Time-Length") != null) {
                    timeMissionLength = MissionsConfig.getConfig().getInt("Missions." + name + ".Time-Length");
                }
            }
        }
        else {
           System.out.println("Mission type for " + name + " is null.");
            holder.add("type");
        }
        if (MissionsConfig.getConfig().getStringList("Missions." + name + ".Custom-Entities") != null){
            customEntities = MissionsConfig.getConfig().getStringList("Missions." + name + ".Custom-Entities");
        }
        else {
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
        if (m.getMissionRegion() == null) { return false; }
        else if (MissionManager.playerInRegion(loc, missionRegion.getMinimumPoint(), missionRegion.getMaximumPoint())){
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
        MissionsConfig.getConfig().set("Missions." + name + ".Schematic-Location", Utilss.serializeLoc(schematicLoc));
        MissionsConfig.saveFile();
    }

    public Location getMainPoint() { return mainPoint; }

    public void setMainPoint(Location loc) { this.mainPoint = loc; }

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

    public Map<String, Mob> getMobs() {
        return mobs;
    }

    public void setMobs(Map<String, Mob> mobs) {
        this.mobs = mobs;
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
           imp.put(key, Utilss.parseLoc(s));
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
    public World getWorld(){ return w; }

    public void setWorld(World world) {this.w = world; }

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
      for (Entity e : w.getEntities()){
        if (getCustomEntitiesUUID().contains(e.getUniqueId().toString()) || getImportantEntitiesUUID().contains(e.getUniqueId().toString())){
            e.remove();
        }
      }
        removeUUIDSConfig();
   }

    public void giveRewards(String p){
        for (String s : rewards){
            s = s.replace("%player%", p);
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
        List<Entity> ents = entity.getNearbyEntities(25.0, 10.0, 25.0);
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
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
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

    public Map<String, String> getSecondFormMap(){
        return secondForm;
    }

    public List<String> getMultiBoss() {
        return multiBoss;
    }

    public void setMultiBoss(List<String> multiBoss) {
        this.multiBoss = multiBoss;
    }
}
