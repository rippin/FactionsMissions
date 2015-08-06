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
        bossLaunchAbility(entity);
    }

    public void bossLaunchAbility(final Entity entity){
        task =   plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            Entity ent = entity;
            int i = 0;
            int r = Utilss.randInt(5, 10);

            @Override
            public void run() {
                if (ent != null && !ent.isDead()) {
                    if (i >= r) {

                        taskInside = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                            Entity entInside = ent;
                            int j = 0;
                            @Override
                            public void run() {
                                if (entInside == null || entInside .isDead()) {
                                    taskInside.cancel();
                                } else {
                                    if (j % 6 == 0) {
                                    ent.getWorld().playEffect(ent.getLocation(), Effect.LAVA_POP, 10);
                                    j = 0;
                                    }
                                }
                                j++;
                            }
                        }, 1L, 3L);


                        List<Player> players = Utilss.getNearbyPlayers(ent);
                        for (Player player : players) {
                            if (((Entity) player).isOnGround()) {
                                player.setVelocity(player.getVelocity().add(new Vector(0,2, 0)));
                                player.setVelocity(player.getLocation().getDirection().multiply(-8));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2));
                            }
                        }

                        r = Utilss.randInt(8, 15);
                        i = 0;
                    }
                } else {
                    task.cancel();
                }

                ++i;
            }
        }, 1L, 20L);
    }

}
