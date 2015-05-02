package rippin.bullyscraft.com.Configs;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ParseItems {

    //Example LEATHER_HELMET ENCHANT@LEVEL LEATHERCOLOR
    public static List<ItemStack> getArmor(List<String> items){
        if (items == null){
            return null;
        }
        List<ItemStack> armor = new ArrayList<ItemStack>();
        for (String i : items){
            armor.add(parseItems(i));
        }
        return armor;
    }

    //ITEM:QUANTITY NAME:name_space Data:data ENCHANT:Sharpness@5 Lore:lore|lore_lore | = space _ = new line
    @SuppressWarnings("deprecated")
    public static ItemStack parseItems(String s){
        if (s.contains(" ")){
            String splitspace[] = s.split("\\s+");
            ItemStack i;
            if (splitspace[0].contains(":")) {
                String splitamount[] = splitspace[0].split(":");
                if (splitspace[1].contains("Data:")){
                    String split[] = splitspace[1].split(":");
                    i = new ItemStack(Material.getMaterial(splitamount[0]),
                            Integer.parseInt(splitamount[1]), (short)0, Byte.parseByte(split[1]));
                }
                else {
                    i = new ItemStack(Material.getMaterial(splitamount[0]), Integer.parseInt(splitamount[1]));
                }
            }

            else{
                if (splitspace[1].contains("Data:")){
                    String split[] = splitspace[1].split(":");
                    i = new ItemStack(Material.getMaterial(splitspace[0]), 1, (short)0, Byte.parseByte(split[1]));
                }
                else {
                    i = new ItemStack(Material.getMaterial(splitspace[0]));
                }
            }
            for (int j = 1; j < splitspace.length; j++){
                ItemMeta meta = i.getItemMeta();
                if (splitspace[j].toLowerCase().contains("Name".toLowerCase())){
                    String splitName[] = splitspace[j].split(":");
                    splitName[1] = splitName[1].replace("_", " ");

                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', splitName[1]));
                    i.setItemMeta(meta);
                }
                else if (splitspace[j].toLowerCase().contains("Lore".toLowerCase())){
                    meta = i.getItemMeta();
                    String splitLore[] = splitspace[j].split(":");
                    splitLore[1] = splitLore[1].replace("|", " ");
                    splitLore[1] = ChatColor.translateAlternateColorCodes('&', splitLore[1]);
                    if (splitLore[1].contains("_")){
                        String splitLoreSpace[] = splitLore[1].split("_");
                        meta.setLore(Arrays.asList(splitLoreSpace));
                    }
                    else {
                        String splitLoreSpace[] = new String[1];
                        splitLoreSpace[0] = splitLore[1];
                        meta.setLore(Arrays.asList(splitLoreSpace));
                    }
                    i.setItemMeta(meta);
                }
                else if (splitspace[j].toLowerCase().contains("Enchant".toLowerCase())){
                    String splitEnchant[] = splitspace[j].split(":");
                    String splitLevel[] = splitEnchant[1].split("@");
                    i.addUnsafeEnchantment(Enchantment.getByName(getOfficialEnchantmentName(splitLevel[0])), Integer.parseInt(splitLevel[1]));
                }
                else if (splitspace[j].toLowerCase().contains("LeatherColor".toLowerCase())){
                    if (isArmor(i.toString())) {
                        if (i.getType().toString().toLowerCase().contains("leather")){
                            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) i.getItemMeta();
                            String splitColor[] = splitspace[j].split(":");
                            leatherArmorMeta.setColor(getFromString(splitColor[1]));
                            i.setItemMeta(leatherArmorMeta);

                        }
                    }
                }
            }
            return i;
        }
        else{
            if (s.contains(":")){
                String split[] = s.split(":");
                return new ItemStack(Material.getMaterial(split[0]), Integer.parseInt(split[1]));
            }
            else{
                return new ItemStack(Material.getMaterial(s));
            }
        }
    }

    public static List<ItemStack> getAllItems(List<String> strings){
        List<ItemStack> items = new ArrayList<ItemStack>();

        for (String key : strings){
            items.add(parseItems(key));
        }

        return items;
    }

    public static Set<PotionEffect> parsePotions(List<String> potions){
        Set<PotionEffect> parsedPotions = new HashSet<PotionEffect>();
        for (String s : potions) {
            if (s.contains(":")) {
                final String[] split = s.split(":");
               parsedPotions.add(new PotionEffect(PotionEffectType.getByName(getOfficialPotionName(split[0])),
                                Integer.parseInt(split[1]), Integer.parseInt(split[2]), true));
                    }
        }
        return parsedPotions;
    }

    public static Color getFromString(String string){

        if (string.equalsIgnoreCase("black")){
            return Color.BLACK;
        }
        else if (string.equalsIgnoreCase("blue")){
            return Color.BLUE;
        }

        else if (string.equalsIgnoreCase("green")){
            return Color.GREEN;
        }
        else if (string.equalsIgnoreCase("silver")){
            return Color.SILVER;
        }
        else if (string.equalsIgnoreCase("gray")){
            return Color.GRAY;
        }
        else if (string.equalsIgnoreCase("yellow")){
            return Color.YELLOW;
        }
        else if (string.equalsIgnoreCase("aqua")){
            return Color.AQUA;
        }
        else if (string.equalsIgnoreCase("fuchsia")){
            return Color.FUCHSIA;
        }
        else if (string.equalsIgnoreCase("purple")){
            return Color.PURPLE;
        }
        else if (string.equalsIgnoreCase("teal")){
            return Color.TEAL;
        }
        else if (string.equalsIgnoreCase("lime")){
            return Color.LIME;
        }
        else if (string.equalsIgnoreCase("olive")){
            return Color.OLIVE;
        }
        else if (string.equalsIgnoreCase("white")){
            return Color.WHITE;
        }
        else{
            return Color.MAROON;
        }

    }


    public static String getOfficialEnchantmentName(String buff) {


        if (buff.equalsIgnoreCase("POWER")) {
            buff = "ARROW_DAMAGE";
            return buff;
        } else if (buff.equalsIgnoreCase("FLAME")) {
            buff = "ARROW_FIRE";
        } else if (buff.equalsIgnoreCase("PUNCH")) {
            buff = "ARROW_KNOCKBACK";
        } else if (buff.equalsIgnoreCase("INFINITY")) {
            buff = "ARROW_INFINITE";
        } else if (buff.equalsIgnoreCase("SHARPNESS")) {
            buff = "DAMAGE_ALL";
        } else if (buff.equalsIgnoreCase("BANEOFARTHROPODS")) {
            buff = "DAMAGE_ARTHROPODS";
        } else if (buff.equalsIgnoreCase("SMITE")) {
            buff = "DAMAGE_UNDEAD";
        } else if (buff.equalsIgnoreCase("EFFICIENCY")) {
            buff = "DIG_SPEED";
        } else if (buff.equalsIgnoreCase("UNBREAKING")) {
            buff = "DURABILITY";
        } else if (buff.equalsIgnoreCase("FIREASPECT")) {
            buff = "FIRE_ASPECT";
        } else if (buff.equalsIgnoreCase("KNOCKBACK")) {
            buff = "KNOCKBACK";
        } else if (buff.equalsIgnoreCase("FORTUNE")) {
            buff = "LOOT_BONUS_BLOCKS";
        } else if (buff.equalsIgnoreCase("LOOTING")) {
            buff = "LOOT_BONUS_MOBS";
        } else if (buff.equalsIgnoreCase("RESPIRATION")) {
            buff = "OXYGEN";
        } else if (buff.equalsIgnoreCase("PROTECTION")) {
            buff = "PROTECTION_ENVIRONMENTAL";
        } else if (buff.equalsIgnoreCase("BLASTPROTECTION")) {
            buff = "PROTECTION_EXPLOSIONS";
        } else if (buff.equalsIgnoreCase("FEATHERFALLING")) {
            buff = "PROTECTION_FALL";
        } else if (buff.equalsIgnoreCase("FIREPROTECTION")) {
            buff = "PROTECTION_FIRE";
        } else if (buff.equalsIgnoreCase("PROJECTILEPROTECTION")) {
            buff = "PROTECTION_PROJECTILE";
        } else if (buff.equalsIgnoreCase("SILKTOUCH")) {
            buff = "SILK_TOUCH";
        } else if (buff.equalsIgnoreCase("THORNS")) {
            buff = "THORNS";
        } else if (buff.equalsIgnoreCase("AQUAAFFINITY")) {
            buff = "WATER_WORKER";
        }
        return buff;

    }

    public static String getOfficialPotionName(String buff) {


        if (buff.equalsIgnoreCase("ABSORPTION")) {
            buff = "ABSORPTION";
            return buff;
        } else if (buff.equalsIgnoreCase("BLINDNESS")) {
            buff = "BLINDNESS";
        } else if (buff.equalsIgnoreCase("NAUSEA")) {
            buff = "CONFUSION";
        } else if (buff.equalsIgnoreCase("RESISTANCE")) {
            buff = "DAMAGE_RESISTANCE";
        } else if (buff.equalsIgnoreCase("HASTE")) {
            buff = "FAST_DIGGING";
        } else if (buff.equalsIgnoreCase("FIRERESISTANCE")) {
            buff = "FIRE_RESISTANCE";
        } else if (buff.equalsIgnoreCase("INSTANTDAMAGE")) {
            buff = "HARM";
        } else if (buff.equalsIgnoreCase("INSTANTHEALTH")) {
            buff = "HEAL";
        } else if (buff.equalsIgnoreCase("HEALTHBOOST")) {
            buff = "HEALTH_BOOST";
        } else if (buff.equalsIgnoreCase("HUNGER")) {
            buff = "HUNGER";
        } else if (buff.equalsIgnoreCase("STRENGTH")) {
            buff = "INCREASE_DAMAGE";
        } else if (buff.equalsIgnoreCase("INVISIBILITY")) {
            buff = "INVISIBILITY";
        } else if (buff.equalsIgnoreCase("JUMPBOOST")) {
            buff = "JUMP";
        } else if (buff.equalsIgnoreCase("NIGHT_VISION")) {
            buff = "NIGHT_VISION";
        } else if (buff.equalsIgnoreCase("POISON")) {
            buff = "POISON";
        } else if (buff.equalsIgnoreCase("REGENERATION")) {
            buff = "REGENERATION";
        } else if (buff.equalsIgnoreCase("SATURATION")) {
            buff = "SATURATION";
        } else if (buff.equalsIgnoreCase("SLOWNESS")) {
            buff = "SLOW";
        } else if (buff.equalsIgnoreCase("SLOW_DIGGING")) {
            buff = "MINING_FATIGUE";
        } else if (buff.equalsIgnoreCase("SPEED")) {
            buff = "SPEED";
        } else if (buff.equalsIgnoreCase("WATERBREATHING")) {
            buff = "WATER_BREATHING";
        } else if (buff.equalsIgnoreCase("WEAKNESS")) {
            buff = "WEAKNESS";
        } else if (buff.equalsIgnoreCase("WITHER")) {
            buff = "WITHER";
        }
        return buff;

    }

    public static boolean isArmor(String s){
        if (s.toLowerCase().contains("leggings") ||
                s.toLowerCase().contains("helmet") ||
                s.toLowerCase().contains("chestplate")
                || s.toLowerCase().contains("boots")) {
            return true;
        }
        return false;
    }

}
