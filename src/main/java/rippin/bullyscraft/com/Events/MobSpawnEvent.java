package rippin.bullyscraft.com.Events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import rippin.bullyscraft.com.Mob;


public class MobSpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Mob m;
    private LivingEntity ent;
    private CreatureSpawnEvent.SpawnReason reason;
    public MobSpawnEvent(Mob m, LivingEntity ent, CreatureSpawnEvent.SpawnReason reason){
    this.m = m;
    this.ent = ent;
    this.reason = reason;
  }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public Mob getMob() {
        return m;
    }

    public void setMob(Mob m) {
        this.m = m;
    }

    public LivingEntity getEntity() {
        return ent;
    }

    public void setEntity(LivingEntity ent) {
        this.ent = ent;
    }

    public CreatureSpawnEvent.SpawnReason getReason() {
        return reason;
    }

    public void setReason(CreatureSpawnEvent.SpawnReason reason) {
        this.reason = reason;
    }
}
