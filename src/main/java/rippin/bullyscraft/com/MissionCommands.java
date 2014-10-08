package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rippin.bullyscraft.com.Configs.Config;
import rippin.bullyscraft.com.Configs.ConfigManager;


public class MissionCommands implements CommandExecutor {
    private FactionsMissions plugin;
    public MissionCommands(FactionsMissions plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bullymission")){
            if (args.length == 0){
                if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "/bullymission setspawn [mission]");
                sender.sendMessage(ChatColor.RED + "/bullymission setImportantEntity [mission] [mob]");
                sender.sendMessage(ChatColor.RED + "/bullymission setRegion [mission]");
                sender.sendMessage(ChatColor.RED + "/bullymission forceStartMission [mission]");
                    sender.sendMessage(ChatColor.RED + "/bullymission setSchematicLoc [mission]");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "No perms");
                }
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
                     p.sendMessage(Utils.prefix + ChatColor.GREEN + "Spawn set for " + m.getName());

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
           }

            else if (args[0].equalsIgnoreCase("setSchematicLoc")) {
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                m.setSchematicLoc(p.getLocation());
                                p.sendMessage(Utils.prefix + ChatColor.GREEN + "Schematic loc set for " + m.getName());

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
                            p.sendMessage(Utils.prefix + ChatColor.GREEN + "Important entity set for " + m.getName());
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
        }
         else if (args[0].equalsIgnoreCase("setRegion")){
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                Utils.createRegion(p,m,plugin);
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
            }

        else if (args[0].equalsIgnoreCase("forceStartMission")){
            if (sender.isOp()) {
                if (args.length == 2){
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (MissionManager.isQueuedMission(args[1])){
                            Mission m = MissionManager.getMission(args[1]);
                            m.start();
                            double x = m.getSchematicLoc().getX();
                            double y = m.getSchematicLoc().getY();
                            double z = m.getSchematicLoc().getZ();
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage")).replace("%name%", m.getName())
                                    .replace("%type%", m.getType().getValue()).replace("%coords%", "X: " + x + "Y: " + y + "Z: " + z));
                                p.sendMessage(Utils.prefix + "Mission " + m.getName() + " has been force started.");
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
            else if (args[0].equalsIgnoreCase("forceEndMission")){
                if (sender.isOp()) {
                    if (args.length == 2){
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (MissionManager.isActiveMission(args[1])){
                                Mission m = MissionManager.getMission(args[1]);
                                m.end();
                                p.sendMessage(Utils.prefix + "Mission " + m.getName() + " has been force ended.");
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
            }
        return true;
        }
        else {
                sender.sendMessage(ChatColor.RED + "Wrong args.");
            }

        return false;
   }
}
