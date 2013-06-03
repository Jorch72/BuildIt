package au.com.mineauz.BuildIt;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.types.BlockType;

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
		BuildIt.instance.getTaskRunner().submit(new SnapshotRestorer(true));
	}
	
	public void restore(BlockVector offset, World world, boolean includeAir)
	{
		BuildIt.instance.getTaskRunner().submit(new SnapshotRestorer(offset, world, includeAir));
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

		@Override
		public float weight()
		{
			return 0.1f;
		}
		
	}
	
	private class SnapshotRestorer implements IncrementalTask
	{
		private Iterator<BlockState> mProgress;
		private boolean mAir;
		
		private BlockVector mOffset;
		private World mWorld;
		
		public SnapshotRestorer(boolean includeAir)
		{
			mProgress = mStates.iterator();
			mAir = includeAir;
			
			mOffset = null;
			mWorld = null;
		}
		public SnapshotRestorer(BlockVector offset, World world, boolean includeAir)
		{
			mProgress = mStates.iterator();
			mAir = includeAir;
			mOffset = offset;
			mWorld = world;
		}
		
		@Override
		public void doSome()
		{
			BlockState block = mProgress.next();
			
			while(!mAir && block.getType() == Material.AIR)
			{
				if(mProgress.hasNext())
					block = mProgress.next();
				else
					return;
			}
			
			if(mOffset != null)
			{
				BlockType type = BlockType.fromState(block);
				
				BlockVector pos = block.getLocation().toVector().add(mOffset).toBlockVector();
				
				Block b = mWorld.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
				type.apply(b);
			}
			else
			{
				block.update(true);
			}
		}

		@Override
		public boolean isDone()
		{
			return !mProgress.hasNext();
		}

		@Override
		public float weight()
		{
			return 4;
		}
		
	}
}
