package au.com.mineauz.BuildIt.selection.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class SphereSelection implements Selection
{
	private World mWorld;
	private BlockVector mCenter;
	private BlockVector mRadiusMarker;
	private double mRadius;
	
	private int mNext;
	
	public SphereSelection(World world)
	{
		mWorld = world;
		mCenter = null;
		
		mNext = 0;
	}
	
	@Override
	public List<BlockVector> getPoints()
	{
		List<BlockVector> list = new ArrayList<BlockVector>();
		if(mCenter != null)
			list.add(mCenter);
		if(mRadiusMarker != null)
			list.add(mRadiusMarker);
		
		return list;
	}

	@Override
	public BlockVector getMinPoint()
	{
		return (BlockVector)mCenter.clone().subtract(new BlockVector(mRadius, mRadius, mRadius));
	}

	@Override
	public BlockVector getMaxPoint()
	{
		return (BlockVector)mCenter.clone().add(new BlockVector(mRadius, mRadius, mRadius));
	}

	@Override
	public World getWorld()
	{
		return mWorld;
	}

	@Override
	public boolean isInSelection( BlockVector pos )
	{
		return (pos.distance(mCenter) <= mRadius);
	}

	@Override
	public boolean addPoint( BlockVector point, MessageHandler messages )
	{
		if(mNext == 0)
		{
			mCenter = point;
			messages.addMessage("Center set to %d,%d,%d", point.getBlockX(), point.getBlockY(), point.getBlockZ());
		}
		else if(mNext == 1)
		{
			mRadiusMarker = point;
			mRadius = mRadiusMarker.distance(mCenter);
			
			messages.addMessage("Radius Set to %d", (int)mRadius);
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
	public SphereSelection clone()
	{
		SphereSelection sel = new SphereSelection(mWorld);
		sel.mCenter = mCenter.clone();
		sel.mNext = mNext;
		sel.mRadius = mRadius;
		sel.mRadiusMarker = mRadiusMarker.clone();
		
		return sel;
	}
}
