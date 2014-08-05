package rippin.bullyscraft.com;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import rippin.bullyscraft.com.Configs.MobsConfig;
import rippin.bullyscraft.com.Configs.ParseItems;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Mob(String name){
        this.name = name;
        plugin = FactionsMissions.instance;
        config = MobsConfig.getConfig();
        loadData();
    }

    private void loadData(){
        weapon = ParseItems.parseItems(config.getString("Mobs." + name + ".Weapon"));
        armor = ParseItems.getArmor(config.getStringList("Mobs." + name + ".Armor"));
        potions = ParseItems.parsePotions(config.getStringList("Mobs." + name + ".Potions"));
        displayName = config.getString("Mobs." + name + ".DisplayName");
        health = config.getDouble("Mobs." + name + ".Health");
        if (config.getBoolean("Mobs." + name + ".Important") != false){
            importantMob = true;
        }

    }

    public void spawnMob(Location loc, Mission m, String metadata){
     LivingEntity ent = (LivingEntity) Bukkit.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, EntityType.valueOf(name));
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
          ent.setCustomName(displayName);
          ent.setCustomNameVisible(true);
      }
        if (health != 0){
            ent.setHealth(health);
        }
        ent.setCanPickupItems(false);
        ent.setMetadata(metadata, new FixedMetadataValue(plugin, m.getName()));
        ent.setRemoveWhenFarAway(false);
        if (metadata.equalsIgnoreCase("CustomEntity")){
            m.getCustomEntitiesUUID().add(ent.getUniqueId().toString());
        }
        else if (metadata.equalsIgnoreCase("ImportantEntity")){
            m.getImportantEntitiesUUID().add(ent.getUniqueId().toString());
        }

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
}
