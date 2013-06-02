package au.com.mineauz.BuildIt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;

public class UndoCommand implements CommandExecutor
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 0)
			return false;
		
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Can only be called by a player");
			return true;
		}
		
		Player player = (Player)sender;
		
		if(BuildIt.instance.getUndoManager().canUndo(player))
		{
			BuildIt.instance.getUndoManager().undoStep(player);
			sender.sendMessage("Undo successful");
		}
		else
			sender.sendMessage("Nothing more to undo");
		
		return true;
	}

}
