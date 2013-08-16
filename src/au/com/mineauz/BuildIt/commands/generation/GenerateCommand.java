package au.com.mineauz.BuildIt.commands.generation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;

import au.com.mineauz.BuildIt.commands.ICommandDescription;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.pattern.RandomPattern;
import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.pattern.TiledPattern;

public class GenerateCommand implements ICommandDescription, Listener
{
	private HashMap<String, CommandDescription> mCommands = new HashMap<String, GenerateCommand.CommandDescription>();
	
	public void register(String name, String permission, IGeneratorCommandHandler handler, String description, Class<?>... argTypes)
	{
		CommandDescription desc = new CommandDescription();
		desc.name = name;
		desc.permission = Bukkit.getPluginManager().getPermission(permission);
		desc.handler = handler;
		desc.description = description;
		desc.argTypes = argTypes;
		
		mCommands.put("/" + name, desc);
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "This command can only be called by Players");
			return true;
		}

		if(mCommands.containsKey(label))
			onGeneratorCommand((Player)sender, mCommands.get(label), args);
		else
		{
			if(args.length == 0)
			{
				sender.sendMessage("Usage: /" + label + " <generator> ...");
				
				sender.sendMessage("No generator is specified. Valid generators:");
				sender.sendMessage(buildGeneratorString());
				
				return true;
			}
		
			if(mCommands.containsKey("/" + args[0]))
				onGeneratorCommand((Player)sender, mCommands.get("/" + args[0]), Arrays.copyOfRange(args, 1, args.length));
			else
			{
				sender.sendMessage("Usage: /" + label + " <generator> ...");
				
				sender.sendMessage("Unknown generator " + args[0] + ". Valid generators:");
				sender.sendMessage(buildGeneratorString());
				
				return true;
			}
		}
		
		return false;
	}
	private String buildGeneratorString()
	{
		String list = "";
		boolean light = true;
		
		for(CommandDescription cmd : mCommands.values())
		{
			if(!list.isEmpty())
				list += ChatColor.WHITE + ", ";
			
			if(light)
				list += ChatColor.WHITE + cmd.name;
			else
				list += ChatColor.GRAY + cmd.name;
			
			light = !light;
		}
		
		return list;
	}
	
	private void printUsage(Player player, CommandDescription cmd)
	{
		
	}
	public void onGeneratorCommand(Player sender, CommandDescription cmd, String[] args)
	{
		if(cmd.permission != null && !sender.hasPermission(cmd.permission))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that");
			return;
		}
		
		int readIndex = 0;
		
		Class<?>[] argTypes = cmd.argTypes;
		Object[] parsed = new Object[argTypes.length];
		
		for(int i = 0; i < argTypes.length; ++i)
		{
			Class<?> type = argTypes[i];
			
			if(type == int.class || type == Integer.class)
			{
				if(args.length <= readIndex)
				{
					sender.sendMessage(ChatColor.RED + "Expected a whole number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
				try
				{
					int number = Integer.parseInt(args[readIndex++]);
					parsed[i] = number;
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Expected a whole number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
			}
			else if(type == short.class || type == Short.class)
			{
				if(args.length <= readIndex)
				{
					sender.sendMessage(ChatColor.RED + "Expected a whole number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
				try
				{
					short number = Short.parseShort(args[readIndex++]);
					parsed[i] = number;
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Expected a whole number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
			}
			else if(type == double.class || type == Double.class)
			{
				if(args.length <= readIndex)
				{
					sender.sendMessage(ChatColor.RED + "Expected a number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
				try
				{
					double number = Double.parseDouble(args[readIndex++]);
					parsed[i] = number;
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Expected a number for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
			}
			else if(type == String.class)
			{
				if(args.length <= readIndex)
				{
					sender.sendMessage(ChatColor.RED + "Expected a string for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
				parsed[i] = args[readIndex++];
			}
			else if(type == Pattern.class)
			{
				if(args.length <= readIndex)
				{
					sender.sendMessage(ChatColor.RED + "Expected a pattern for argument " + (i+1));
					printUsage(sender, cmd);
					return;
				}
				
				try
				{
					Pattern pattern;
					
					if(args[readIndex].equalsIgnoreCase("tiled"))
					{
						if(args.length <= readIndex + 1)
						{
							sender.sendMessage(ChatColor.RED + "Expected a pattern for argument " + (i+1) + " after tiled");
							printUsage(sender, cmd);
							return;
						}
						
						pattern = TiledPattern.parse(args[readIndex + 1]);
						readIndex += 2;
					}
					else
					{
						if(args[readIndex].contains(","))
							pattern = RandomPattern.parse(args[readIndex]);
						else
							pattern = SingleTypePattern.parse(args[readIndex]);
						
						readIndex++;
					}
					
					parsed[i] = pattern;
				}
				catch(IllegalArgumentException e)
				{
					sender.sendMessage(ChatColor.RED + "Expected a pattern for argument " + (i+1));
					sender.sendMessage(ChatColor.RED + e.getMessage());
					printUsage(sender, cmd);
					return;
				}
			}
		}
		
		cmd.handler.onCommand(sender, parsed);
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, Command command, String alias, String[] args )
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public String getPermission()
	{
		return null;
	}

	@Override
	public String[] getAliases()
	{
		return mCommands.keySet().toArray(new String[mCommands.size()]);
	}

	@Override
	public String getUsage()
	{
		return "";
	}

	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	private void onCommandEntered(PlayerCommandPreprocessEvent event)
	{
		String name;
		String[] args;
		
		if(event.getMessage().contains(" "))
		{
			name = event.getMessage().substring(1, event.getMessage().indexOf(' '));
			args = event.getMessage().substring(event.getMessage().indexOf(' ') + 1).split(" ");
		}
		else
		{
			name = event.getMessage().substring(1);
			args = new String[0];
		}
		
		PluginCommand command = Bukkit.getPluginCommand("/generate");
		
		if(mCommands.containsKey(name))
		{
			onCommand(event.getPlayer(), command, name, args);
			event.setCancelled(true);
		}
	}
	
	private static class CommandDescription
	{
		public String name;
		public String description;
		public Permission permission;
		public Class<?>[] argTypes;
		public IGeneratorCommandHandler handler;
	}
}
