package au.com.mineauz.BuildIt.selection;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;

public interface Selection extends Iterable<BlockVector>, Cloneable
{
	public BlockVector getMinPoint();
	
	public BlockVector getMaxPoint();
	
	public World getWorld();
	public void setWorld(World world);
	
	/**
	 * This is called to do a fine check that the point is in the selection. By this point, it is already known to be within the bounding box of the selection
	 */
	public boolean isInSelection(BlockVector pos);
	
	public boolean addPoint(BlockVector point, MessageHandler messages);
	
	public boolean isComplete();
	
	@Override
	public Iterator<BlockVector> iterator();
	
	public Selection clone();
	
	public void offset(BlockVector pos);
	
	public void scale(BlockVector amount);
	
	public void expand(BlockFace dir, int amount);
}
