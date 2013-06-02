package au.com.mineauz.BuildIt.selection.mode;

import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;

//TODO:
public class FillSelection implements Selection
{

	@Override
	public List<BlockVector> getPoints()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockVector getMinPoint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockVector getMaxPoint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInSelection( BlockVector pos )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPoint( BlockVector point, MessageHandler messages )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComplete()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<BlockVector> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FillSelection clone()
	{
		return null;
	}
}
