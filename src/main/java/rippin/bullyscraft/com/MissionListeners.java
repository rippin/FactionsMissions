package rippin.bullyscraft.com;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import rippin.bullyscraft.com.Configs.MissionsConfig;
import rippin.bullyscraft.com.Events.MobSpawnEvent;


import java.util.*;


public class MissionListeners implements Listener {
    private FactionsMissions plugin;
    private int taskID;
    private int cicleTaskid;
    private int iceBossTaskID;
    private BukkitTask stompTaskID;
    private int fireworkTaskID;
    private BukkitTask stompTaskIDInside;
    public MissionListeners(FactionsMissions plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        if (!event.getFrom().getWorld().getName().equalsIgnoreCase(event.getTo().getWorld().getName())){
            if (MissionManager.getMissionWorld().equalsIgnoreCase(event.getTo().getWorld().getName())){
                event.getPlayer().sendMessage(MissionManager.getTeleportworldMessage());
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(final EntityDeathEvent event){
        String uuid = event.getEntity().getUniqueId().toString();
        Mission m = MissionManager.getMobMission(uuid);

        if (event.getEntity().hasMetadata("minion")){
            Iterator<MinionSpawnCountdown> it = Mob.msc.listIterator();
            while (it.hasNext()) {
                MinionSpawnCountdown minionSpawnCountdown = it.next();
                if (minionSpawnCountdown.alivenEntUUIDS.containsKey(uuid)){
                    Mob mob  = minionSpawnCountdown.alivenEntUUIDS.get(uuid);
                    minionSpawnCountdown.alivenEntUUIDS.remove(uuid);
                    if (!mob.getDrops().isEmpty()) {
                        event.getDrops().clear();
                        event.getDrops().addAll(mob.getDrops());
                    }
                    //remove from entity list in countdown
                    ListIterator<LivingEntity> eIt = minionSpawnCountdown.minionEntity.listIterator();
                    while (eIt.hasNext()){
                        if (eIt.next().getUniqueId().toString().equalsIgnoreCase(uuid)){
                            eIt.remove();
                        }
                    }
                    break;

                }

            }
        }

        if (m == null) {
            if (MissionManager.getMissionWorld().equalsIgnoreCase(event.getEntity().getLocation().getWorld().getName())){
                if (MobsManager.isInReplaceEntUUID(uuid)){
                   Mob repl = MobsManager.removeReplaceEntUUID(uuid);

                    if (!repl.getDrops().isEmpty()) {
                        event.getDrops().clear();
                      event.getDrops().addAll(repl.getDrops());
                    }
                }
          }
  }
       else  {
            //do drops
            //remove uuid
            if (m.getCustomEntitiesUUID().contains(uuid)){
                Mob mob = m.getMobs().get(uuid);
                event.getDrops().clear();
                if (!mob.getDrops().isEmpty()) {
                    event.getDrops().addAll(mob.getDrops());
                }
                m.getCustomEntitiesUUID().remove(uuid);
            }
            else if(m.getImportantEntitiesUUID().contains(uuid)) {
                Mob mob = m.getMobs().get(uuid);
                event.getDrops().clear();
                if (!mob.getDrops().isEmpty()) {
                    event.getDrops().addAll(mob.getDrops());
                }
                m.getImportantEntitiesUUID().remove(uuid);
                removeImportantBarEntities(m,uuid);

            }

            // Second Form spawning
            if (m.getSecondFormMap().size() > 0){
                Iterator it = m.getSecondFormMap().entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry<String, String> entry = (Map.Entry<String,String>) it.next();
                    if (MobsManager.getMob(entry.getValue()) != null && uuid.equalsIgnoreCase(entry.getKey())){

                        String spawnThis = entry.getValue();

                        Mob mob = MobsManager.getMob(spawnThis);
                        if (entry.getValue().equalsIgnoreCase(mob.getName())) {
                            m.getCustomEntitiesUUID().add("placeholder");
                            spawnSecondForm(m, mob, event.getEntity().getLocation()); //
                            MissionsConfig.saveFile();
                            break;
                        }
                    }
                }
            }
            //END SECOND FORM

                if (m.getType() == MissionType.ELIMINATE || m.getType() == MissionType.SPY){
                    int totalEnts = (m.getCustomEntitiesUUID().size() + m.getImportantEntitiesUUID().size());
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + "&aA mob has been killed, " + totalEnts + " mobs remain.");
                    if (totalEnts == 0){
                       if (event.getEntity().getKiller() instanceof Player){
                           final Mission finalMission = m;
                           Player p = event.getEntity().getKiller();
                           p.sendMessage(ChatColor.GOLD + "You have killed the last mob in the " + m.getName() + " you will now receive rewards.");
                           m.giveRewards(p.getName());
                           Bukkit.broadcastMessage(Utilss.prefix + "Players have eliminated all mobs from the " + m.getName() + " mission.");
                           if (MissionManager.pasteSchematic()) {
                               plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                                   @Override
                                   public void run() {
                                       finalMission.end();
                                   }
                               },1200L);
                           }
                           else {
                               finalMission.end();
                           }
                       }
                       fireworkTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                            int i = 10;
                            @Override
                            public void run() {
                                if (i > 0) {
                                Firework fw = (Firework) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.FIREWORK);
                                FireworkMeta meta = fw.getFireworkMeta();
                                FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED, Color.GRAY).trail(true).
                                        withFade(Color.BLACK).with(FireworkEffect.Type.STAR).build();
                                meta.addEffect(effect);
                                meta.setPower(5);
                                fw.setFireworkMeta(meta);
                                }
                                else {
                                    Bukkit.getScheduler().cancelTask(fireworkTaskID);
                                }
                                --i;
                               }
                        },1L, 20L);
                    }
                }
            else if (m.getType() == MissionType.BOSS){
                    Mob boss = m.getMobs().get(uuid);
                    if (boss.isBoss()){
                       Bukkit.broadcastMessage(ChatColor.GREEN + "The Boss in mission " + m.getName() + " has been defeated.");
                        if (event.getEntity().getKiller() instanceof Player){
                        m.giveRewards(event.getEntity().getKiller().getName());
                        }
                        else{
                            List<Player> players = MissionManager.getPlayersInMissionregionObject(m, MissionManager.getMissionWorld());

                            Player randPlayer = players.get(Utilss.randInt(0, players.size() -1));
                            MissionManager.messagePlayersInMission(m, "&4Killer not found, a random player in the region will be rewarded.");
                            m.giveRewards(randPlayer.getName());
                        }
                        m.end();
                    }
                }
            m.getMobs().remove(uuid); // remove UUID of mob here
            }
        }

    //spy mission
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM && (event.getEntity() instanceof Player)){
            if (event.getDamager() instanceof FallingBlock){
                if (event.getDamager().hasMetadata("ICE-BOSS")){
                    Player player = (Player) event.getEntity();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 140, 0), false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 140, 0), false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 0), false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 80, 0), false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 4), false);

                    return;
                }
            }
        }

        if(event.getDamager() instanceof  Snowball && event.getEntity() instanceof  LivingEntity){
            if (!MissionManager.getActiveMissions().isEmpty()){
                if (event.getDamager().hasMetadata("ICE-BOSS")){
                    event.setDamage(15);

                    LivingEntity damaged = (LivingEntity) event.getEntity();
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0), false);
                }
            }
        }

        // DO MOB BAR CODE
        /*
    if (event.getEntity() instanceof LivingEntity){
            List<Mission> activeMissions = MissionManager.getActiveMissions();
            if (!activeMissions.isEmpty()) {
                String uuid = event.getEntity().getUniqueId().toString();
                Mission m = MissionManager.getMobMission(uuid);
                if (m != null) {
                    Map<LivingEntity, String> mobbarents = m.getImportantBarEntities();
                    for (LivingEntity mob : mobbarents.keySet()){
                        if (mob.getUniqueId().toString().equalsIgnoreCase(uuid)){
                            m.updateBarHealth(mob);
                        }
                    }
                }
            }
        }
        else if ((!(event.getEntity() instanceof  Player)) && event.getDamager() instanceof  LivingEntity){
            List<Mission> activeMissions = MissionManager.getActiveMissions();
            if (!activeMissions.isEmpty()){
                String uuid1 = event.getEntity().getUniqueId().toString();
                String uuid2 = event.getEntity().getUniqueId().toString();
                Mission m1 = MissionManager.getMobMission(uuid1);
                Mission m2 = MissionManager.getMobMission(uuid2);
                if (m1 != null || m2 != null){
                    event.setCancelled(true);
                }
            }
        }
        */
        /*

                    END BAR CODE
         */
        else if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)){
        List<Mission> activeMissions = MissionManager.getActiveMissions();
            for (Mission m : activeMissions){
                //Spy mission code
                if (m.getType() == MissionType.SPY && m.getStatus() != MissionStatus.ENDING) {
                    //arrow code
                    if (event.getDamager() instanceof Projectile){
                    if (((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
                    LivingEntity shooter = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                    if (m.getCustomEntitiesUUID().contains(shooter.getUniqueId().toString())
                            || m.getImportantEntitiesUUID().contains(shooter.getUniqueId().toString())){
                        final Mission finalMission = m;
                        Bukkit.broadcastMessage(Utilss.prefix + ((Player) event.getEntity()).getName() + " was damaged and spy mission " + m.getName()
                                + " was failed.");
                        finalMission.end();

                    }
                   }
                }
                    //regular damage code
                 else if (m.getCustomEntitiesUUID().contains(event.getDamager().getUniqueId().toString())
                        || m.getImportantEntitiesUUID().contains(event.getDamager().getUniqueId().toString())){
                    final Mission finalMission = m;
                    m.setStatus(MissionStatus.ENDING);
                    Bukkit.broadcastMessage(Utilss.prefix + ((Player) event.getEntity()).getName() + " was damaged and spy mission " + m.getName()
                    + " was failed.");
                   finalMission.end();
                }
              }
            }
        }
    }
    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event){
        if (!(event.getEntity() instanceof  Player)){
            List<Mission> activeMissions = MissionManager.getActiveMissions();
            if (!activeMissions.isEmpty()) {
                String uuid = event.getEntity().getUniqueId().toString();
                Mission m = MissionManager.getMobMission(uuid);
                if (m != null) {
                    Map<LivingEntity, String> mobbarents = m.getImportantBarEntities();
                    for (LivingEntity mob : mobbarents.keySet()){
                        if (mob.getUniqueId().toString().equalsIgnoreCase(uuid)){
                            m.updateBarHealth(mob);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if (event.getTo() != event.getFrom()){
           if (event.getTo().getWorld().getName().equalsIgnoreCase(MissionManager.getMissionWorld())){
           if (!MissionManager.getAllMissions().isEmpty()) {
           if (!MissionManager.getActiveMissions().isEmpty()) {
            Mission to = MissionManager.isPlayerInActiveRegion(event.getTo());
            Mission from = MissionManager.isPlayerInActiveRegion(event.getFrom());
             if (from == null && to != null){
                if (to.getEnterMessage() != null){
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Utilss.prefix + to.getEnterMessage()));
                return;
                
               }
             }
            else if (to == null && from != null){
                    if (from.getType() == MissionType.TIME){
                        if (!event.getPlayer().hasPermission("bullymissions.admin")) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You may not LEAVE this area while THIS mission is active!");
                        event.getPlayer().getLocation().getDirection().multiply(-2);
                        event.setCancelled(true);
                        }
                    }
                    return;
                }
          }
               Mission to = MissionManager.isPlayerInAnyMisionRegion(event.getTo());
               Mission from = MissionManager.isPlayerInAnyMisionRegion(event.getFrom());
                   if (from == null && to != null){
                       if (to.getStatus() != MissionStatus.ACTIVE){
                           if (!event.getPlayer().hasPermission("bullymissions.admin")) {
                           event.getPlayer().sendMessage(ChatColor.RED + "You may not enter this area while mission is not active!");
                           event.getPlayer().getLocation().getDirection().multiply(-2);
                           event.setCancelled(true);
                           }
                       }
                   }
               }
             }
        }
    }

    public void removeImportantBarEntities(Mission m, String uuid){
        Iterator it  = m.getImportantBarEntities().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<LivingEntity, String> entry = (Map.Entry<LivingEntity, String>)it.next();
                if (entry.getKey().getUniqueId().toString().equalsIgnoreCase(uuid)){
                it.remove();
                m.removeBar();
                m.cancelBarTask();
            }
        }
    }

    public void spawnSecondForm(final Mission m, final Mob mob, final Location loc){
    final String world = loc.getWorld().getName();
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 40;
            @Override
            public void run() {

                if (i > 0) {
                    Bukkit.getWorld(world).playEffect(loc, Effect.MOBSPAWNER_FLAMES, 10);
                    Bukkit.getWorld(world).playSound(loc, Sound.NOTE_BASS_DRUM, 10F, 1F);
                    }
                else{
                    Bukkit.getWorld(world).playSound(loc, Sound.ENDERDRAGON_GROWL, 10F, 1F);
                     mob.spawnMob(loc, m, "ImportantEntity");
                    m.getCustomEntitiesUUID().remove("placeholder");
                    Bukkit.getScheduler().cancelTask(taskID);
                }
                --i;
            }
        },1L, 5L);
        circleLoc(loc, 5, 6, Effect.LAVA_POP);
}
    public void circleLoc(final Location loc, int r, int height, final Effect effect){
    double x;
    double z;
    int y = (int) loc.getY();
        final List<Location> locs = new ArrayList<Location>();
        for (int yy = y; yy <= (height+y); yy++) {
          for (double i = 0.0; i < 360.0; i+=15){
            double angle = (i* Math.PI / 180);
            x = (loc.getX() + r * Math.cos(angle));
            z = (loc.getZ() + r * Math.sin(angle));
           Location newLoc = new Location(loc.getWorld(),x, (0.5+yy), z);
             locs.add(newLoc);
         }
       }

               cicleTaskid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                   int i = 0;
                    @Override
                    public void run() {
                        if (i < locs.size()) {
                        Bukkit.getWorld(loc.getWorld().getName()).playEffect(locs.get(i), effect, 10);
                        }
                        else {
                            Bukkit.getScheduler().cancelTask(cicleTaskid);
                        }
                        ++i;
                    }
                },1L, 1L);

    }


    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event){
        if (event.getWorld().getName().equalsIgnoreCase(MissionManager.getMissionWorld())){
            Chunk c = event.getChunk();
           // System.out.println("Chunk:" + c.getX() + ":" + c.getZ() + " unloaded :" + System.currentTimeMillis() );
            if (!MissionManager.getActiveMissions().isEmpty() || (MissionManager.getRevertMissions() != null && !MissionManager.getRevertMissions().isEmpty())){
            for (Mission m : MissionManager.getActiveMissions()){
                int minX = (int) m.getMissionRegion().getMinimumPoint().getX();
                int minZ = (int) m.getMissionRegion().getMinimumPoint().getZ();

                int maxX = (int) m.getMissionRegion().getMaximumPoint().getX();
                int maxZ = (int) m.getMissionRegion().getMaximumPoint().getZ();

                if (m.getWorld().getName().equalsIgnoreCase(event.getWorld().getName())) {
                if (event.getChunk().getX() >= minX && event.getChunk().getX() <= maxX && event.getChunk().getZ() >= minZ
                        && event.getChunk().getZ() <= maxZ){
                     event.setCancelled(true);
                }

            }
           }
        }
       }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        final Entity entity = event.getEntity();
       if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(MissionManager.getMissionWorld())) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM || entity.hasMetadata("CustomEntity")){
            if (MissionManager.getAllMissions() != null && !MissionManager.getAllMissions().isEmpty()){
                for (Mission m : MissionManager.getAllMissions()){
                  if  (m.isLocationInMissionRegion(event.getLocation())){
                      event.setCancelled(true);
                      return;

                  }
                }
            }
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN
                    || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT
                    || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                    || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG){
                if (MobsManager.isEntityInReplaceMap(event.getEntityType())){
                 Mob m =  MobsManager.getReplaceMob(event.getEntityType());
                    m.spawnMob(event.getLocation(), null, "ReplaceEntity");
                    event.setCancelled(true);
                    return;
                }
            }
        }
     }
  }
    @EventHandler
    public void onMobSpawn(MobSpawnEvent event){
        final Entity entity = event.getEntity();
        if (event.getMob().getAbilities().contains("ICEBOSS")){
            iceBossTaskID =   plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                Entity ent = entity;
                @Override
                public void run() {
                    if (ent != null && !ent.isDead()){
                        iceBossMethod(ent);
                    }
                    else {
                        Bukkit.getScheduler().cancelTask(iceBossTaskID);
                    }
                }
            },1L, 40L);
        }
       else if (event.getMob().getAbilities().contains("STOMP")){
            stompAbility(event.getEntity());
        }
    }

    public void stompAbility(final Entity entity){
        stompTaskID =   plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            Entity ent = entity;
            int i = 0;
            int r = Utilss.randInt(8, 15);

            @Override
            public void run() {
                if (ent != null && !ent.isDead()) {
                    if (i >= r) {
                        ent.setVelocity(ent.getVelocity().add(new Vector(ent.getVelocity().getX(), 1.5, ent.getVelocity().getY())));
                        r = Utilss.randInt(8, 15);
                        //check if ent is in ground
                        stompTaskIDInside = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                            Entity entInside = ent;
                            @Override
                            public void run() {
                                if (entInside == null || entInside .isDead()) {
                                    stompTaskIDInside.cancel();
                                } else if (entInside .isOnGround()) {
                                    //get nearby players and hurt/do w/e
                                    List<Player> players = Utilss.getNearbyPlayers(entInside );
                                    entInside .getWorld().playEffect(entInside .getLocation().add(0, 1.5, 0), Effect.EXPLOSION, 30, 30);
                                    for (Player player : players) {
                                        if (((Entity) player).isOnGround()) {
                                            player.setVelocity(player.getVelocity().add(new Vector(player.getVelocity().getX(), 1.5, player.getVelocity().getY())));
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2));
                                        }
                                    }

                                    stompTaskIDInside.cancel();
                                }
                            }
                        }, 5L, 5L);
                        i = 0;
                    }
                } else {
                    stompTaskID.cancel();
                }

                ++i;
            }
        }, 1L, 20L);
    }
    public void iceBossMethod(Entity entity){
       List<Entity> ents = entity.getNearbyEntities(30, 12, 30);
       for (Entity ent : ents){
           if (ent instanceof  Player){
               Location loc = entity.getLocation().add(0, 2.1, 0).clone();

               Vector to = ent.getLocation().add(0, 1, 0).toVector().clone();
               Vector from = loc.toVector().clone();
               Vector vel = to.subtract(from).normalize();

               FallingBlock block = loc.getWorld().spawnFallingBlock(loc, Material.ICE, (byte) 0);
               block.setVelocity(vel.multiply(2.5));
               block.setMetadata("ICE-BOSS", new FixedMetadataValue(plugin,""));
               new FallingBlockCollideCountdown(plugin, block).startCountdown();

           }
       }
    }
    @EventHandler
    public void blockStateChange(final EntityChangeBlockEvent event){
        if (event.getEntity() instanceof  FallingBlock){
            FallingBlock block = (FallingBlock) event.getEntity();
            if (block.hasMetadata("ICE-BOSS")){
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                         event.getBlock().setType(Material.AIR);
                    }
                }, 80L);
            }
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event){
        if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(MissionManager.getMissionWorld())){
            if (event.getEntity().getType() == EntityType.SKELETON || event.getEntity().getType() == EntityType.ZOMBIE){
                event.setCancelled(true);

            }
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event){
       if (event.getEntity() instanceof  LivingEntity) {
        if (event.getDamager() instanceof  Projectile){
            for (Mob m : MobsManager.getAllMobs()){
                if (event.getDamager().hasMetadata(m.getName())){
                    event.setDamage(m.getProjectileDamage());
                    if (!m.getProjPotions().isEmpty()){
                        ((LivingEntity) event.getEntity()).addPotionEffects(m.getProjPotions());
                    }
                    return;
                }
            }
        }
      }
    }
/*

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onExplode(final EntityExplodeEvent event){
        if (event.getLocation().getWorld().getName().equalsIgnoreCase(MissionManager.getMissionWorld())){
            event.setCancelled(true);
           plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
               @Override
               public void run() {

              List<Block> blocks = new ArrayList<Block>(event.blockList());
                for (Block b : blocks){
                   FallingBlock f =  b.getWorld().spawnFallingBlock(b.getLocation().clone(), b.getType(), b.getData());
                   float x = (float) -1 + (float) (Math.random() * ((1 - -1)+1));
                   float y = (float) -2 + (float) (Math.random() * ((2 - -2)+1));
                   float z = (float) -1 + (float) (Math.random() * ((1 - -1)+1));
                   f.setVelocity(new Vector(x,y,z));
                   f.setDropItem(false);
                   f.setMetadata("explode", new FixedMetadataValue(plugin, ""));
                   }
               }
           }, 4L);
         }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event){
        if (event.getEntity().hasMetadata("explode")){
            event.setCancelled(true);
        }
    }
    */

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        if (MissionManager.isPlayerInActiveRegion(event.getPlayer().getLocation()) != null){
            event.getPlayer().teleport(Bukkit.getWorld(MissionManager.getMissionWorld()).getSpawnLocation());
        }
    }


}
