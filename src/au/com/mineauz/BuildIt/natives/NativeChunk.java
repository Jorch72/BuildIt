package au.com.mineauz.BuildIt.natives;

import org.bukkit.block.Biome;

public interface NativeChunk
{
	public int getTypeId(int x, int y, int z);
	public byte getData(int x, int y, int z);
	
	public Biome getBiome(int x, int z);
}
