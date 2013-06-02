package au.com.mineauz.BuildIt.selection.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class Union implements Selection
{
	private Selection mA, mB;
	
	public Union(Selection a, Selection b)
	{
		Validate.notNull(a);
		Validate.notNull(b);
		
		Validate.isTrue(a.isComplete());
		
		Validate.isTrue(a.getWorld().equals(b.getWorld()));
		
		mA = a;
		mB = b;
	}
	@Override
	public List<BlockVector> getPoints()
	{
		ArrayList<BlockVector> points = new ArrayList<BlockVector>(mA.getPoints());
		points.addAll(mB.getPoints());
		return points;
	}

	@Override
	public BlockVector getMinPoint()
	{
		return new BlockVector(Math.min(mA.getMinPoint().getX(), mB.getMinPoint().getX()), Math.min(mA.getMinPoint().getY(), mB.getMinPoint().getY()), Math.min(mA.getMinPoint().getZ(), mB.getMinPoint().getZ()));
	}

	@Override
	public BlockVector getMaxPoint()
	{
		return new BlockVector(Math.max(mA.getMaxPoint().getX(), mB.getMaxPoint().getX()), Math.max(mA.getMaxPoint().getY(), mB.getMaxPoint().getY()), Math.max(mA.getMaxPoint().getZ(), mB.getMaxPoint().getZ()));
	}

	@Override
	public World getWorld()
	{
		return mA.getWorld();
	}

	@Override
	public boolean isInSelection( BlockVector pos )
	{
		boolean in = false;
		if(pos.isInAABB(mA.getMinPoint(), mA.getMaxPoint()))
		{
			if(mA.isInSelection(pos))
				in = true;
		}
		
		if(!in && mB.isComplete() && pos.isInAABB(mB.getMinPoint(), mB.getMaxPoint()))
		{
			if(mB.isInSelection(pos))
				in = true;
		}
		
		return in;
	}

	@Override
	public boolean addPoint( BlockVector point, MessageHandler messages )
	{
		return mB.addPoint(point, messages);
	}

	@Override
	public boolean isComplete()
	{
		return mB.isComplete();
	}

	@Override
	public Iterator<BlockVector> iterator()
	{
		return SelectionManager.makeIterator(this);
	}
	
	@Override
	public Union clone()
	{
		Union sel = new Union(mA.clone(), mB.clone());
		return sel;
	}

}
