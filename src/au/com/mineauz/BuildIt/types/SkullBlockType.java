package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

public class SkullBlockType extends BlockType
{
	private String mSkullOwner;
	private BlockFace mFacing;
	private SkullType mType;
	
	public SkullBlockType( SkullType type, BlockFace facing, String owner )
	{
		super(Material.SKULL, (byte)1);
		
		mSkullOwner = owner;
		mFacing = facing;
		mType = type;
	}
	
	public SkullBlockType( SkullType type )
	{
		this(type, BlockFace.SOUTH, null);
	}
	
	public SkullBlockType( SkullType type, BlockFace face )
	{
		this(type, face, null);
	}
	
	public SkullBlockType( SkullType type, String owner)
	{
		this(type, BlockFace.SOUTH, owner);
	}
	
	public SkullBlockType( BlockType type )
	{
		super(Material.SKULL, type.getData());
	}
	
	public void setOwner(String owner)
	{
		mSkullOwner = owner;
	}
	
	public void setFacing(BlockFace facing)
	{
		mFacing = facing;
	}
	
	public void setType(SkullType type)
	{
		mType = type;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		Skull state = (Skull)block.getState();
		state.setSkullType(mType);
		
		if(mSkullOwner != null)
			state.setOwner(mSkullOwner);
		
		state.setRotation(mFacing);
		
		state.update();
	}

}
