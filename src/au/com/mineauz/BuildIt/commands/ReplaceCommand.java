package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.RandomPattern;
import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.tasks.BlockChangeTask;
import au.com.mineauz.BuildIt.types.BlockType;

public class ReplaceCommand implements ICommandDescription
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 2 && args.length != 3)
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
			Mask mask = new Mask(); 
			
			// Parse the mask
			String[] replaceIDs = args[0].split(",");
			for(String id : replaceIDs)
				mask.add(BlockType.parse(id));
			
			// Parse pattern
			
			if(args[1].equalsIgnoreCase("tiled"))
				pattern = TiledPattern.parse(args[2]);
			else
			{
				if(args[1].contains(","))
					pattern = RandomPattern.parse(args[1]);
				else
					pattern = SingleTypePattern.parse(args[1]);
			}
			
			BlockChangeTask task = new BlockChangeTask(sel, pattern, mask);
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
		return "Replaces the contents of the selection with something else";
	}

	@Override
	public String getPermission()
	{
		return "buildit.replace";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return "/<command> {mask} {pattern}";
	}

}
