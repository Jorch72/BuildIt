package au.com.mineauz.BuildIt;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.selection.Selection;

public class Snapshot
{
	private Selection mSelection;
	private ArrayList<BlockState> mStates;
	
	private Snapshot(Selection selection)
	{
		mSelection = selection;
		mStates = new ArrayList<BlockState>();
	}
	
	public static Snapshot create(Selection selection)
	{
		Snapshot snapshot = new Snapshot(selection);
		
		for(BlockVector point : selection)
		{
			Block b = selection.getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());
			snapshot.mStates.add(b.getState());
		}
		
		return snapshot;
	}
	
	/**
	 * Replaces all of the snapshot back into the world
	 */
	public void restore()
	{
		for(BlockState state : mStates)
			state.update(true);
	}
	
	public Selection getSelection()
	{
		return mSelection;
	}
}
