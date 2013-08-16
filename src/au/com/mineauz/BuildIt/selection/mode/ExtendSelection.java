package au.com.mineauz.BuildIt.selection.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class ExtendSelection implements Selection
{
	private World mWorld;
	private BlockVector mMin;
	private BlockVector mMax;
	
	private int mNext;
	
	public ExtendSelection(World world)
	{
		mWorld = world;
		mMin = null;
		mMax = null;
		
		mNext = 0;
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
	public void setWorld( World world )
	{
		mWorld = world;
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
			mMin = mMax = point;
			
			messages.addMessage("Selection set to %d,%d,%d", point.getBlockX(), point.getBlockY(), point.getBlockZ());
		}
		else if(mNext >= 1)
		{
			Vector oldMin = mMin;
			Vector oldMax = mMax;
			mMin = new BlockVector(Math.min(oldMin.getX(), point.getX()), Math.min(oldMin.getY(), point.getY()), Math.min(oldMin.getZ(), point.getZ()));
			mMax = new BlockVector(Math.max(oldMax.getX(), point.getX()), Math.max(oldMax.getY(), point.getY()), Math.max(oldMax.getZ(), point.getZ()));
			
			messages.addMessage("Extended Selection to %d,%d,%d", point.getBlockX(), point.getBlockY(), point.getBlockZ());
		}
		
		mNext++;
		return true;
	}
	
	@Override
	public boolean isComplete()
	{
		return mNext != 0;
	}
	
	@Override
	public Iterator<BlockVector> iterator()
	{
		return SelectionManager.makeIterator(this);
	}
	
	@Override
	public ExtendSelection clone()
	{
		ExtendSelection sel = new ExtendSelection(mWorld);
		sel.mMax = mMax.clone();
		sel.mMin = mMin.clone();
		sel.mNext = mNext;
		
		return sel;
	}

	@Override
	public void offset( BlockVector pos )
	{
		Validate.isTrue(isComplete());
		
		mMax.add(pos);
		mMin.add(pos);
	}

	@Override
	public void scale( BlockVector amount )
	{
		Validate.isTrue(isComplete());
	}

	@Override
	public void expand( BlockFace dir, int amount )
	{
		Validate.isTrue(isComplete());
		
		switch(dir)
		{
		case DOWN:
			mMin.setY(mMin.getY() - amount);
			break;
		case EAST:
			mMax.setX(mMax.getX() + amount);
			break;
		case NORTH:
			mMax.setZ(mMax.getZ() - amount);
			break;
		case SOUTH:
			mMin.setZ(mMin.getZ() + amount);
			break;
		case UP:
			mMax.setY(mMax.getY() + amount);
			break;
		case WEST:
			mMin.setX(mMin.getX() - amount);
			break;
		default:
			break;
		}
	}
	
	@Override
	public List<BlockVector> getPointsForDisplay()
	{
ArrayList<BlockVector> points = new ArrayList<BlockVector>();
		
		int pX = (mMax.getBlockX() - mMin.getBlockX()) / 10;
		int pY = (mMax.getBlockY() - mMin.getBlockY()) / 10;
		int pZ = (mMax.getBlockZ() - mMin.getBlockZ()) / 10;
		
		points.add(new BlockVector(mMin.getBlockX(), mMin.getBlockY(), mMin.getBlockZ()));
		points.add(new BlockVector(mMin.getBlockX(), mMin.getBlockY(), mMax.getBlockZ()));
		points.add(new BlockVector(mMin.getBlockX(), mMax.getBlockY(), mMin.getBlockZ()));
		points.add(new BlockVector(mMax.getBlockX(), mMin.getBlockY(), mMin.getBlockZ()));
		points.add(new BlockVector(mMin.getBlockX(), mMax.getBlockY(), mMax.getBlockZ()));
		points.add(new BlockVector(mMax.getBlockX(), mMax.getBlockY(), mMin.getBlockZ()));
		points.add(new BlockVector(mMax.getBlockX(), mMin.getBlockY(), mMax.getBlockZ()));
		points.add(new BlockVector(mMax.getBlockX(), mMax.getBlockY(), mMax.getBlockZ()));
		
		double amt = (mMax.getBlockX() - mMin.getBlockX()) / (pX + 1);
		for(int i = 0; i <= pX; ++i)
		{
			points.add(new BlockVector(mMin.getBlockX() + i * amt, mMin.getBlockY(), mMin.getBlockZ()));
			points.add(new BlockVector(mMin.getBlockX() + i * amt, mMax.getBlockY(), mMin.getBlockZ()));
			points.add(new BlockVector(mMin.getBlockX() + i * amt, mMin.getBlockY(), mMax.getBlockZ()));
			points.add(new BlockVector(mMin.getBlockX() + i * amt, mMax.getBlockY(), mMax.getBlockZ()));
		}
		
		amt = (mMax.getBlockZ() - mMin.getBlockZ()) / (pZ + 1);
		for(int i = 0; i <= pZ; ++i)
		{
			points.add(new BlockVector(mMin.getBlockX(), mMin.getBlockY(), mMin.getBlockZ() + i * amt));
			points.add(new BlockVector(mMin.getBlockX(), mMax.getBlockY(), mMin.getBlockZ() + i * amt));
			points.add(new BlockVector(mMax.getBlockX(), mMin.getBlockY(), mMin.getBlockZ() + i * amt));
			points.add(new BlockVector(mMax.getBlockX(), mMax.getBlockY(), mMin.getBlockZ() + i * amt));
		}
		
		amt = (mMax.getBlockY() - mMin.getBlockY()) / (pY + 1);
		for(int i = 0; i <= pY; ++i)
		{
			points.add(new BlockVector(mMin.getBlockX(), mMin.getBlockY() + i * amt, mMin.getBlockZ()));
			points.add(new BlockVector(mMax.getBlockX(), mMin.getBlockY() + i * amt, mMin.getBlockZ()));
			points.add(new BlockVector(mMin.getBlockX(), mMin.getBlockY() + i * amt, mMax.getBlockZ()));
			points.add(new BlockVector(mMax.getBlockX(), mMin.getBlockY() + i * amt, mMax.getBlockZ()));
		}
		
		return points;
	}
}
