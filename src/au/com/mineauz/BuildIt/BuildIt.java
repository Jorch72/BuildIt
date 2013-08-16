package au.com.mineauz.BuildIt;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.InvalidPluginException;
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
import au.com.mineauz.BuildIt.commands.generation.GenerateCommand;
import au.com.mineauz.BuildIt.commands.generation.GeneratorCommand;
import au.com.mineauz.BuildIt.commands.generation.IGeneratorCommandHandler;
import au.com.mineauz.BuildIt.drawing.DrawingManager;
import au.com.mineauz.BuildIt.generation.HollowSphereGenerator;
import au.com.mineauz.BuildIt.generation.IGenerator;
import au.com.mineauz.BuildIt.generation.SphereGenerator;
import au.com.mineauz.BuildIt.natives.NativeManager;
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
	
	private GenerateCommand mGenerateCommand;
	
	public BuildIt()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		try
		{
			NativeManager.initialize();
		}
		catch(InvalidPluginException e)
		{
			getLogger().severe("This version of BuiltIt (" + getDescription().getVersion() + ") is not compatable with Craftbukkit " + NativeManager.getCraftBukkitVersion());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
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
		
		registerGenerators();
		
	}
	
	@Override
	public void onDisable()
	{
		mTaskRunner.cancelAll();
		
		mSelections.removeAllSelections();
		mTaskRunner.forceAll();
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
	
	private void registerGenerator(Class<? extends IGenerator> generator)
	{
		for(Class<?> clazz : generator.getClasses())
		{
			if(clazz.isAnnotationPresent(GeneratorCommand.class) && IGeneratorCommandHandler.class.isAssignableFrom(clazz))
			{
				IGeneratorCommandHandler handler;
				
				try
				{
					handler = (IGeneratorCommandHandler)clazz.newInstance();
					GeneratorCommand cmdInfo = clazz.getAnnotation(GeneratorCommand.class);
					mGenerateCommand.register(cmdInfo.name(), cmdInfo.perm(), handler, cmdInfo.description(), cmdInfo.args());
				}
				catch(Exception e)
				{
					throw new RuntimeException("Unable to register generator command handler for " + generator.getSimpleName() + ".", e);
				}
			}
		}
	}
	private void registerGenerators()
	{
		mGenerateCommand = new GenerateCommand();
		Bukkit.getPluginManager().registerEvents(mGenerateCommand, this);
		registerGenerator(SphereGenerator.class);
		registerGenerator(HollowSphereGenerator.class);
		
		registerCommand("/generate", mGenerateCommand);
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command,String label, String[] args )
	{
		// TODO Auto-generated method stub
		return super.onCommand(sender, command, label, args);
	}
	
	public SelectionManager getSelectionManager() { return mSelections; }
	public DrawingManager getDrawingManager() { return mDrawings; }
	public IncrementalTaskRunner getTaskRunner() { return mTaskRunner; }
	public UndoManager getUndoManager() { return mUndoManager; }
	public ClipboardManager getClipboardManager() { return mClipboardManager; }
}
