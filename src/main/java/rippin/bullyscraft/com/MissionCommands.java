package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rippin.bullyscraft.com.Configs.Config;
import rippin.bullyscraft.com.Configs.ConfigManager;
import rippin.bullyscraft.com.Configs.MissionsConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MissionCommands implements CommandExecutor {
    private FactionsMissions plugin;
    public MissionCommands(FactionsMissions plugin){
        this.plugin = plugin;
    }
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bullymission")){
            if (args.length == 0){
                if (sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "/bullymission create [mission]");
                sender.sendMessage(ChatColor.RED + "/bullymission setspawn [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission showspawns [mission]");
                sender.sendMessage(ChatColor.RED + "/bullymission setImportantEntity [mission] [mob]");
                sender.sendMessage(ChatColor.RED + "/bullymission setRegion [mission]");
                sender.sendMessage(ChatColor.RED + "/bullymission forceStart [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission forceEnd [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission setSchematicLoc [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission setMainPoint [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission reload");
                    sender.sendMessage(ChatColor.RED + "/bullymission clearRList | crl to clear replace mobs list.");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "/bullymission list | Lists active missions");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("setSpawn")) {
            if (sender.isOp()) {
            if (args.length == 2){
                if (sender instanceof Player) {
                Player p = (Player) sender;
                    if (MissionManager.isMission(args[1])){
                    Mission m = MissionManager.getMission(args[1]);
                    m.getSpawns().add(p.getLocation());
                    MissionManager.setMissionSpawnsToConfig(m);
                     p.sendMessage(Utilss.prefix + ChatColor.GREEN + "Spawn set for " + m.getName());

                }
                else{
                    sender.sendMessage(ChatColor.RED + " That is not a mission.");
                }
              }
                else {
                    sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                }
            }
            else {
                sender.sendMessage("Wrong arguments.");
            }
           }
           else {
                sender.sendMessage(ChatColor.RED + "No perms.");
            }
                return true;
            }
            else if (args[0].equalsIgnoreCase("spawnBlock")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                ItemStack item = new ItemStack(Material.WOOL);
                                ItemMeta meta = item.getItemMeta();
                                meta.setDisplayName(ChatColor.RED + m.getName());
                                meta.setLore(Arrays.asList(new String[]{ChatColor.RED + "Place to add a spawn"}));
                                item.setItemMeta(meta);

                                p.getInventory().addItem(item);
                            }
                            else{
                                sender.sendMessage(ChatColor.RED + " That is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("list")){
                if (args.length == 1){
                    MissionManager.printActiveMissionsInfo(sender);
                }
                else {
                    sender.sendMessage("Wrong arguments.");
                }
            }
            else if (args[0].equalsIgnoreCase("showspawns")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                for (Location loc : m.getSpawns()){
                                    p.sendBlockChange(loc, Material.WOOL, (byte) 14);
                                }
                                p.sendMessage(ChatColor.DARK_RED + "Spawns are showed now.");
                            }
                            else{
                                sender.sendMessage(ChatColor.RED + " That is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("spawnMob")){
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                if (sender.isOp()){
                    if (args.length == 2){
                        if (MobsManager.containsMob(args[1])){
                            Mob m = MobsManager.getMob(args[1]);
                            m.spawnMob(p.getEyeLocation(), null, "Spawn");
                            p.sendMessage(ChatColor.GREEN + args[1] + " ahas been spawned");
                        }
                    }
                }
              } else {
                    sender.sendMessage(ChatColor.RED + "No console.");
                }
            }

            else if (args[0].equalsIgnoreCase("setSchematicLoc")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                m.setSchematicLoc(p.getLocation());
                                p.sendMessage(Utilss.prefix + ChatColor.GREEN + "Schematic loc set for " + m.getName());

                            }
                            else{
                                sender.sendMessage(ChatColor.RED + " That is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("create")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (!MissionManager.isMission(args[1])){
                                MissionsConfig.getConfig().set("Missions." + args[1] + ".World", p.getLocation().getWorld().getName());
                                MissionsConfig.saveFile();
                                MissionsConfig.reload();
                                p.sendMessage(Utilss.prefix + ChatColor.GREEN + "Mission created named: " + args[1]);

                            }
                            else{
                                sender.sendMessage(ChatColor.RED + "This is already a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
            else if(args[0].equalsIgnoreCase("near")){
                if (args.length == 2){
                    if (sender instanceof  Player){
                        Player p = (Player) sender;
                        if (MissionManager.isMission(args[1])){
                            Mission m = MissionManager.getMission(args[1]);
                            if (!p.isOp()) {
                                if (m.getCustomEntitiesUUID().size() + m.getImportantEntitiesUUID().size() < 8 && m.getType() == MissionType.ELIMINATE){
                                    nearMissioCommand(m, p);
                                }
                            }
                            else {
                                nearMissioCommand(m, p);
                            }

                    }
                        else{
                            sender.sendMessage(ChatColor.RED + "This is not a mission.");
                        }
                  }
                    else {
                        sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("setMainPoint")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                MissionsConfig.getConfig().set("Missions." + args[1] + ".MainPoint", Utilss.serializeLoc(p.getLocation()));
                                MissionsConfig.saveFile();
                                MissionsConfig.reload();
                                p.sendMessage(Utilss.prefix + ChatColor.GREEN + "Set Main Point Location for Mission: " + args[1]);

                            }
                            else{
                                sender.sendMessage(ChatColor.RED + "This is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }

        else if (args[0].equalsIgnoreCase("setImportantEntity")){
            if (sender.isOp()) {
            if (args.length == 3){
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (MissionManager.isMission(args[1])){
                        Mission m = MissionManager.getMission(args[1]);
                        if (MobsManager.containsMob(args[2])){
                            m.getImportantEntities().put(args[2], p.getLocation());
                            MissionManager.setImportantEntityToConfig(m, args[2]);
                            p.sendMessage(Utilss.prefix + ChatColor.GREEN + "Important entity set for " + m.getName());
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + " That is not a mob.");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + " That is not a mission.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                }
            }
            else {
                sender.sendMessage("Wrong arguments.");
            }
           }
            else {
                sender.sendMessage(ChatColor.RED + "No perms.");
            }
                return true;
            }
         else if (args[0].equalsIgnoreCase("setRegion")){
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                Utilss.createRegion(p, m, plugin);
                                //add world here if not using schem loc
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + " That is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }

        else if (args[0].equalsIgnoreCase("forceStart")){
            if (sender.isOp()) {
                if (args.length == 2){
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (MissionManager.isQueuedMission(args[1])){
                            Mission m = MissionManager.getMission(args[1]);
                            m.start();
                            int x = ((Double) m.getMainPoint().getX()).intValue();
                            int y = ((Double) m.getMainPoint().getY()).intValue();
                            int z = ((Double) m.getMainPoint().getZ()).intValue();

                            Bukkit.broadcastMessage(Utilss.prefix + ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage")).replace("%name%", m.getName())
                                    .replace("%type%", m.getType().getValue()).replace("%coords%", " X: " + x + " Y: " + y + " Z: " + z));
                                p.sendMessage(Utilss.prefix + "Mission " + m.getName() + " has been force started.");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + " That is not a qeued mission.");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                    }
                }
                else {
                    sender.sendMessage("Wrong arguments.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "No perms.");
            }
                return true;
            }
    else if (args[0].equalsIgnoreCase("reload")){
                if (sender.isOp()){
                    if (args.length  == 1){
                        ConfigManager.reloadConfigs();
                        sender.sendMessage(ChatColor.GREEN + "Configs reloaded.");
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("clearRList") || args[0].equalsIgnoreCase("crl") ){
                if (sender.isOp()){
                    if (args.length  == 1){
                        MobsManager.clearReplaceEntUUIDs();
                        sender.sendMessage(ChatColor.GREEN + "List Cleared.");
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("forceEnd")){
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isActiveMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                m.end();
                                p.sendMessage(Utilss.prefix + "Mission " + m.getName() + " has been force ended.");
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + " That is not a mission.");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
                        }
                    }
                    else {
                        sender.sendMessage("Wrong arguments.");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms.");
                }
                return true;
            }
        return true;
        }
        else {
                sender.sendMessage(ChatColor.RED + "Wrong args.");
            }

        return false;
   }

  private void nearMissioCommand(Mission m, Player p){
          List<String> uuids = new ArrayList<String>();
          uuids.addAll(m.getCustomEntitiesUUID());
          uuids.addAll(m.getImportantEntitiesUUID());
          p.sendMessage(ChatColor.RED + "=================== Near entities ====================");
          for (String uuid : uuids){
              for (Entity e : m.getWorld().getEntities()){
                  if (e.getUniqueId().toString().equalsIgnoreCase(uuid)){
                      Location loc = e.getLocation();
                      p.sendMessage(ChatColor.GOLD+ "X:" + loc.getX() + " Y:" + loc.getY() + " Z:" + loc.getZ());
                  }
              }
      }
  }
}
