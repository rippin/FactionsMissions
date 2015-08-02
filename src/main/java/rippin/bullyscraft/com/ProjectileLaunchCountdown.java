package rippin.bullyscraft.com;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;


public class ProjectileLaunchCountdown {
    private FactionsMissions plugin;
    private long delay;
    private String mobName;
    private Entity entity;
    private String proj;
    private BukkitTask task;
    private double velocity;

    public ProjectileLaunchCountdown(FactionsMissions plugin, Entity ent, String mobName, String proj, long delay, double velocity){
        this.plugin = plugin;
        this.entity = ent;
        this.mobName = mobName;
        this.proj = proj;
        this.delay = delay;
        this.velocity = velocity;
    }

    public void startCountdown(){
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (entity.isDead() || entity == null){
                    task.cancel();
                }
                projectileTask(entity, proj, mobName, velocity);
            }
        },1L, (delay * 20));
    }

    public void projectileTask(Entity entity, String proj, String name, double velocity){

        try {
            Class projectileClass = Class.forName("org.bukkit.entity." + proj);
            List<Entity> ents = entity.getNearbyEntities(12, 7, 12);
            for (Entity player : ents){
                if (player instanceof Player){
                    Location loc = entity.getLocation().add(0, 3.15, 0).clone();
                    org.bukkit.util.Vector to = player.getLocation().toVector().clone();
                    org.bukkit.util.Vector from = loc.toVector().clone();
                    org.bukkit.util.Vector vel = to.subtract(from).normalize();
                    Projectile projectile = (Projectile) entity.getWorld().spawn(loc, projectileClass);
                    projectile.setVelocity(vel.multiply(velocity));
                    projectile.setMetadata(name, new FixedMetadataValue(plugin, ""));

                }
            }
        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
