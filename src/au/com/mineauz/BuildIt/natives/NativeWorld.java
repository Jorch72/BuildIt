package au.com.mineauz.BuildIt.natives;

public interface NativeWorld
{
	public void populateChunk(int x, int z);
	
	public NativeChunk generateChunk(int x, int z);
}
