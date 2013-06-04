package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.RandomPattern;
import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.tasks.BlockChangeTask;

public class SetCommand implements ICommandDescription
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 1 && args.length != 2)
			return false;
		
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Can only be called by a player");
			return true;
		}
		
		Player player = (Player)sender;
		
		Selection sel = BuildIt.instance.getSelectionManager().getSelection(player);
		if(sel == null)
		{
			sender.sendMessage(ChatColor.RED + "You have nothing selected");
			return true;
		}
		
		try
		{
			Pattern pattern;
			
			if(args[0].equalsIgnoreCase("tiled"))
				pattern = TiledPattern.parse(args[1]);
			else
			{
				if(args[0].contains(","))
					pattern = RandomPattern.parse(args[0]);
				else
					pattern = SingleTypePattern.parse(args[0]);
			}
			
			BlockChangeTask task = new BlockChangeTask(sel, pattern);
			if(!BuildIt.instance.getUndoManager().addStep(sel, player))
				sender.sendMessage(ChatColor.GOLD + "WARNING: The selection is too large to be undone");
			BuildIt.instance.getTaskRunner().submit(task);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, Command command, String alias, String[] args )
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Sets the contents of a selection to something";
	}

	@Override
	public String getPermission()
	{
		return "buildit.set";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return "/<command> {pattern}";
	}

}
