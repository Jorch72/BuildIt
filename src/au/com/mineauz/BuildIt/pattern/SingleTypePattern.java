package au.com.mineauz.BuildIt.pattern;

import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.types.BlockType;

public class SingleTypePattern implements Pattern
{
	private BlockType mType;
	
	public SingleTypePattern(BlockType type)
	{
		mType = type;
	}
	
	@Override
	public BlockType getBlockType( BlockVector offset )
	{
		return mType;
	}

	public static SingleTypePattern parse(String patternString) throws IllegalArgumentException
	{
		return new SingleTypePattern(BlockType.parse(patternString));
	}
}
