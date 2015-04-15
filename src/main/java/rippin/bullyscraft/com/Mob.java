package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import rippin.bullyscraft.com.Configs.MobsConfig;
import rippin.bullyscraft.com.Configs.ParseItems;

import java.util.*;

public class Mob {
    private String name;
    private List<ItemStack> armor = new ArrayList<ItemStack>();
    private ItemStack weapon;
    private Set<PotionEffect> potions = new HashSet<PotionEffect>();
    private double health;
    private String displayName;
    private FileConfiguration config;
    private boolean importantMob = false;
    private FactionsMissions plugin;
    private String type;
    private String vehicle;
    private boolean importantBar = false;
    private String secondForm;
    private List<ItemStack> drops = new ArrayList<ItemStack>();
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

        else {
        ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.valueOf(type));
        }

        if (ent instanceof Zombie){
            Zombie s = (Zombie) ent;
            if (s.isBaby()){
                s.setBaby(false);
            }
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
        }
        else if (metadata.equalsIgnoreCase("CustomEntity")){
            m.getCustomEntitiesUUID().add(ent.getUniqueId().toString());
        }
      }
        else {
            if (metadata.equalsIgnoreCase("ReplaceEntity")){
                MobsManager.addReplaceEntityUUIDToListAndFile(ent.getUniqueId().toString(), name);
            }
        }
        return ent;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public boolean isImportantMob() { return  importantMob; }
    public boolean isImportantBar() { return  importantBar; }





}
