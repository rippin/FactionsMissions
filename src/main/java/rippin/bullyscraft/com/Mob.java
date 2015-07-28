package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import rippin.bullyscraft.com.Configs.MobsConfig;
import rippin.bullyscraft.com.Configs.ParseItems;
import rippin.bullyscraft.com.Events.MobSpawnEvent;

import java.util.*;

public class Mob {
    private String name;
    private List<ItemStack> armor = new ArrayList<ItemStack>();
    private ItemStack weapon;
    private Set<PotionEffect> potions = new HashSet<PotionEffect>();
    private Set<PotionEffect> projPotions = new HashSet<PotionEffect>();
    private double health;
    private String displayName;
    private FileConfiguration config;
    private boolean importantMob = false;
    private FactionsMissions plugin;
    private String type;
    private String vehicle;
    private boolean importantBar = false;
    private String secondForm;
    private String proj;
    private double projVelocity = 1;
    private long projectileDelay;
    private List<String> minions = new ArrayList<String>();
    private long spawnMinionDelay = 1;
    private double projectileDamage;
    private List<String> abilities = new ArrayList<String>();
    private List<ItemStack> drops = new ArrayList<ItemStack>();
    private boolean boss = false;
    public static List<MinionSpawnCountdown> msc = new ArrayList<MinionSpawnCountdown>();
    public Mob(String name){
        this.name = name;
        plugin = FactionsMissions.instance;
        config = MobsConfig.getConfig();
        loadData();
    }

    private void loadData(){
        if (config.getString("Mobs." + name + ".Weapon") != null)
        weapon = ParseItems.parseItems(config.getString("Mobs." + name + ".Weapon"));
        if (config.getStringList("Mobs." + name + ".Armor") != null)
        armor = ParseItems.getArmor(config.getStringList("Mobs." + name + ".Armor"));
        if (config.getStringList("Mobs." + name + ".Potions") != null)
        potions = ParseItems.parsePotions(config.getStringList("Mobs." + name + ".Potions"));
       if (config.getStringList("Mobs." + name + ".Drops") != null){
           drops = ParseItems.getAllItems(config.getStringList("Mobs." + name + ".Drops"));
       }
       if (config.getBoolean("Mobs." + name + ".Boss")){
           boss = true;
       }

        if (config.getStringList("Mobs." + name + ".Abilities") != null){
            abilities = config.getStringList("Mobs." + name + ".Abilities");
        }
        if (config.getString("Mobs." + name + ".Projectile.Type") != null){
           proj = (config.getString("Mobs." + name + ".Projectile.Type"));
            projectileDelay = (config.getLong("Mobs." + name + ".Projectile.Delay"));
            projectileDamage = (config.getDouble("Mobs." + name + ".Projectile.Damage"));
            projVelocity = (config.getDouble("Mobs." + name + ".Projectile.Velocity"));
            if (config.getStringList("Mobs." + name + ".Projectile.Potions") != null)
                projPotions = ParseItems.parsePotions(config.getStringList("Mobs." + name + ".Projectile.Potions"));

        }

        if (config.getString("Mobs." + name + ".DisplayName") != null)
        displayName = config.getString("Mobs." + name + ".DisplayName");
        if ((Double) config.getDouble("Mobs." + name + ".Health") != null)
        health = config.getDouble("Mobs." + name + ".Health");
        if (config.getBoolean("Mobs." + name + ".Important") != false){
            importantMob = true;
        }
        if (config.getBoolean("Mobs." + name + ".Important-Health-Bar") != false){
            importantBar = true;
        }
        if (config.getString("Mobs." + name + ".Type") != null)
            type = config.getString("Mobs." + name + ".Type");
        if (config.getString("Mobs." + name + ".Vehicle") != null){
            this.vehicle = config.getString("Mobs." + name + ".Vehicle");
        }
        if (config.getStringList("Mobs." + name + ".Minions") != null){
            minions = config.getStringList("Mobs." + name + ".Minions");
           spawnMinionDelay = config.getLong("Mobs." + name + ".Minion-Spawn-Delay");
        }
        if (config.getString("Mobs." + name + ".SecondForm") != null){
           secondForm = config.getString("Mobs." + name + ".SecondForm");
        }
    }

    public LivingEntity spawnMob(Location loc, Mission m, String metadata){

        if (type == null){
         System.out.println("Failed to spawn because type is null");
             return null;

         }
        LivingEntity ent;
        if (type.toUpperCase().equalsIgnoreCase("WITHER_SKELETON")){
           ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.SKELETON);
            ((Skeleton) ent).setSkeletonType(Skeleton.SkeletonType.WITHER);
        }
        else if (type.toUpperCase().equalsIgnoreCase("LIGHTNING_CREEPER")){
            ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.CREEPER);
            ((Creeper) ent).setPowered(true); // make it charged
        }
        else if (type.toUpperCase().equalsIgnoreCase("ELDER_GUARDIAN")){
            ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.GUARDIAN);
            ((Guardian) ent).setElder(true);
        }
        else if (type.toUpperCase().equalsIgnoreCase("KILLER_RABBIT")){
            ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.RABBIT);
            ((Rabbit) ent).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
        }

        else if (type.toUpperCase().equalsIgnoreCase("BABY_ZOMBIE")){
            ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.ZOMBIE);
            ((Zombie) ent).setBaby(true);
        }
        else if (type.toUpperCase().equalsIgnoreCase("ZOMBIE")){
            ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.ZOMBIE);
            ((Zombie) ent).setBaby(false);
        }

        else {
        ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.valueOf(type));
        }

        EntityEquipment ee = ent.getEquipment();
     if (weapon != null){
         ee.setItemInHand(weapon);
     }
     if (armor != null && !armor.isEmpty()){
         ee.setArmorContents(armor.toArray(new ItemStack[armor.size()]));
     }
     if (potions != null && !potions.isEmpty()){
        ent.addPotionEffects(potions);
     }
      if (displayName != null){
          ent.setCustomName(ChatColor.translateAlternateColorCodes('&', displayName));
          ent.setCustomNameVisible(true);
      }
            if (health > 0){
            ent.setMaxHealth(health);
            ent.setHealth(health);
        }
        if (secondForm != null){
               m.getSecondFormMap().put(ent.getUniqueId().toString(), secondForm);

        }
        if (proj != null){
            new ProjectileLaunchCountdown(plugin, ent, getName(), proj, projectileDelay, projVelocity).startCountdown();
        }
        if (!minions.isEmpty()){
            MinionSpawnCountdown c = new MinionSpawnCountdown(plugin, ent, minions, spawnMinionDelay);
            c.startCountdown();
            msc.add(c);
        }
        if (vehicle != null){
            if (MobsManager.containsMob(vehicle)){
                Mob veh = MobsManager.getMob(vehicle);
                LivingEntity entVeh = veh.spawnMob(loc, m, metadata);
                entVeh.setPassenger(ent);

            }
            else {
                try {
                if (EntityType.valueOf(vehicle) != null){
                    Entity e = Bukkit.getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.valueOf(vehicle));
                    if (e instanceof Horse){
                        if (config.getString("Mobs." + vehicle + ".HorseType") != null){
                           //dont bother checking just take string.
                            Horse.Variant variant = Horse.Variant.valueOf(config.getString("Mobs." + vehicle + ".HorseType"));
                            ((Horse) e).setVariant(variant);
                        }
                    }
                    e.setPassenger(ent);
                }
                } catch (IllegalArgumentException ex){
                    System.out.println("Not a valid entitytype.");
                }
            }
        }
        ent.setCanPickupItems(false);
        ent.setRemoveWhenFarAway(false);

        //ent.setMetadata(metadata, new FixedMetadataValue(plugin, m.getName()));
        if (m != null) {
        if (metadata.equalsIgnoreCase("ImportantEntity")) {
            m.getImportantEntitiesUUID().add(ent.getUniqueId().toString());
            if (importantBar) {
                m.getImportantBarEntities().put(ent, getName());
            }
            m.getMobs().put(ent.getUniqueId().toString(), this);
        }
        else if (metadata.equalsIgnoreCase("CustomEntity")){
            m.getCustomEntitiesUUID().add(ent.getUniqueId().toString());
            m.getMobs().put(ent.getUniqueId().toString(), this);
        }
      }
        if (metadata.equalsIgnoreCase("minion")){
            ent.setMetadata(metadata, new FixedMetadataValue(plugin, ""));
        }
        else {
            if (metadata.equalsIgnoreCase("ReplaceEntity")){
                MobsManager.addReplaceEntityUUIDToListAndFile(ent.getUniqueId().toString(), name);
            }
        }
        MobSpawnEvent mse = new MobSpawnEvent(this, ent, CreatureSpawnEvent.SpawnReason.CUSTOM);
        plugin.getServer().getPluginManager().callEvent(mse);
        return ent;
    }

    public List<MinionSpawnCountdown> getMsc() {
        return msc;
    }

    public void setMsc(List<MinionSpawnCountdown> msc) {
        this.msc = msc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemStack> getArmor() {
        return armor;
    }

    public void setArmor(List<ItemStack> armor) {
        this.armor = armor;
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public void setWeapon(ItemStack weapon) {
        this.weapon = weapon;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }

    public Set<PotionEffect> getPotions() {
        return potions;
    }

    public void setPotions(Set<PotionEffect> potions) {
        this.potions = potions;
    }

    public String getProj() {
        return proj;
    }

    public void setProj(String proj) {
        this.proj = proj;
    }

    public Set<PotionEffect> getProjPotions() {
        return projPotions;
    }

    public void setProjPotions(Set<PotionEffect> projPotions) {
        this.projPotions = projPotions;
    }

    public double getProjectileDamage() {
        return projectileDamage;
    }

    public void setProjectileDamage(double projectileDamage) {
        this.projectileDamage = projectileDamage;
    }

    public long getProjectileDelay() {
        return projectileDelay;
    }

    public void setProjectileDelay(long projectileDelay) {
        this.projectileDelay = projectileDelay;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public boolean isImportantMob() { return  importantMob; }
    public boolean isImportantBar() { return  importantBar; }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }
}
