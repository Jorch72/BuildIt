package au.com.mineauz.BuildIt.selection.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class CuboidSelection implements Selection
{
	private World mWorld;
	private BlockVector mMin;
	private BlockVector mMax;
	
	private int mNext;
	
	public CuboidSelection(World world)
	{
		mWorld = world;
		mMin = null;
		mMax = null;
		
		mNext = 0;
	}
	
	@Override
	public List<BlockVector> getPoints()
	{
		List<BlockVector> list = new ArrayList<BlockVector>();
		if(mMin != null)
			list.add(mMin);
		if(mMax != null)
			list.add(mMax);
		
		return list;
	}

	@Override
	public BlockVector getMinPoint()
	{
		return mMin;
	}

	@Override
	public BlockVector getMaxPoint()
	{
		return mMax;
	}

	@Override
	public World getWorld()
	{
		return mWorld;
	}

	@Override
	public boolean isInSelection( BlockVector pos )
	{
		return true;
	}

	@Override
	public boolean addPoint( BlockVector point, MessageHandler messages )
	{
		if(mNext == 0)
		{
			mMin = point;
			messages.addMessage("Corner 1 Set to %d,%d,%d", point.getBlockX(), point.getBlockY(), point.getBlockZ());
		}
		else if(mNext == 1)
		{
			Vector temp = mMin;
			mMin = new BlockVector(Math.min(temp.getX(), point.getX()), Math.min(temp.getY(), point.getY()), Math.min(temp.getZ(), point.getZ()));
			mMax = new BlockVector(Math.max(temp.getX(), point.getX()), Math.max(temp.getY(), point.getY()), Math.max(temp.getZ(), point.getZ()));
			
			messages.addMessage("Corner 2 Set to %d,%d,%d", point.getBlockX(), point.getBlockY(), point.getBlockZ());
		}
		else
			return false;
		
		mNext++;
		return true;
	}
	
	@Override
	public boolean isComplete()
	{
		return mNext == 2;
	}
	
	@Override
	public Iterator<BlockVector> iterator()
	{
		return SelectionManager.makeIterator(this);
	}
	
	@Override
	public CuboidSelection clone()
	{
		CuboidSelection sel = new CuboidSelection(mWorld);
		sel.mMax = mMax.clone();
		sel.mMin = mMin.clone();
		sel.mNext = mNext;
		
		return sel;
	}
}
