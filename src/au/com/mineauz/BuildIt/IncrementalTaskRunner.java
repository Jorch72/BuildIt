package au.com.mineauz.BuildIt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class IncrementalTaskRunner
{
	private List<IncrementalTask> mAllTasks;
	private BukkitTask mTask;
	
	public static int maxChangesPerTick = 4000;
	
	public IncrementalTaskRunner()
	{
		mAllTasks = new ArrayList<IncrementalTask>();
		mTask = null;
	}
	public void submit(IncrementalTask task )
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
		Iterator<IncrementalTask> it = mAllTasks.iterator();

		double changes = 0;
		outer: while(it.hasNext() && changes < maxChangesPerTick)
		{
			IncrementalTask task = it.next();
			while(changes < maxChangesPerTick)
			{
				if(task.isDone())
				{
					it.remove();
					continue outer;
				}
				
				task.doSome();
				changes += task.weight();
			}
		}
		
		if(mAllTasks.isEmpty())
		{
			mTask.cancel();
			mTask = null;
		}
	}
}
