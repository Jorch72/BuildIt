package au.com.mineauz.BuildIt;

import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.BuildIt.commands.CopyCommand;
import au.com.mineauz.BuildIt.commands.CutCommand;
import au.com.mineauz.BuildIt.commands.OffsetCommand;
import au.com.mineauz.BuildIt.commands.PasteCommand;
import au.com.mineauz.BuildIt.commands.RedoCommand;
import au.com.mineauz.BuildIt.commands.ReplaceCommand;
import au.com.mineauz.BuildIt.commands.SetCommand;
import au.com.mineauz.BuildIt.commands.UndoCommand;
import au.com.mineauz.BuildIt.commands.WandCommand;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class BuildIt extends JavaPlugin
{
	public static BuildIt instance;
	
	private SelectionManager mSelections;
	private IncrementalTaskRunner mTaskRunner;
	private UndoManager mUndoManager;
	private ClipboardManager mClipboardManager;
	
	public BuildIt()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		mSelections = new SelectionManager(this);
		mTaskRunner = new IncrementalTaskRunner();
		mUndoManager = new UndoManager();
		mClipboardManager = new ClipboardManager();
		
		getCommand("/wand").setExecutor(new WandCommand());
		getCommand("/set").setExecutor(new SetCommand());
		getCommand("/undo").setExecutor(new UndoCommand());
		getCommand("/redo").setExecutor(new RedoCommand());
		getCommand("/offset").setExecutor(new OffsetCommand());
		getCommand("/replace").setExecutor(new ReplaceCommand());
		getCommand("/cut").setExecutor(new CutCommand());
		getCommand("/copy").setExecutor(new CopyCommand());
		getCommand("/paste").setExecutor(new PasteCommand());
	}
	
	@Override
	public void onDisable()
	{
		mTaskRunner.cancelAll();
	}
	
	public SelectionManager getSelectionManager() { return mSelections; }
	public IncrementalTaskRunner getTaskRunner() { return mTaskRunner; }
	public UndoManager getUndoManager() { return mUndoManager; }
	public ClipboardManager getClipboardManager() { return mClipboardManager; }
}
