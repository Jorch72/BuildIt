package au.com.mineauz.BuildIt;

import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.BuildIt.commands.RedoCommand;
import au.com.mineauz.BuildIt.commands.SetCommand;
import au.com.mineauz.BuildIt.commands.UndoCommand;
import au.com.mineauz.BuildIt.commands.WandCommand;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class BuildIt extends JavaPlugin
{
	public static BuildIt instance;
	
	private SelectionManager mSelections;
	private BlockChanger mBlockChanger;
	private UndoManager mUndoManager;
	
	public BuildIt()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		mSelections = new SelectionManager(this);
		mBlockChanger = new BlockChanger();
		mUndoManager = new UndoManager();
		
		getCommand("/wand").setExecutor(new WandCommand());
		getCommand("/set").setExecutor(new SetCommand());
		getCommand("/undo").setExecutor(new UndoCommand());
		getCommand("/redo").setExecutor(new RedoCommand());
		
		
	}
	
	@Override
	public void onDisable()
	{
		mBlockChanger.cancelAll();
	}
	
	public SelectionManager getSelectionManager() { return mSelections; }
	public BlockChanger getBlockChanger() { return mBlockChanger; }
	public UndoManager getUndoManager() { return mUndoManager; }
}
