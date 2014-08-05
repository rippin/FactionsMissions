package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class MissionListeners implements Listener {
    private FactionsMissions plugin;
    public MissionListeners(FactionsMissions plugin){
        this.plugin = plugin;
    }

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
                    event.getDrops().clear();
                }
                if (m.getType() == MissionType.ELIMINATE){
                    MissionManager.messagePlayersInMission(m, "&aA mob has been killed, " + m.getCustomEntitiesUUID().size() + m.getImportantEntitiesUUID().size() + " mobs remain.");
                    if ((m.getCustomEntitiesUUID().size() + m.getImportantEntitiesUUID().size()) == 0){
                       if (event.getEntity().getKiller() instanceof Player){
                           Player p = event.getEntity().getKiller();
                           p.sendMessage(ChatColor.GOLD + "You have killed the last mob in the " + m.getType().getValue() + " you will now receive rewards.");
                           m.giveRewards(p);
                           Bukkit.broadcastMessage("Players have eliminated all mobs from an eliminate mission.");

                       }

                    }
                }
            }
        }
    }

}
