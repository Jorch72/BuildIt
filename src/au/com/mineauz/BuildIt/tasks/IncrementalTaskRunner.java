package au.com.mineauz.BuildIt.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import au.com.mineauz.BuildIt.BuildIt;

public class IncrementalTaskRunner
{
	private List<IncrementalTask> mAllTasks;
	private List<IncrementalTask> mWaitingTasks;
	
	private HashMap<IncrementalTask, Runnable> mCallbacks;
	private BukkitTask mTask;
	
	private boolean mIsProcessing;
	
	public static int maxChangesPerTick = 4000;
	
	public IncrementalTaskRunner()
	{
		mAllTasks = new ArrayList<IncrementalTask>();
		mWaitingTasks = new ArrayList<IncrementalTask>();
		
		mCallbacks = new HashMap<IncrementalTask, Runnable>();
		mTask = null;
	}
	public void submit(IncrementalTask task)
	{
		submit(task, null);
	}
	public void submit(IncrementalTask task, Runnable callback )
	{
		if(mIsProcessing)
			mWaitingTasks.add(task);
		else
			mAllTasks.add(task);
		
		if(callback != null)
			mCallbacks.put(task, callback);
		
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
		mIsProcessing = true;
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

					if(task.getWorld() != null)
						BuildIt.instance.getSelectionManager().reshowSelections(task.getWorld());
					
					Runnable callback = mCallbacks.remove(task);

					if(callback != null)
						callback.run();
					
					continue outer;
				}
				
				changes += task.doSome();
			}
		}
		
		mAllTasks.addAll(mWaitingTasks);
		mWaitingTasks.clear();
		
		if(mAllTasks.isEmpty())
		{
			mTask.cancel();
			mTask = null;
		}
		
		mIsProcessing = false;
	}
	
	public void forceAll()
	{
		for(IncrementalTask task : mAllTasks)
		{
			while(!task.isDone())
				task.doSome();
		}
		
		mAllTasks.clear();
	}
}
