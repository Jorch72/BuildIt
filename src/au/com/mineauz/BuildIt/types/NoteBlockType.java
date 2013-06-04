package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;

public class NoteBlockType extends BlockType
{
	private Note mNote;
	
	public NoteBlockType(Note note)
	{
		super(Material.NOTE_BLOCK, (byte)0);
		
		mNote = note;
	}
	
	public Note getNote()
	{
		return mNote;
	}
	
	public void setNote(Note note)
	{
		mNote = note;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		NoteBlock note = (NoteBlock)block.getState();
		
		note.setNote(mNote);
		
		note.update();
	}
}
