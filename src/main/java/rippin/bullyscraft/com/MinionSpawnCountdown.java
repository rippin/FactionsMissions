package rippin.bullyscraft.com;


import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MinionSpawnCountdown {

    private LivingEntity entity;
    private FactionsMissions plugin;
    private long delay;
    private List<String> minions = new ArrayList<String>();
    private BukkitTask task;
    public HashMap<String, Mob> alivenEntUUIDS = new HashMap<String, Mob>();
    public List<LivingEntity> minionEntity = new ArrayList<LivingEntity>();
    private MinionSpawnCountdown thisObject;

    public MinionSpawnCountdown(FactionsMissions plugin, LivingEntity entity, List<String> minions, long delay){
    this.plugin = plugin;
    this.minions = minions;
    this.entity = entity;
    this.delay = delay;
    this.thisObject = this;
    }

    public void startCountdown(){
    task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
        
            public void run() {
                Iterator<LivingEntity> it = getMinionEntity().listIterator();
                if (getEntity().isDead() || getEntity()== null){
                while (it.hasNext()){
                    LivingEntity livingEntity = it.next();
                        livingEntity.getWorld().playEffect(livingEntity.getLocation(), Effect.SMOKE, 5);
                        livingEntity.remove();
                        it.remove();

                    }
                   getTask();
                    Mob.msc.remove(getThisObject());
                    return;
                }
                    else if (getMinions().size() > getMinionEntity().size()){

                        int i = (getMinionEntity().size());

                       while (getMinions().size() > getMinionEntity().size()){
                          Mob m = MobsManager.getMob(getMinions().get(i));
                           LivingEntity e = m.spawnMob(getEntity().getLocation(), null, "minion");
                           getMinionEntity().add(e);
                           getAlivenEntUUIDS().put(e.getUniqueId().toString(), m);
                           ++i;
                       }
                   }
            }
        }, 20L, (delay*20));
    }
    public BukkitTask getTask(){
        return this.task;
    }
    public Entity getEntity(){
        return  this.entity;
    }
    public List<String> getMinions(){
        return  this.minions;
    }
    public List<LivingEntity> getMinionEntity(){
        return this.minionEntity; }

    public MinionSpawnCountdown getThisObject() { return  this.thisObject; }
    public HashMap<String, Mob> getAlivenEntUUIDS() { return this.alivenEntUUIDS; }

    //remove minions on death...
}
