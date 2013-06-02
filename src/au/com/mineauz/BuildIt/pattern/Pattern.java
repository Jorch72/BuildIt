package au.com.mineauz.BuildIt.pattern;

import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.types.BlockType;

public interface Pattern
{
	public BlockType getBlockType(BlockVector offset);
}
