package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.tasks.NaturalizeTask;
import au.com.mineauz.BuildIt.types.BlockType;

public class NaturalizeCommand implements ICommandDescription
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 0 && args.length != 1)
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
			
			Mask mask = new Mask(); 
			
			if(args.length == 1)
			{
				// Parse the mask
				String[] replaceIDs = args[0].split(",");
				for(String id : replaceIDs)
					mask.add(BlockType.parse(id));
			}
			
			NaturalizeTask task = new NaturalizeTask(sel, mask);

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
		return "Makes the contents of the selection seems more natural by putting grass on top, dirt under that, and stone under that";
	}

	@Override
	public String getPermission()
	{
		return "buildit.naturalize";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return "/<command>";
	}

}
