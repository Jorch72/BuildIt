package au.com.mineauz.BuildIt.tasks;

import java.util.Random;

import net.minecraft.server.v1_6_R2.BiomeBase;

import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.block.CraftBlock;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.selection.Selection;

public class RegenerateTask implements IncrementalTask
{
	private Selection mSelection;
	private World mWorld;
	
	private int mChunkX;
	private int mChunkZ;
	
	private BlockVector mProgress;
	private boolean mIsDone;
	
	private Chunk mChunk = null;
	private short[][] mLastGen = null;
	
	private BiomeLookup mLookup;
	private Random mRand;
	
	private net.minecraft.server.v1_6_R2.Chunk mNativeChunk;
	
	public RegenerateTask(Selection selection)
	{
		Validate.notNull(selection);
		Validate.isTrue(selection.isComplete());

		mRand = new Random();
		
		mSelection = selection.clone();
		mWorld = mSelection.getWorld();
		
		mChunkX = selection.getMinPoint().getBlockX() >> 4;
		mChunkZ = selection.getMinPoint().getBlockZ() >> 4;
		
		mProgress = selection.getMinPoint().clone();
		mIsDone = false;
		
		System.out.println(String.format("Min: %d %d %d", mProgress.getBlockX(), mProgress.getBlockY(), mProgress.getBlockZ()));
		System.out.println(String.format("Max: %d %d %d", mSelection.getMaxPoint().getBlockX(), mSelection.getMaxPoint().getBlockY(), mSelection.getMaxPoint().getBlockZ()));
	}
	
	private boolean offsetToNext()
	{
		do
		{
			if(!offsetProgress())
				return false;
			
		} while(!mSelection.isInSelection(mProgress));
		
		return true;
	}
	
	private boolean offsetProgress()
	{
		mProgress.setY(mProgress.getBlockY()+1);
		
		if(mProgress.getBlockY() > mSelection.getMaxPoint().getBlockY())
		{
			mProgress.setY(mSelection.getMinPoint().getBlockY());
			mProgress.setX(mProgress.getBlockX()+1);
			
			if(mProgress.getBlockX() > mSelection.getMaxPoint().getBlockX() || mProgress.getBlockX() >= mChunkX * 16 + 16)
			{
				mProgress.setX(Math.max(mSelection.getMinPoint().getBlockX(), mChunkX * 16));
				
				mProgress.setZ(mProgress.getBlockZ()+1);
				
				if(mProgress.getBlockZ() >= mChunkZ * 16 + 16)
				{
					
					if(mProgress.getBlockX() == mSelection.getMaxPoint().getBlockX() && mProgress.getBlockY() == mSelection.getMaxPoint().getBlockY() && mProgress.getBlockZ() == mSelection.getMaxPoint().getBlockZ())
						return false;
					
					// Offset the chunks
					mChunkX++;
					
					if(mChunkX > mSelection.getMaxPoint().getBlockX() >> 4)
					{
						mChunkX = mSelection.getMinPoint().getBlockX() >> 4;
						
						mChunkZ++;
					}
					
					mProgress.setY(mSelection.getMinPoint().getBlockY());
					mProgress.setX(Math.max(mSelection.getMinPoint().getBlockX(), mChunkX * 16));
					mProgress.setZ(Math.max(mSelection.getMinPoint().getBlockZ(), mChunkZ * 16));
				}
				
			}
		}
		
		if(mProgress.getBlockX() == mSelection.getMaxPoint().getBlockX() && mProgress.getBlockY() == mSelection.getMaxPoint().getBlockY() && mProgress.getBlockZ() == mSelection.getMaxPoint().getBlockZ())
			return false;
		
		return true;
	}
	
	@Override
	public float doSome()
	{
		if(!offsetToNext())
		{
			mIsDone = true;
			return 0f;
		}
		
		
		if(mProgress.getBlockY() < 0 || mProgress.getBlockY() > mWorld.getMaxHeight())
			return 0;
		
		Block block = mSelection.getWorld().getBlockAt(mProgress.getBlockX(), mProgress.getBlockY(), mProgress.getBlockZ());
		
		if(mChunk == null || (mProgress.getBlockX() >> 4) != mChunk.getX() || (mProgress.getBlockZ() >> 4) != mChunk.getZ())
		{
			// Load the next chunk
			mChunk = mWorld.getChunkAt(mProgress.getBlockX() >> 4, mProgress.getBlockZ() >> 4);
			
			mLookup = new BiomeLookup(mWorld, mChunk);
			ChunkGenerator gen = mWorld.getGenerator();
			if(gen != null)
			{
				mLastGen = gen.generateExtBlockSections(mWorld, mRand, mChunk.getX(), mChunk.getZ(), mLookup);
				mNativeChunk = null;
			}
			else
			{
				mNativeChunk = ((CraftWorld)mWorld).getHandle().chunkProviderServer.chunkProvider.getOrCreateChunk(mProgress.getBlockX() >> 4, mProgress.getBlockZ() >> 4);
			}
			System.out.println("Generating chunk " + mChunk.getX() + ", " + mChunk.getZ());
		}

		short id;
		
		if(mNativeChunk == null)
			id = mLastGen[mProgress.getBlockY() / 16][(mProgress.getBlockX() % 16) + (mProgress.getBlockZ() % 16) * 16];
		else
			id = (short)mNativeChunk.getTypeId(mProgress.getBlockX() & 0xF, mProgress.getBlockY(), mProgress.getBlockZ() & 0xF);
		
		block.setTypeId(id, false);
		
		if(mNativeChunk != null)
		{
			BiomeBase baseBiome = mNativeChunk.a(mProgress.getBlockX() & 0xF, mProgress.getBlockZ() & 0xF, ((CraftWorld)mWorld).getHandle().worldProvider.e);
			block.setBiome(CraftBlock.biomeBaseToBiome(baseBiome));
		}
		
		return 4;
	}
	
	@Override
	public boolean isDone()
	{
		return mIsDone;
	}
	
	private static class BiomeLookup implements BiomeGrid
	{
		private World mWorld;
		private Chunk mChunk;
		
		public BiomeLookup(World world, Chunk chunk)
		{
			mWorld = world;
			mChunk = chunk;
		}
		
		@Override
		public Biome getBiome( int chunkX, int chunkZ )
		{
			return mWorld.getBiome(chunkX + mChunk.getX(), chunkZ + mChunk.getZ());
		}
		
		@Override
		public void setBiome( int chunkX, int chunkZ, Biome biome )
		{
			mWorld.setBiome(chunkX + mChunk.getX(), chunkZ + mChunk.getZ(), biome);
		}
	}
}
