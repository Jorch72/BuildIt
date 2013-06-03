package au.com.mineauz.BuildIt.selection.mode;

import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.SelectionManager;

public class Intersection implements Selection
{
	private Selection mA, mB;
	
	public Intersection(Selection a, Selection b)
	{
		Validate.notNull(a);
		Validate.notNull(b);
		
		Validate.isTrue(a.isComplete());
		
		Validate.isTrue(a.getWorld().equals(b.getWorld()));
		
		mA = a;
		mB = b;
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
	public void setWorld( World world )
	{
		mA.setWorld(world);
		mB.setWorld(world);
	}

	@Override
	public boolean isInSelection( BlockVector pos )
	{
		if(!mB.isComplete())
			return false;
		
		boolean ina = false;
		if(pos.isInAABB(mA.getMinPoint(), mA.getMaxPoint()))
			ina = mA.isInSelection(pos);
		
		boolean inb = false;
		if(pos.isInAABB(mB.getMinPoint(), mB.getMaxPoint()))
			inb = mB.isInSelection(pos);
		
		return ina && inb;
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
	public Intersection clone()
	{
		Intersection sel = new Intersection(mA.clone(), mB.clone());
		return sel;
	}
	
	@Override
	public void offset( BlockVector pos )
	{
		mA.offset(pos);
		mB.offset(pos);
	}

	@Override
	public void scale( BlockVector amount )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void expand( BlockFace dir, int amount )
	{
		mA.expand(dir, amount);
		mB.expand(dir, amount);
	}
}
