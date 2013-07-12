package au.com.mineauz.BuildIt.natives.concrete;

import net.minecraft.server.v1_6_R2.BiomeBase;
import net.minecraft.server.v1_6_R2.Chunk;

import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_6_R2.block.CraftBlock;

import au.com.mineauz.BuildIt.natives.NativeChunk;

public class Chunk1_6_R2 implements NativeChunk
{
	Chunk mChunk;
	public Chunk1_6_R2(Chunk chunk)
	{
		mChunk = chunk;
	}
	
	@Override
	public int getTypeId( int x, int y, int z )
	{
		return mChunk.getTypeId(x, y, z);
	}

	@Override
	public byte getData( int x, int y, int z )
	{
		return (byte)mChunk.getData(x, y, z);
	}

	@Override
	public Biome getBiome( int x, int z )
	{
		BiomeBase baseBiome = mChunk.a(x, z, mChunk.world.worldProvider.e);
		return CraftBlock.biomeBaseToBiome(baseBiome);
	}

}
