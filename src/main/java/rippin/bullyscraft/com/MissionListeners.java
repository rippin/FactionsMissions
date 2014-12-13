package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class MissionListeners implements Listener {
    private FactionsMissions plugin;
    public MissionListeners(FactionsMissions plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        String uuid = event.getEntity().getUniqueId().toString();
        List<Mission> activeMissions = MissionManager.getActiveMissions();
        if (!activeMissions.isEmpty()){
            for (Mission m : activeMissions){
                if (m.getCustomEntitiesUUID().contains(uuid)){
                    m.getCustomEntitiesUUID().remove(uuid);
                    event.getDrops().clear();
                }
                else if(m.getImportantEntitiesUUID().contains(uuid)) {
                    m.getImportantEntitiesUUID().remove(uuid);
                    removeImportantBarEntities(m,uuid);
                    event.getDrops().clear();
                }
                if (m.getType() == MissionType.ELIMINATE || m.getType() == MissionType.SPY){
                    int totalEnts = (m.getCustomEntitiesUUID().size() + m.getImportantEntitiesUUID().size());
                    MissionManager.messagePlayersInMission(m, Utils.prefix + "&aA mob has been killed, " + totalEnts + " mobs remain.");
                    if (totalEnts == 0){
                       if (event.getEntity().getKiller() instanceof Player){
                           final Mission finalMission = m;
                           Player p = event.getEntity().getKiller();
                           p.sendMessage(ChatColor.GOLD + "You have killed the last mob in the " + m.getName() + " you will now receive rewards.");
                           m.giveRewards(p);
                           Bukkit.broadcastMessage(Utils.prefix + "Players have eliminated all mobs from " + m.getName() + " mission.");
                           MissionManager.messagePlayersInMission(finalMission, "&4The mission region you are in will be " +
                                   "reverted in 3 minutes, please leave to prevent suffocation damage.");
                           plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                               @Override
                               public void run() {
                                   finalMission.end();
                               }
                           },1200L);
                       }

                    }
                }
            }
        }
    }
    //spy mission
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        // DO MOB BAR CODE
        if (event.getEntity() instanceof LivingEntity){
            List<Mission> activeMissions = MissionManager.getActiveMissions();
            if (!activeMissions.isEmpty()) {
            for (Mission m : activeMissions){
                Map<LivingEntity, String> mobbarents = m.getImportantBarEntities();
                for (LivingEntity mob : mobbarents.keySet()){

                }
            }
          }
        }
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
                        Bukkit.broadcastMessage(Utils.prefix + ((Player) event.getEntity()).getName() + " was damaged and spy mission " + m.getName()
                                + " was failed.");
                        MissionManager.messagePlayersInMission(finalMission, "&4The mission region you are in will be " +
                                "reverted in 3 minutes, please leave to prevent suffocation damage.");
                        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                MissionManager.messagePlayersInMission(finalMission, "&4Rolling back area now, you were warned...");
                                finalMission.end();
                                return;
                            }
                        },1200L);
                     }
                   }
                }
                    //regular damage code
                 else if (m.getCustomEntitiesUUID().contains(event.getDamager().getUniqueId().toString())
                        || m.getImportantEntitiesUUID().contains(event.getDamager().getUniqueId().toString())){
                    final Mission finalMission = m;
                    m.setStatus(MissionStatus.ENDING);
                    Bukkit.broadcastMessage(Utils.prefix + ((Player) event.getEntity()).getName() + " was damaged and spy mission " + m.getName()
                    + " was failed.");
                    MissionManager.messagePlayersInMission(finalMission, "&4The mission region you are in will be " +
                            "reverted in 3 minutes, please leave to prevent suffocation damage.");
                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            MissionManager.messagePlayersInMission(finalMission, "&4Rolling back area now, you were warned...");
                            finalMission.end();
                            return;
                        }
                    },1200L);
                }
              }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event){
        if (event.getTo() != event.getFrom()){
           Mission to = MissionManager.isPlayerInActiveRegion(event.getTo());
            Mission from = MissionManager.isPlayerInActiveRegion(event.getFrom());
             if (from == null && to != null){
                if (to.getEnterMessage() != null){
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Utils.prefix + to.getEnterMessage()));

                }
             }
        }
    }

    public void removeImportantBarEntities(Mission m, String uuid){
        Iterator it  = m.getImportantBarEntities().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<LivingEntity, String> entry = (Map.Entry<LivingEntity, String>)it.next();
                if (entry.getKey().getUniqueId() == UUID.fromString(uuid)){
                it.remove();
                m.removeBar();
            }
        }
    }

}
