package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.selection.Selection;

public class ExpandCommand implements ICommandDescription
{
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(args.length != 2)
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
		
		BlockFace direction;
		int amount;
		
		// Parse the amount
		try
		{
			amount = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e)
		{
			sender.sendMessage(ChatColor.RED + "Expected integer as first argument");
			return true;
		}
		
		// Parse the direction
		if(args[1].equalsIgnoreCase("north"))
			direction = BlockFace.NORTH;
		else if(args[1].equalsIgnoreCase("south"))
			direction = BlockFace.SOUTH;
		else if(args[1].equalsIgnoreCase("east"))
			direction = BlockFace.EAST;
		else if(args[1].equalsIgnoreCase("west"))
			direction = BlockFace.WEST;
		else if(args[1].equalsIgnoreCase("up"))
			direction = BlockFace.UP;
		else if(args[1].equalsIgnoreCase("down"))
			direction = BlockFace.DOWN;
		else if(args[1].equalsIgnoreCase("left"))
		{
			double yaw = player.getEyeLocation().getYaw();

			if(yaw >= -45 && yaw <= 45) // south
				direction = BlockFace.EAST;
			else if(yaw > 45 && yaw < 135) // west
				direction = BlockFace.SOUTH;
			else if(yaw > -135 && yaw < -45) // east
				direction = BlockFace.NORTH;
			else // north
				direction = BlockFace.WEST;
		}
		else if(args[1].equalsIgnoreCase("right"))
		{
			double yaw = player.getEyeLocation().getYaw();

			if(yaw >= -45 && yaw <= 45) // south
				direction = BlockFace.WEST;
			else if(yaw > 45 && yaw < 135) // west
				direction = BlockFace.NORTH;
			else if(yaw > -135 && yaw < -45) // east
				direction = BlockFace.SOUTH;
			else // north
				direction = BlockFace.EAST;
		}
		else if(args[1].equalsIgnoreCase("forward"))
		{
			double yaw = player.getEyeLocation().getYaw();

			if(yaw >= -45 && yaw <= 45) // south
				direction = BlockFace.SOUTH;
			else if(yaw > 45 && yaw < 135) // west
				direction = BlockFace.WEST;
			else if(yaw > -135 && yaw < -45) // east
				direction = BlockFace.EAST;
			else // north
				direction = BlockFace.NORTH;
		}
		else if(args[1].equalsIgnoreCase("back"))
		{
			double yaw = player.getEyeLocation().getYaw();

			if(yaw >= -45 && yaw <= 45) // south
				direction = BlockFace.NORTH;
			else if(yaw > 45 && yaw < 135) // west
				direction = BlockFace.EAST;
			else if(yaw > -135 && yaw < -45) // east
				direction = BlockFace.WEST;
			else // north
				direction = BlockFace.SOUTH;
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Expected a direction as second argument");
			return true;
		}
		
		sel.expand(direction, amount);
		sender.sendMessage("Selection expanded " + amount + " units " + direction.toString().toLowerCase().replaceAll("_", " "));
		
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
		return "Expands the selection";
	}

	@Override
	public String getPermission()
	{
		return "buildit.expand";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return "/<command> {amount} {direction}";
	}

}
