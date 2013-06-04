package au.com.mineauz.BuildIt.tasks;

import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.types.BlockType;

public class BlockChangeTask implements IncrementalTask
{
	private Selection mSelection;
	private Pattern mPlacePattern;
	private Mask mMask;
	
	private Iterator<BlockVector> mProgress;
	
	public BlockChangeTask(Selection selection, Pattern placePattern)
	{
		this(selection, placePattern, new Mask());
	}
	
	public BlockChangeTask(Selection selection, Pattern placePattern, Mask mask)
	{
		Validate.notNull(selection);
		Validate.isTrue(selection.isComplete());
		
		Validate.notNull(placePattern);
		Validate.notNull(mask);
		
		mSelection = selection.clone();
		mPlacePattern = placePattern;
		mMask = mask;
		
		mProgress = mSelection.iterator();
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
			BlockType newType = mPlacePattern.getBlockType((BlockVector)loc.clone().subtract(mSelection.getMinPoint()));
			try
			{
				newType.apply(block);
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
