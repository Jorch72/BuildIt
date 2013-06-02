package au.com.mineauz.BuildIt;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

public class BlockChanger
{
	private Set<BlockChangeTask> mAllTasks;
	private BukkitTask mTask;
	
	public static int maxChangesPerTick = 200;
	
	public BlockChanger()
	{
		mAllTasks = new HashSet<BlockChangeTask>();
		mTask = null;
	}
	public void submit(BlockChangeTask task, OfflinePlayer owner )
	{
		mAllTasks.add(task);
		
		// Start processing tasks
		if(mTask == null)
		{
			mTask = Bukkit.getScheduler().runTaskTimer(BuildIt.instance, new Runnable()
			{
				@Override
				public void run()
				{
					processAll();
				}
			}, 1, 1);
		}
	}
	
	public void cancelAll()
	{
		if(mTask != null)
			mTask.cancel();
		
		mAllTasks.clear();
	}
	
	private void processAll()
	{
		Iterator<BlockChangeTask> it = mAllTasks.iterator();

		int changesPerTask = maxChangesPerTick / Math.min(mAllTasks.size(), maxChangesPerTick);
		
		int changes = 0;
		outer: while(it.hasNext() && changes < maxChangesPerTick)
		{
			BlockChangeTask task = it.next();
			for(int i = 0; i < changesPerTask; ++i)
			{
				if(task.isDone())
				{
					it.remove();
					continue outer;
				}
				
				task.doSome();
				changes++;
				
				if(changes >= maxChangesPerTick)
					break outer;
			}
		}
		
		if(mAllTasks.isEmpty())
		{
			mTask.cancel();
			mTask = null;
		}
	}
}
