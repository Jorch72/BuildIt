package au.com.mineauz.BuildIt;

import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.BuildIt.commands.SetCommand;
import au.com.mineauz.BuildIt.commands.WandCommand;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class BuildIt extends JavaPlugin
{
	public static BuildIt instance;
	
	private SelectionManager mSelections;
	private BlockChanger mBlockChanger;
	
	public BuildIt()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		mSelections = new SelectionManager(this);
		mBlockChanger = new BlockChanger();
		
		getCommand("/wand").setExecutor(new WandCommand());
		getCommand("/set").setExecutor(new SetCommand());
	}
	
	@Override
	public void onDisable()
	{
		mBlockChanger.cancelAll();
	}
	
	public SelectionManager getSelectionManager() { return mSelections; }
	public BlockChanger getBlockChanger() { return mBlockChanger; }
}
