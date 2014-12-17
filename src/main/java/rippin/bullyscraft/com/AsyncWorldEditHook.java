package rippin.bullyscraft.com;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacerJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.worldedit.AsyncCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

import java.io.IOException;

/**
 * Prevent NOCLASSDEFEXCEPTION
 */
public class AsyncWorldEditHook {

    public static void AWEHookPaste(FactionsMissions plugin, final Mission m) throws WorldEditException, IOException, DataException{
        PluginMain asyncWorldEdit = (PluginMain) plugin.getAsyncWorldEdit();
        WorldEdit worldEdit = plugin.getWorldEdit().getWorldEdit();
        String playerName = plugin.getServer().getConsoleSender().getName();

        AsyncEditSessionFactory fac = (AsyncEditSessionFactory) worldEdit.getEditSessionFactory();
        BlockPlacer bPlacer = asyncWorldEdit.getBlockPlacer();
        final int jobID = bPlacer.getJobId(playerName);


        final IJobEntryListener stateListener = new IJobEntryListener() {
            public void jobStateChanged(BlockPlacerJobEntry job) {
                if (job.isTaskDone()) {
                    if (job.getJobId() == jobID)
                        System.out.println("done here");
                }
            }
        };

        final IBlockPlacerListener listener = new IBlockPlacerListener() {
            public void jobAdded(BlockPlacerJobEntry job) {
                job.addStateChangedListener(stateListener);

            }
            public void jobRemoved(BlockPlacerJobEntry job) {
                job.removeStateChangedListener(stateListener);
                if (job.isTaskDone()) {
                    if (jobID == job.getJobId()) {
                        m.spawnCustomEntities();
                        m.spawnImportantEntities();
                        m.setUUIDSToConfig();
                        System.out.println("Removed and DONE SPAWNED");
                    }
                }
            }
        };
        bPlacer.addListener(listener);
        AsyncEditSession session = (AsyncEditSession) fac.getEditSession(BukkitUtil.getLocalWorld(m.getSchematicLoc().getWorld()), -1);
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(m.getSchematic());
        AsyncCuboidClipboard clip =  new AsyncCuboidClipboard(playerName, cc);
        clip.paste(session, BukkitUtil.toVector(m.getSchematicLoc()), false);


    }

    public static void revertHookPaste(FactionsMissions plugin, final Mission m) throws WorldEditException, IOException, DataException{
        PluginMain asyncWorldEdit = (PluginMain) plugin.getAsyncWorldEdit();
        WorldEdit worldEdit = plugin.getWorldEdit().getWorldEdit();
        String playerName = plugin.getServer().getConsoleSender().getName();

        AsyncEditSessionFactory fac = (AsyncEditSessionFactory) worldEdit.getEditSessionFactory();
        BlockPlacer bPlacer = asyncWorldEdit.getBlockPlacer();
        final int jobID = bPlacer.getJobId(playerName);


        final IJobEntryListener stateListener = new IJobEntryListener() {
            public void jobStateChanged(BlockPlacerJobEntry job) {
                if (job.isTaskDone()) {
                    if (job.getJobId() == jobID)
                        System.out.println("done here");
                }
            }
        };

        final IBlockPlacerListener listener = new IBlockPlacerListener() {
            public void jobAdded(BlockPlacerJobEntry job) {
                job.addStateChangedListener(stateListener);

            }
            public void jobRemoved(BlockPlacerJobEntry job) {
                job.removeStateChangedListener(stateListener);
                if (job.isTaskDone()) {
                    if (jobID == job.getJobId()) {
                        System.out.println("Removed and DONE SPAWNED");
                    }
                }
            }
        };
        bPlacer.addListener(listener);
        AsyncEditSession session = (AsyncEditSession) fac.getEditSession(BukkitUtil.getLocalWorld(m.getSchematicLoc().getWorld()), -1);
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(m.getSchematic());
        AsyncCuboidClipboard clip =  new AsyncCuboidClipboard(playerName, cc);
        clip.paste(session, BukkitUtil.toVector(m.getSchematicLoc()), false);


    }



}
