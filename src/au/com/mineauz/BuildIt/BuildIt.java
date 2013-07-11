package au.com.mineauz.BuildIt;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.BuildIt.commands.ChunkCommand;
import au.com.mineauz.BuildIt.commands.CopyCommand;
import au.com.mineauz.BuildIt.commands.CutCommand;
import au.com.mineauz.BuildIt.commands.ExpandCommand;
import au.com.mineauz.BuildIt.commands.ICommandDescription;
import au.com.mineauz.BuildIt.commands.NaturalizeCommand;
import au.com.mineauz.BuildIt.commands.OffsetCommand;
import au.com.mineauz.BuildIt.commands.PasteCommand;
import au.com.mineauz.BuildIt.commands.RedoCommand;
import au.com.mineauz.BuildIt.commands.RegenCommand;
import au.com.mineauz.BuildIt.commands.ReplaceCommand;
import au.com.mineauz.BuildIt.commands.SetCommand;
import au.com.mineauz.BuildIt.commands.UndoCommand;
import au.com.mineauz.BuildIt.commands.WandCommand;
import au.com.mineauz.BuildIt.commands.generation.SphereCommand;
import au.com.mineauz.BuildIt.drawing.DrawingManager;
import au.com.mineauz.BuildIt.selection.SelectionManager;
import au.com.mineauz.BuildIt.tasks.IncrementalTaskRunner;

public class BuildIt extends JavaPlugin
{
	public static BuildIt instance;
	
	private SelectionManager mSelections;
	private DrawingManager mDrawings;
	@SuppressWarnings( "unused" )
	private WandManager mWandManager;
	
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
		mSelections = new SelectionManager();
		mDrawings = new DrawingManager();
		mWandManager = new WandManager(this);
		mTaskRunner = new IncrementalTaskRunner();
		mUndoManager = new UndoManager();
		mClipboardManager = new ClipboardManager();
		
		registerCommand("/wand", new WandCommand());
		registerCommand("/set", new SetCommand());
		registerCommand("/naturalize", new NaturalizeCommand());
		registerCommand("/undo", new UndoCommand());
		registerCommand("/redo", new RedoCommand());
		registerCommand("/offset", new OffsetCommand());
		registerCommand("/replace", new ReplaceCommand());
		registerCommand("/cut", new CutCommand());
		registerCommand("/copy", new CopyCommand());
		registerCommand("/paste", new PasteCommand());
		registerCommand("/regen", new RegenCommand());
		registerCommand("/expand", new ExpandCommand());
		registerCommand("/chunk", new ChunkCommand());
		
		registerCommand("/sphere", new SphereCommand());
		
	}
	
	@Override
	public void onDisable()
	{
		mTaskRunner.cancelAll();
	}
	
	private void registerCommand(String name, ICommandDescription command)
	{
		PluginCommand cmd = getCommand(name);
		Validate.notNull(cmd);
		
		if(command.getAliases() == null)
			cmd.setAliases(new ArrayList<String>());
		else
			cmd.setAliases(Arrays.asList(command.getAliases()));
		
		cmd.setDescription(command.getDescription());
		cmd.setPermission(command.getPermission());
		cmd.setUsage(command.getUsage());
		
		cmd.setExecutor(command);
		cmd.setTabCompleter(command);
	}
	
	public SelectionManager getSelectionManager() { return mSelections; }
	public DrawingManager getDrawingManager() { return mDrawings; }
	public IncrementalTaskRunner getTaskRunner() { return mTaskRunner; }
	public UndoManager getUndoManager() { return mUndoManager; }
	public ClipboardManager getClipboardManager() { return mClipboardManager; }
}
