package rippin.bullyscraft.com;


import org.bukkit.Effect;
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
            @Override
            public void run() {
                Iterator<LivingEntity> it = minionEntity.listIterator();
                if (entity.isDead() || entity == null){
                while (it.hasNext()){
                    LivingEntity livingEntity = it.next();
                        livingEntity.getWorld().playEffect(livingEntity.getLocation(), Effect.SMOKE, 5);
                        livingEntity.remove();
                        it.remove();

                    }
                    task.cancel();
                    Mob.msc.remove(thisObject);
                    return;
                }
                    else if (minions.size() > minionEntity.size()){

                        int i = (minionEntity.size());

                       while (minions.size() > minionEntity.size()){
                          Mob m = MobsManager.getMob(minions.get(i));
                           LivingEntity e = m.spawnMob(entity.getLocation(), null, "minion");
                           minionEntity.add(e);
                           alivenEntUUIDS.put(e.getUniqueId().toString(), m);
                           ++i;
                       }
                   }
            }
        }, 20L, (delay*20));
    }
    //remove minions on death...
}
