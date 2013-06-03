package au.com.mineauz.BuildIt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BlockChangeTask;
import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.Snapshot;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.RandomPattern;
import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.types.BlockType;

public class ReplaceCommand implements CommandExecutor
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
			BuildIt.instance.getUndoManager().addStep(Snapshot.create(sel), player);
			BuildIt.instance.getTaskRunner().submit(task);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		
		return true;
	}

}
