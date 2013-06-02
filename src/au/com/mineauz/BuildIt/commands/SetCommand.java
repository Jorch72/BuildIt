package au.com.mineauz.BuildIt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BlockChangeTask;
import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.Snapshot;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;
import au.com.mineauz.BuildIt.selection.Selection;

public class SetCommand implements CommandExecutor
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 1)
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
			Pattern pattern = TiledPattern.parse(args[0]);
			
			BlockChangeTask task = new BlockChangeTask(sel, pattern);
			BuildIt.instance.getUndoManager().addStep(Snapshot.create(sel), player);
			BuildIt.instance.getBlockChanger().submit(task, player);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		
		return true;
	}

}
