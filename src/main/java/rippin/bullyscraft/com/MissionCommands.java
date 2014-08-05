package rippin.bullyscraft.com;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import rippin.bullyscraft.com.FactionsMissions;
import rippin.bullyscraft.com.MissionManager;

public class MissionCommands implements CommandExecutor {
    private FactionsMissions plugin;
    public MissionCommands(FactionsMissions plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setmissionspawns")){
            if (args.length == 1){
                if (MissionManager.isMission(args[0])){
                    //do the thang
                }
                else{
                    sender.sendMessage(ChatColor.RED + " That is not a mission.");
                }
            }
            else {
                sender.sendMessage("Wrong arguments.");
            }
        }

        return false;
    }
}
