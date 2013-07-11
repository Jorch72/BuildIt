package au.com.mineauz.BuildIt.commands.generation;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.commands.ICommandDescription;
import au.com.mineauz.BuildIt.generation.SphereGenerator;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.RandomPattern;
import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;
import au.com.mineauz.BuildIt.selection.mode.SphereSelection;
import au.com.mineauz.BuildIt.tasks.GeneratorTask;

public class SphereCommand implements ICommandDescription
{

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can use this");
			return true;
		}
		
		if(args.length != 2 && args.length != 3)
			return false;
		
		int radius;
		
		try
		{
			radius = Integer.parseInt(args[0]);
			if(radius <= 0)
			{
				sender.sendMessage(ChatColor.RED + "Radius must be greater than 0");
				return true;
			}
		}
		catch(NumberFormatException e)
		{
			sender.sendMessage(ChatColor.RED + "Expected radius as argument 1");
			return true;
		}
		
		// TODO: Max radius check
		
		Pattern pattern;
		
		if(args[1].equalsIgnoreCase("tiled"))
			pattern = TiledPattern.parse(args[2]);
		else
		{
			if(args[1].contains(","))
				pattern = RandomPattern.parse(args[1]);
			else
				pattern = SingleTypePattern.parse(args[1]);
		}
		
		Location loc = new Location(((Player)sender).getWorld(), ((Player)sender).getLocation().getBlockX(), ((Player)sender).getLocation().getBlockY(), ((Player)sender).getLocation().getBlockZ());
		
		BuildIt.instance.getUndoManager().addStep(new SphereSelection(((Player)sender).getWorld(), loc.toVector().toBlockVector(), radius), (Player)sender);
		BuildIt.instance.getTaskRunner().submit(new GeneratorTask(new SphereGenerator(loc, radius), pattern));
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender arg0, Command arg1, String arg2, String[] arg3 )
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "Generates a sphere at your location";
	}

	@Override
	public String getPermission()
	{
		return "buildit.gen.sphere";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getUsage()
	{
		return "/<command> {radius} {pattern}";
	}

}
