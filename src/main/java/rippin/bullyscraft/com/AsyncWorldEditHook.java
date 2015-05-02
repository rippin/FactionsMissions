package rippin.bullyscraft.com;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

import java.io.IOException;

/**
 * Prevent NOCLASSDEFEXCEPTION
 */
public class AsyncWorldEditHook {

    @SuppressWarnings("deprecation")
    public static void AWEHookPaste(FactionsMissions plugin, final Mission m) throws WorldEditException, IOException, DataException {
        AsyncWorldEditMain asyncWorldEdit = (AsyncWorldEditMain) plugin.getAsyncWorldEdit();
        WorldEdit worldEdit = plugin.getWorldEdit().getWorldEdit();
        final String playerName = plugin.getServer().getConsoleSender().getName();

        AsyncEditSessionFactory fac = (AsyncEditSessionFactory) worldEdit.getEditSessionFactory();
        BlockPlacer bPlacer = asyncWorldEdit.getBlockPlacer();

      final IJobEntryListener stateListener = new IJobEntryListener() {
            public void jobStateChanged(JobEntry job) {
                if (job.isTaskDone()) {
                    PlayerEntry playerEntry = job.getPlayer();
                    if (playerEntry.isUnknown()){
                        if (job.getName().equalsIgnoreCase("place")) {
                       m.spawnCustomEntities();
                       m.spawnImportantEntities();
                       m.setUUIDSToConfig();
                        }
                    }
                }
            }
        };

        final IBlockPlacerListener listener = new IBlockPlacerListener() {
            public void jobAdded(JobEntry job) {
                job.addStateChangedListener(stateListener);

            }
            public void jobRemoved(JobEntry job) {
                job.removeStateChangedListener(stateListener);
            }
        };
        ThreadSafeEditSession session = (ThreadSafeEditSession) fac.getEditSession(new BukkitWorld(m.getSchematicLoc().getWorld()), -1);
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(m.getSchematic());
        AsyncCuboidClipboard clip =  new AsyncCuboidClipboard(session.getPlayer(), cc);
        bPlacer.addListener(listener);
        clip.paste(session, BukkitUtil.toVector(m.getSchematicLoc()), false);


    }

   @SuppressWarnings("deprecation")
    public static void revertHookPaste(FactionsMissions plugin, final Mission m) throws WorldEditException, IOException, DataException {
        AsyncWorldEditMain asyncWorldEdit = (AsyncWorldEditMain) plugin.getAsyncWorldEdit();
        WorldEdit worldEdit = plugin.getWorldEdit().getWorldEdit();


        AsyncEditSessionFactory fac = (AsyncEditSessionFactory) worldEdit.getEditSessionFactory();
        BlockPlacer bPlacer = asyncWorldEdit.getBlockPlacer();

        final IJobEntryListener stateListener = new IJobEntryListener() {
            public void jobStateChanged(JobEntry job) {
                if (job.isTaskDone()) {
                    PlayerEntry playerEntry = job.getPlayer();
                    if (playerEntry.isUnknown()){
                        if (job.getPlayer().isUnknown()) {
                          System.out.println("Placed");
                           MissionManager.getRevertMissions().remove(m.getName());
                        }
                    }
                }
            }
        };

        final IBlockPlacerListener listener = new IBlockPlacerListener() {
            public void jobAdded(JobEntry job) {
                job.addStateChangedListener(stateListener);

            }
            public void jobRemoved(JobEntry job) {
                job.removeStateChangedListener(stateListener);
            }
        };
        ThreadSafeEditSession session = (ThreadSafeEditSession) fac.getEditSession(new BukkitWorld(m.getSchematicLoc().getWorld()), -1);
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(m.getRevertSchematic());
        AsyncCuboidClipboard clip =  new AsyncCuboidClipboard(session.getPlayer(), cc);
        bPlacer.addListener(listener);
        clip.paste(session, BukkitUtil.toVector(m.getSchematicLoc()), false);


    }



}
