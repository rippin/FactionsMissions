package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rippin.bullyscraft.com.Configs.Config;

import java.util.ArrayList;
import java.util.List;

public class StartMissionCountdown {

    private FactionsMissions plugin;
    private int delay;
    private String broadcastMessage;

    public StartMissionCountdown(FactionsMissions plugin, int delay){
    this.plugin = plugin;
    this.delay = delay;
     broadcastMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage"));
    }

    public void startCountdown(){
        final int saveDelay = delay;
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                //announce messages soon?
                if (delay <= 0 && Bukkit.getOnlinePlayers().size() > 0) {
                    if (MissionManager.getQueuedMissions().isEmpty()) {
                        delay = saveDelay;

                    } else {
                        List<Mission> queuedMissionRegion = new ArrayList<Mission>();
                        for (Mission m : MissionManager.getQueuedMissions()){
                            if (!MissionManager.isMissionRegionActive(m)){
                                queuedMissionRegion.add(m);
                            }
                          }
                        if (queuedMissionRegion.isEmpty()){
                            delay = saveDelay;
                        }
                        else {
                            Mission m = MissionManager.getRandomQueuedRegionMission(queuedMissionRegion);
                            m.start();
                            Double x = m.getMainPoint().getX();
                            Double y = m.getMainPoint().getY();
                            Double z = m.getMainPoint().getZ();
                            Bukkit.broadcastMessage(Utilss.prefix + broadcastMessage.replace("%name%", m.getName())
                                    .replace("%type%", m.getType().getValue()).replace("%coords%", "X: " + x.intValue() + " Y: " + y.intValue()) + " Z: " + z.intValue());
                            delay = saveDelay;
                        }

                    }
                }
                --delay;
            }
        }, 20L, 20L);
    }
}
