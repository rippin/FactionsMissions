package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;


public class FallingBlockCollideCountdown {
    private FactionsMissions plugin;
    private FallingBlock block;
    private int blockTask;
    public FallingBlockCollideCountdown(FactionsMissions plugin, FallingBlock block){
        this.plugin = plugin;
        this.block = block;
    }

    public void startCountdown(){
        blockTask =plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 0;
            public void run() {
                if (i > 50){
                    cancelTask();
                }
                else {
                    List<Entity> ents = block.getNearbyEntities(0.25, 1, 0.25);
                    for (Entity ent : ents){
                        if (ent instanceof Player){
                            Player damagee = (Player) ent;
                            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(block, damagee, EntityDamageEvent.DamageCause.CUSTOM, 10.0);
                            plugin.getServer().getPluginManager().callEvent(event);
                            damagee.playEffect(EntityEffect.HURT);
                            block.remove();
                            cancelTask();
                        }
                    }
                }
                ++i;
            }
        }, 1L, 1L);
    }


    private void cancelTask(){
        Bukkit.getScheduler().cancelTask(blockTask);
        block.remove();
    }

}
