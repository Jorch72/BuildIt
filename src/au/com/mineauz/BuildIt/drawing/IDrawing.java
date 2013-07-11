package au.com.mineauz.BuildIt.drawing;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.selection.Selection;

public interface IDrawing
{
	public boolean addPoint(BlockVector point, Player who);
	
	public boolean isDefined();
	
	public Selection makeSelection(Player owner);
	
	public void draw(Pattern pattern, World world);
}
