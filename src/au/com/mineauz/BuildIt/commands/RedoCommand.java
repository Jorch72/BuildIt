package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;

public class RedoCommand implements ICommandDescription
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
		
		if(BuildIt.instance.getUndoManager().canRedo(player))
		{
			BuildIt.instance.getUndoManager().redoStep(player);
			sender.sendMessage("Redo successful");
		}
		else
			sender.sendMessage("Nothing more to redo");
		
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
		return "Redoes the last thing that was undone";
	}

	@Override
	public String getPermission()
	{
		return "buildit.undo";
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
