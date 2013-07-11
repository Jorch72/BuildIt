package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.tasks.RegenerateTask;

public class RegenCommand implements ICommandDescription
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
		
		Selection sel = BuildIt.instance.getSelectionManager().getSelection(player);
		if(sel == null)
		{
			sender.sendMessage(ChatColor.RED + "You have nothing selected");
			return true;
		}
		
		RegenerateTask task = new RegenerateTask(sel);
		if(!BuildIt.instance.getUndoManager().addStep(sel, player))
			sender.sendMessage(ChatColor.GOLD + "WARNING: The selection is too large to be undone");
		BuildIt.instance.getTaskRunner().submit(task);
		
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
		return "Regenerates the contents of the selection";
	}

	@Override
	public String getPermission()
	{
		return "buildit.regen";
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
