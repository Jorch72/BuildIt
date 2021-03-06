package au.com.mineauz.BuildIt;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.mineauz.BuildIt.selection.Selection;

public class UndoManager
{
	public static int maxUndoSteps = 20;
	
	private HashMap<OfflinePlayer, ArrayDeque<Snapshot>> mSteps;
	private HashMap<OfflinePlayer, ArrayDeque<Snapshot>> mRedoSteps;
	
	public UndoManager()
	{
		mSteps = new HashMap<OfflinePlayer, ArrayDeque<Snapshot>>();
		mRedoSteps = new HashMap<OfflinePlayer, ArrayDeque<Snapshot>>();
	}
	
	private ArrayDeque<Snapshot> getSteps(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> steps = mSteps.get(player);
		if(steps == null)
		{
			steps = new ArrayDeque<Snapshot>();
			mSteps.put(player, steps);
		}
		
		return steps;
	}
	
	private ArrayDeque<Snapshot> getRedoSteps(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> steps = mRedoSteps.get(player);
		if(steps == null)
		{
			steps = new ArrayDeque<Snapshot>();
			mRedoSteps.put(player, steps);
		}
		
		return steps;
	}
	
	public void addStep(Snapshot snapshot, OfflinePlayer player)
	{
		ArrayDeque<Snapshot> redoSteps = getRedoSteps(player);
		redoSteps.clear();
		
		ArrayDeque<Snapshot> steps = getSteps(player);
		
		if(steps.size() >= maxUndoSteps)
			steps.pollLast();
		
		steps.push(snapshot);
	}
	
	public boolean addStep(Selection selection, OfflinePlayer player)
	{
		Vector delta = selection.getMaxPoint().clone().subtract(selection.getMinPoint());
		int vol = delta.getBlockX() * delta.getBlockY() * delta.getBlockZ();
		
		if(vol > 10000000)
			return false;
		
		Snapshot snap = Snapshot.create(selection);
		ArrayDeque<Snapshot> redoSteps = getRedoSteps(player);
		redoSteps.clear();
		
		ArrayDeque<Snapshot> steps = getSteps(player);
		
		if(steps.size() >= maxUndoSteps)
			steps.pollLast();
		
		steps.push(snap);
		
		return true;
	}
	
	public void undoStep(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> redoSteps = getRedoSteps(player);
		ArrayDeque<Snapshot> steps = getSteps(player);
		
		if(steps.isEmpty())
			return;
		
		Snapshot snapshot = steps.pop();
		
		if(redoSteps.size() >= maxUndoSteps)
			redoSteps.pollLast();
		
		redoSteps.push(Snapshot.create(snapshot.getSelection()));
		
		if(player.isOnline())
			snapshot.restore(new MessageCallback((Player)player, "Undo complete"));
		else
			snapshot.restore(null);
	}
	
	public void redoStep(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> redoSteps = getRedoSteps(player);
		ArrayDeque<Snapshot> steps = getSteps(player);
		
		if(redoSteps.isEmpty())
			return;
		
		Snapshot snapshot = redoSteps.pop();
		
		if(steps.size() >= maxUndoSteps)
			steps.pollLast();
		
		steps.push(Snapshot.create(snapshot.getSelection()));
		
		if(player.isOnline())
			snapshot.restore(new MessageCallback((Player)player, "Redo complete"));
		else
			snapshot.restore(null);
	}
	
	public boolean canUndo(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> steps = getSteps(player);
		
		return !steps.isEmpty();
	}
	
	public boolean canRedo(OfflinePlayer player)
	{
		ArrayDeque<Snapshot> steps = getRedoSteps(player);
		
		return !steps.isEmpty();
	}
	
	
}
