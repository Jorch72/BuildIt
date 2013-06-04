package au.com.mineauz.BuildIt.types;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;

public class JukeboxType extends BlockType
{
	private Material mRecord;
	
	public JukeboxType(Material record)
	{
		super(Material.JUKEBOX, (byte)0);
		
		Validate.isTrue(record.isRecord());
		
		mRecord = record;
	}
	
	public Material getRecord()
	{
		return mRecord;
	}
	
	public void setRecord(Material record)
	{
		Validate.isTrue(record.isRecord());
		
		mRecord = record;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		Jukebox jukebox = (Jukebox)block.getState();
		
		jukebox.setPlaying(mRecord);
		
		jukebox.update();
	}
}
