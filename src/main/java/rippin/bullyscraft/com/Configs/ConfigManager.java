package rippin.bullyscraft.com.Configs;


import rippin.bullyscraft.com.FactionsMissions;

import java.io.File;

public class ConfigManager {

    public static void generateConfigs(FactionsMissions plugin){
    Config.setUp(plugin);
    MissionsConfig.setUp(plugin);
    MobsConfig.setUp(plugin);
    new File(plugin.getDataFolder() + File.separator + "schematics" + File.separator).mkdir();
    }
    public static void reloadConfigs(){
        Config.reload();
        MissionsConfig.reload();
        MobsConfig.reload();
    }
}
