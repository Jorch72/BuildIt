package au.com.mineauz.BuildIt.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface ICommandDescription extends CommandExecutor, TabCompleter
{
	public String getDescription();
	public String getPermission();
	public String[] getAliases();
	
	public String getUsage();
}
