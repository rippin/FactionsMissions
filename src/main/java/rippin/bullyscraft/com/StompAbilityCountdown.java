package rippin.bullyscraft.com;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;


public class StompAbilityCountdown {
    private FactionsMissions plugin;
    private Entity entity;
    private BukkitTask task;
    private BukkitTask taskInside;


    public StompAbilityCountdown(FactionsMissions plugin, Entity ent){
        this.plugin = plugin;
        this.entity = ent;

    }

    public void startCountdown(){
        stompAbility(entity);
    }

    public void stompAbility(final Entity entity){
        taskInside =   plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            Entity ent = entity;
            int i = 0;
            int r = Utilss.randInt(8, 15);

            public void run() {
                if (getEntity() != null && !getEntity().isDead()) {
                    if (i >= r) {
                        ent.setVelocity(ent.getVelocity().add(new Vector(ent.getVelocity().getX(), 1.5, ent.getVelocity().getY())));
                        r = Utilss.randInt(8, 15);
                        //check if ent is in ground
                        taskInside = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                            public void run() {
                                if (getEntity() == null || getEntity().isDead()) {
                                    getInsideTask().cancel();
                                } else if (getEntity().isOnGround()) {
                                    //get nearby players and hurt/do w/e
                                    List<Player> players = Utilss.getNearbyPlayers(getEntity());
                                    getEntity().getWorld().playEffect(getEntity() .getLocation().add(0, 1.5, 0), Effect.EXPLOSION, 30, 30);
                                    for (Player player : players) {
                                        if (((Entity) player).isOnGround()) {
                                            player.setVelocity(player.getVelocity().add(new Vector(player.getVelocity().getX(), 1.5, player.getVelocity().getY())));
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2));
                                        }
                                    }

                                    getInsideTask().cancel();
                                }
                            }
                        }, 5L, 5L);
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
        return  task;
    }

    public BukkitTask getInsideTask(){
        return  taskInside;
    }
    public Entity getEntity(){
        return  this.entity;
    }
}
