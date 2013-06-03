package au.com.mineauz.BuildIt;

import java.util.WeakHashMap;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.pattern.SingleTypePattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.types.BlockType;

public class ClipboardManager
{
	private static WeakHashMap<Player, ClipboardEntry> mClipboard;
	
	public ClipboardManager()
	{
		mClipboard = new WeakHashMap<Player, ClipboardEntry>();
	}
	
	public void copy(Selection selection, Player owner)
	{
		Validate.isTrue(selection.getWorld().equals(owner.getWorld()));
		
		ClipboardEntry entry = new ClipboardEntry();
		entry.snapshot = Snapshot.create(selection);
		entry.offset = owner.getLocation().toVector().subtract(selection.getMinPoint()).toBlockVector();
		
		mClipboard.put(owner, entry);
	}
	
	public void cut(Selection selection, Player owner)
	{
		Snapshot snap = Snapshot.create(selection);
		ClipboardEntry entry = new ClipboardEntry();
		entry.snapshot = snap;
		entry.offset = owner.getLocation().toVector().subtract(selection.getMinPoint()).toBlockVector();
		
		mClipboard.put(owner, entry);
		
		BuildIt.instance.getUndoManager().addStep(snap, owner);
		BuildIt.instance.getTaskRunner().submit(new BlockChangeTask(selection, new SingleTypePattern(new BlockType(Material.AIR, (byte)0))));
	}
	
	public void paste(Location position, boolean pasteAir, Player owner)
	{
		if(!canPaste(owner))
			return;
		
		ClipboardEntry entry = mClipboard.get(owner);
		
		Selection newSel = entry.snapshot.getSelection().clone();
		// FIXME: The paste can appear 1 further than it should be. But only happens if far enough away
		BlockVector newOffset = owner.getLocation().toVector().subtract(entry.offset.clone().add(entry.snapshot.getSelection().getMinPoint())).toBlockVector();
		newSel.offset(newOffset);
		newSel.setWorld(owner.getWorld());
		BuildIt.instance.getUndoManager().addStep(Snapshot.create(newSel), owner);
		
		entry.snapshot.restore(newOffset, owner.getWorld(), pasteAir);
	}
	
	public boolean canPaste(Player owner)
	{
		return mClipboard.containsKey(owner);
	}
	
	private static class ClipboardEntry
	{
		public Snapshot snapshot;
		public BlockVector offset;
	}
}
