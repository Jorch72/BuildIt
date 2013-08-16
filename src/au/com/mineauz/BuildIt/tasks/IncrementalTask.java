package au.com.mineauz.BuildIt.tasks;

import org.bukkit.World;

public interface IncrementalTask
{
	public World getWorld();
	
	public float doSome();
	
	public boolean isDone();
}
