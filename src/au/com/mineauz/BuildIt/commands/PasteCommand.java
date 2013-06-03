package au.com.mineauz.BuildIt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;

public class PasteCommand implements CommandExecutor
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
		
		boolean air = true;
		
		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("-a"))
				air = false;
			else
			{
				sender.sendMessage(ChatColor.RED + "Unknown option " + args[0]);
				return true;
			}
		}
		
		Player player = (Player)sender;
		
		BuildIt.instance.getClipboardManager().paste(player.getLocation(), air, player);
		sender.sendMessage("Selection pasted at your location");
		
		return true;
	}

}
