package rippin.bullyscraft.com;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;


public class LaunchAbilityCountdown {
    private FactionsMissions plugin;
    private Entity entity;
    private BukkitTask task;
    private BukkitTask taskInside;


    public LaunchAbilityCountdown(FactionsMissions plugin, Entity ent){
        this.plugin = plugin;
        this.entity = ent;

    }

    public void startCountdown(){
        bossLaunchAbility();
    }

    public void bossLaunchAbility(){
        task =   plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            Entity ent = getEntity();
            int i = 0;
            int r = Utilss.randInt(5, 10);

            public void run() {
                if (getEntity() != null && !getEntity().isDead()) {
                    if (i >= r) {

                        taskInside = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                            Entity entInside = ent;
                            int j = 0;
                            public void run() {
                                if (entInside == null || entInside .isDead()) {
                                    getInsideTask().cancel();
                                } else {
                                    if (j % 6 == 0) {
                                    entInside.getWorld().playEffect(entInside.getLocation(), Effect.LAVA_POP, 10);
                                    j = 0;
                                    }
                                }
                                j++;
                            }
                        }, 1L, 3L);


                        List<Player> players = Utilss.getNearbyPlayers(getEntity());
                        for (Player player : players) {
                            if (((Entity) player).isOnGround()) {
                                player.setVelocity(player.getVelocity().add(new Vector(0,2, 0)));
                                player.setVelocity(player.getLocation().getDirection().multiply(-8));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2));
                                if (getEntity().getCustomName() != null) {
                                    player.sendMessage("* You have been launched by " + getEntity().getCustomName());
                                }
                                else {
                                    player.sendMessage("* You have been launched by a mob");
                                }
                            }
                        }

                        r = Utilss.randInt(8, 15);
                        i = 0;
                    }
                } else {
                    getTask().cancel();
                }

                ++i;
            }
        }, 1L, 20L);
    }
    public BukkitTask getTask(){
        return  this.task;
    }

    public BukkitTask getInsideTask(){
        return  this.taskInside;
    }
    public Entity getEntity(){
        return  this.entity;
    }
}
