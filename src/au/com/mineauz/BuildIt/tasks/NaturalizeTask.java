package au.com.mineauz.BuildIt.tasks;

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.types.BlockType;

public class NaturalizeTask implements IncrementalTask
{
	private Selection mSelection;
	private Mask mMask;
	
	private Random mRand;
	
	private Iterator<BlockVector> mProgress;
	
	public NaturalizeTask(Selection selection)
	{
		this(selection, new Mask());
	}
	
	public NaturalizeTask(Selection selection, Mask mask)
	{
		Validate.notNull(selection);
		Validate.isTrue(selection.isComplete());
		
		Validate.notNull(mask);
		
		mSelection = selection.clone();
		mMask = mask;
		
		mProgress = mSelection.iterator();
		
		mRand = new Random();
		
	}
	
	@Override
	public float doSome()
	{
		if(!mProgress.hasNext())
			return 0;
		
		BlockVector loc = mProgress.next();
		
		if(loc.getBlockY() < 0 || loc.getBlockY() > 256)
			return 0;
		
		Block block = mSelection.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		if(mMask.isEmpty() || mMask.isPresent(block))
		{
			// Find out how many blocks are above this
			int count = 0;
			
			for(int y = loc.getBlockY(); y <= mSelection.getMaxPoint().getBlockY(); ++y)
			{
				if(!mSelection.isInSelection(new BlockVector(loc.getBlockX(), y, loc.getBlockZ())))
					break;
				
				Block b = mSelection.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
				if(!b.getType().isSolid())
					break;
				
				++count;
			}
			
			if(count == 0)
				return 0;
			
			BlockType type;
			
			int dirtDepth = mRand.nextInt(2) + 3;
			
			if(count > dirtDepth)
				type = new BlockType(Material.STONE, (byte)0);
			else if(count > 1)
				type = new BlockType(Material.DIRT, (byte)0);
			else
				type = new BlockType(Material.GRASS, (byte)0);
			
			try
			{
				type.apply(block);
			}
			catch(Exception e)
			{
				return 0;
			}
		}
		
		return 4;
	}
	
	@Override
	public boolean isDone()
	{
		return !mProgress.hasNext();
	}
}
