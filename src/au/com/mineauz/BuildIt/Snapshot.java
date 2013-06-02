package au.com.mineauz.BuildIt;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.selection.Selection;

public class Snapshot
{
	private Selection mSelection;
	private ArrayList<BlockState> mStates;
	private boolean mReady;
	
	private Snapshot(Selection selection)
	{
		mSelection = selection;
		mStates = new ArrayList<BlockState>();
		mReady = false;
	}
	
	public static Snapshot create(Selection selection)
	{
		Snapshot snapshot = new Snapshot(selection);
		
		BuildIt.instance.getTaskRunner().submit(snapshot.new SnapshotBuilder());
		
		return snapshot;
	}
	
	/**
	 * Replaces all of the snapshot back into the world
	 */
	public void restore()
	{
		BuildIt.instance.getTaskRunner().submit(new SnapshotRestorer());
	}
	
	public boolean isReady()
	{
		return mReady;
	}
	
	public Selection getSelection()
	{
		return mSelection;
	}
	
	private class SnapshotBuilder implements IncrementalTask
	{
		private Iterator<BlockVector> mProgress;
		
		public SnapshotBuilder()
		{
			mProgress = mSelection.iterator();
		}
		
		@Override
		public void doSome()
		{
			BlockVector point = mProgress.next();
			
			Block b = mSelection.getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());
			mStates.add(b.getState());
			
			if(isDone())
				mReady = true;
		}

		@Override
		public boolean isDone()
		{
			return !mProgress.hasNext();
		}
		
	}
	
	private class SnapshotRestorer implements IncrementalTask
	{
		private Iterator<BlockState> mProgress;
		
		public SnapshotRestorer()
		{
			mProgress = mStates.iterator();
		}
		
		@Override
		public void doSome()
		{
			BlockState block = mProgress.next();
			
			block.update(true);
		}

		@Override
		public boolean isDone()
		{
			return !mProgress.hasNext();
		}
		
	}
}
