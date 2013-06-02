package au.com.mineauz.BuildIt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;

import au.com.mineauz.BuildIt.types.BlockType;

public class Mask
{
	private Set<BlockType> mFilterTypes;
	
	public Mask()
	{
		mFilterTypes = new HashSet<BlockType>();
	}
	public Mask(BlockType... types)
	{
		mFilterTypes = new HashSet<BlockType>();
		Collections.addAll(mFilterTypes, types);
	}
	
	public void add(BlockType type)
	{
		mFilterTypes.add(type);
	}
	
	public void remove(BlockType type)
	{
		mFilterTypes.remove(type);
	}
	
	public void clear()
	{
		mFilterTypes.clear();
	}
	
	public boolean isPresent(BlockType type)
	{
		return mFilterTypes.contains(type);
	}
	
	public boolean isPresent(Block block)
	{
		return mFilterTypes.contains(new BlockType(block.getType(), block.getData()));
	}
	
	public boolean isEmpty()
	{
		return mFilterTypes.isEmpty();
	}
	
}
