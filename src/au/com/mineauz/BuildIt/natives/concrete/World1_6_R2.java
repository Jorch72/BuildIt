package au.com.mineauz.BuildIt.natives.concrete;

import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;

import net.minecraft.server.v1_6_R2.Chunk;
import net.minecraft.server.v1_6_R2.WorldServer;
import au.com.mineauz.BuildIt.natives.NativeChunk;
import au.com.mineauz.BuildIt.natives.NativeWorld;

public class World1_6_R2 implements NativeWorld
{
	private WorldServer mWorld;
	
	public World1_6_R2(org.bukkit.World world)
	{
		mWorld = ((CraftWorld)world).getHandle();
	}
	
	@Override
	public void populateChunk( int x, int z )
	{
		Chunk chunk = mWorld.chunkProviderServer.getOrCreateChunk(x, z);
		chunk.done = false;
		mWorld.chunkProviderServer.getChunkAt(mWorld.chunkProviderServer, x, z);
	}

	@Override
	public NativeChunk generateChunk( int x, int z )
	{
		return new Chunk1_6_R2(mWorld.chunkProviderServer.chunkProvider.getOrCreateChunk(x, z));
	}

}
