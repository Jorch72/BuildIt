package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.selection.SelectionManager;

public class WandCommand implements ICommandDescription
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Can only be called by a player");
			return true;
		}
		
		Player player = (Player)sender;

		// Remove existing wand
		boolean had = false;
		for(int i = 0; i < player.getInventory().getSize(); ++i)
		{
			if(SelectionManager.isWand(player.getInventory().getItem(i)))
			{
				player.getInventory().clear(i);
				had = true;
			}
		}
		
		if(!had)
			// Give them a wand
			player.getInventory().addItem(SelectionManager.makeWand());

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
		return "Gives you the wand, or takes it from you";
	}

	@Override
	public String getPermission()
	{
		return "buildit.wand";
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
