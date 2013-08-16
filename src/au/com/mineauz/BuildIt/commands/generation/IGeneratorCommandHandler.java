package au.com.mineauz.BuildIt.commands.generation;

import org.bukkit.entity.Player;

public interface IGeneratorCommandHandler
{
	public boolean onCommand(Player player, Object... arguments);
}
