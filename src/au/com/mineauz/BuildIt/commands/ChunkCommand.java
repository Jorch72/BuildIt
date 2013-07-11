package au.com.mineauz.BuildIt.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.mode.CuboidSelection;

public class ChunkCommand implements ICommandDescription
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
		
		int chunkX = sel.getMinPoint().getBlockX() >> 4;
		int chunkZ = sel.getMinPoint().getBlockZ() >> 4;
		
		int chunkXMax = sel.getMaxPoint().getBlockX() >> 4;
		int chunkZMax = sel.getMaxPoint().getBlockZ() >> 4;
		
		sel = new CuboidSelection(sel.getWorld(), new BlockVector(chunkX << 4, 0, chunkZ << 4), new BlockVector((chunkXMax + 1) * 16 - 1, sel.getWorld().getMaxHeight() - 1, (chunkZMax + 1) * 16 - 1));
		int chunks = (chunkXMax - chunkX + 1) * (chunkZMax - chunkZ + 1);
		
		sender.sendMessage("Selection expanded to encompass " + chunks + " Chunks");
		
		BuildIt.instance.getSelectionManager().setSelection(player, sel);
		
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
		return "Selects entire chunks";
	}

	@Override
	public String getPermission()
	{
		return "buildit.chunk";
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
