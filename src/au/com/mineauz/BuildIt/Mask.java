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
	
	public boolean isPresent(BlockType other)
	{
		for(BlockType type : mFilterTypes)
		{
			if(!type.getMaterial().equals(other.getMaterial()))
				continue;
			
			if(type.getData() == -1)
				return true;
			
			if(type.getData() == other.getData())
				return true;
		}
		
		return false;
	}
	
	public boolean isPresent(Block block)
	{
		for(BlockType type : mFilterTypes)
		{
			if(!type.getMaterial().equals(block.getType()))
				continue;
			
			if(type.getData() == -1)
				return true;
			
			if(type.getData() == block.getData())
				return true;
		}
		return false;
	}
	
	public boolean isEmpty()
	{
		return mFilterTypes.isEmpty();
	}
	
}
