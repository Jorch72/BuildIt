package au.com.mineauz.BuildIt.selection.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

//TODO: Make this an ellipse
public class SphereSelection implements Selection
{
	private World mWorld;
	private BlockVector mCenter;
	private double mRadius;
	
	private int mNext;
	
	public SphereSelection(World world)
	{
		mWorld = world;
		mCenter = null;
		
		mNext = 0;
	}
	
	public SphereSelection(World world, BlockVector center, double radius)
	{
		mWorld = world;
		mCenter = center;
		mRadius = radius;
		
		mNext = 2;
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
	public void setWorld( World world )
	{
		mWorld = world;
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
			mRadius = point.distance(mCenter);
			
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

		return sel;
	}

	@Override
	public void offset( BlockVector pos )
	{
		Validate.isTrue(isComplete());
		mCenter.add(pos);
	}

	@Override
	public void scale( BlockVector amount )
	{
		mRadius *= amount.length();
	}

	@Override
	public void expand( BlockFace dir, int amount )
	{
		mRadius += amount;
	}
	
	@Override
	public List<BlockVector> getPointsForDisplay()
	{
		ArrayList<BlockVector> points = new ArrayList<BlockVector>();
		
		points.add(new BlockVector(mCenter.getBlockX(), mCenter.getBlockY() + mRadius, mCenter.getBlockZ()));
		points.add(new BlockVector(mCenter.getBlockX(), mCenter.getBlockY() - mRadius, mCenter.getBlockZ()));
		
		points.add(new BlockVector(mCenter.getBlockX() + mRadius, mCenter.getBlockY(), mCenter.getBlockZ()));
		points.add(new BlockVector(mCenter.getBlockX() - mRadius, mCenter.getBlockY(), mCenter.getBlockZ()));
		
		points.add(new BlockVector(mCenter.getBlockX(), mCenter.getBlockY(), mCenter.getBlockZ() + mRadius));
		points.add(new BlockVector(mCenter.getBlockX(), mCenter.getBlockY(), mCenter.getBlockZ() - mRadius));
		
		return points;
	}
}
