package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BlockType
{
	private Material mMaterial;
	private byte mData;
	
	public BlockType(Material material, byte data)
	{
		mMaterial = material;
		mData = data;
	}
	
	public Material getMaterial()
	{
		return mMaterial;
	}
	public int getTypeId()
	{
		return mMaterial.getId();
	}
	
	public byte getData()
	{
		return mData;
	}
	
	public static BlockType fromItemStack(ItemStack stack)
	{
		if(!stack.getType().isBlock())
			throw new IllegalArgumentException("The material of that itemstack is not a block");
		
		return new BlockType(stack.getType(), (byte)stack.getDurability());
	}
	
	public static BlockType fromState(BlockState state)
	{
		BlockType type;
		if(state instanceof Skull)
		{
			type = new SkullBlockType(((Skull)state).getSkullType(), ((Skull)state).getRotation(), ((Skull)state).getOwner());
			type.mData = state.getData().getData();
		}
		else if(state instanceof CommandBlock)
			type = new CommandBlockType(((CommandBlock)state).getName(), ((CommandBlock)state).getCommand());
		else if(state instanceof CreatureSpawner)
			type = new SpawnerBlockType(((CreatureSpawner)state).getSpawnedType(), ((CreatureSpawner)state).getDelay());
		else if(state instanceof Jukebox)
			type = new JukeboxType(((Jukebox)state).getPlaying());
		else if(state instanceof NoteBlock)
			type = new NoteBlockType(((NoteBlock)state).getNote());
		else if(state instanceof Sign)
			type = new SignBlockType(state.getType(), state.getData().getData(), ((Sign)state).getLines());
		else if(state instanceof BrewingStand)
			type = new BrewingStandType(((BrewingStand)state).getBrewingTime(), ((BrewingStand)state).getInventory());
		else if(state instanceof Furnace)
			type = new FurnaceType(state.getType(), ((Furnace)state).getCookTime(), ((Furnace)state).getBurnTime());
		else if(state instanceof Chest)
			type = new InventoryHolderBlockType(state.getType(), state.getData().getData(), ((Chest)state).getBlockInventory());
		else if(state instanceof InventoryHolder)
			type = new InventoryHolderBlockType(state.getType(), state.getData().getData(), ((InventoryHolder)state).getInventory());
		else
			type = new BlockType(state.getType(), state.getData().getData());
				
		return type;
	}
	
	public static BlockType parse(String id) throws IllegalArgumentException
	{
		id = id.replace("_", " ");
		id = id.replace(";", "|");
		
		String[] data = id.split("\\|");
		String[] typeAndData = data[0].split(":");
		
		int itemId = 0;
		int itemData = -1;
		
		try
		{
			itemId = Integer.parseInt(typeAndData[0]);
			if(Material.getMaterial(itemId) == null)
				throw new IllegalArgumentException("No block with the id " + itemId);
		}
		catch(NumberFormatException e)
		{
			// Try a textual name
			itemId = -1;
			for(Material mat : Material.values())
			{
				if(typeAndData[0].equalsIgnoreCase(mat.toString().replace("_", " ")))
				{
					itemId = mat.getId();
					break;
				}
			}
			
			if(itemId == -1)
				throw new IllegalArgumentException("No block with the name " + typeAndData[0]);
		}
		
		BlockType type = null;
		
		// Parse data value
		if(typeAndData.length > 1)
		{
			try
			{
				itemData = Integer.parseInt(typeAndData[1]);
				if(itemData < 0 || itemData > 15)
					throw new IllegalArgumentException("Data values must be in the range 0 to 15 inclusive");
			}
			catch(NumberFormatException e)
			{
				Material mat = Material.getMaterial(itemId);
				if(mat == Material.WOOL)
				{
					if(typeAndData[1].equalsIgnoreCase("white"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("orange"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("magenta"))
						itemData = 2;
					else if(typeAndData[1].equalsIgnoreCase("light blue") || typeAndData[1].equalsIgnoreCase("lightblue"))
						itemData = 3;
					else if(typeAndData[1].equalsIgnoreCase("yellow"))
						itemData = 4;
					else if(typeAndData[1].equalsIgnoreCase("lime"))
						itemData = 5;
					else if(typeAndData[1].equalsIgnoreCase("pink"))
						itemData = 6;
					else if(typeAndData[1].equalsIgnoreCase("gray") || typeAndData[1].equalsIgnoreCase("grey"))
						itemData = 7;
					else if(typeAndData[1].equalsIgnoreCase("light gray") || typeAndData[1].equalsIgnoreCase("lightgray") || typeAndData[1].equalsIgnoreCase("light grey") || typeAndData[1].equalsIgnoreCase("lightgrey"))
						itemData = 8;
					else if(typeAndData[1].equalsIgnoreCase("cyan"))
						itemData = 9;
					else if(typeAndData[1].equalsIgnoreCase("purple"))
						itemData = 10;
					else if(typeAndData[1].equalsIgnoreCase("blue"))
						itemData = 11;
					else if(typeAndData[1].equalsIgnoreCase("brown"))
						itemData = 12;
					else if(typeAndData[1].equalsIgnoreCase("green"))
						itemData = 13;
					else if(typeAndData[1].equalsIgnoreCase("red"))
						itemData = 14;
					else if(typeAndData[1].equalsIgnoreCase("black"))
						itemData = 15;
				}
				else if(mat == Material.LOG || mat == Material.LEAVES || mat == Material.WOOD || mat == Material.SAPLING || mat == Material.WOOD_STEP)
				{
					if(typeAndData[1].equalsIgnoreCase("oak"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("spruce") || typeAndData[1].equalsIgnoreCase("pine"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("birch"))
						itemData = 2;
					else if(typeAndData[1].equalsIgnoreCase("jungle"))
						itemData = 3;
				}
				else if(mat == Material.STEP || mat == Material.DOUBLE_STEP)
				{
					if(typeAndData[1].equalsIgnoreCase("stone"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("sandstone"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("wooden"))
						itemData = 2;
					else if(typeAndData[1].equalsIgnoreCase("cobblestone"))
						itemData = 3;
					else if(typeAndData[1].equalsIgnoreCase("brick"))
						itemData = 4;
					else if(typeAndData[1].equalsIgnoreCase("stone brick") || typeAndData[1].equalsIgnoreCase("stonebrick"))
						itemData = 5;
					else if(typeAndData[1].equalsIgnoreCase("nether brick") || typeAndData[1].equalsIgnoreCase("netherbrick"))
						itemData = 6;
					else if(typeAndData[1].equalsIgnoreCase("quartz"))
						itemData = 7;
				}
				else if(mat == Material.SANDSTONE)
				{
					if(typeAndData[1].equalsIgnoreCase("normal"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("chiseled"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("smooth"))
						itemData = 2;
				}
				else if(mat == Material.LONG_GRASS)
				{
					if(typeAndData[1].equalsIgnoreCase("shrub"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("grass"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("fern"))
						itemData = 2;
				}
				else if(mat == Material.SMOOTH_BRICK)
				{
					if(typeAndData[1].equalsIgnoreCase("normal"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("mossy"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("cracked"))
						itemData = 2;
					else if(typeAndData[1].equalsIgnoreCase("chiseled"))
						itemData = 3;
				}
				else if(mat == Material.SKULL_ITEM || mat == Material.SKULL)
				{
					
					if(typeAndData[1].equalsIgnoreCase("skeleton"))
						type = new SkullBlockType(SkullType.SKELETON);
					else if(typeAndData[1].equalsIgnoreCase("wither skeleton") || typeAndData[1].equalsIgnoreCase("witherskeleton") || typeAndData[1].equalsIgnoreCase("wither"))
						type = new SkullBlockType(SkullType.WITHER);
					else if(typeAndData[1].equalsIgnoreCase("zombie"))
						type = new SkullBlockType(SkullType.ZOMBIE);
					else if(typeAndData[1].equalsIgnoreCase("human") || typeAndData[1].equalsIgnoreCase("player"))
						type = new SkullBlockType(SkullType.PLAYER);
					else if(typeAndData[1].equalsIgnoreCase("creeper"))
						type = new SkullBlockType(SkullType.CREEPER);
				}
				else if(mat == Material.QUARTZ_BLOCK)
				{
					if(typeAndData[1].equalsIgnoreCase("normal"))
						itemData = 0;
					else if(typeAndData[1].equalsIgnoreCase("chiseled"))
						itemData = 1;
					else if(typeAndData[1].equalsIgnoreCase("pillar"))
						itemData = 2;
				}
				
				if(itemData == -1 && type == null)
					throw new IllegalArgumentException("Unknown data value: " + typeAndData[1]);
			}
		}
		else
			itemData = -1;
		
		Material mat = Material.getMaterial(itemId); 
		
		if(!mat.isBlock())
			throw new IllegalArgumentException("Expected block id. Got " + typeAndData[0]);
		
		
		if(type == null)
			type = new BlockType(mat, (byte)itemData);
		
		// Extra data
		if(data.length > 1)
		{
			if(mat == Material.SKULL)
			{
				if(!(type instanceof SkullBlockType))
					type = new SkullBlockType(type);
				
				((SkullBlockType)type).setOwner(data[1]);
				if(data.length > 2)
				{
					// Facing
					try
					{
						BlockFace face = BlockFace.valueOf(data[2].toUpperCase().replaceAll(" ", "_"));
						((SkullBlockType)type).setFacing(face);
					}
					catch(IllegalArgumentException e)
					{
						throw new IllegalArgumentException("Unknown block face " + data[2]);
					}
				}
			}
			else if(mat == Material.MOB_SPAWNER)
			{
				EntityType entityType = EntityType.fromName(data[1]);
				if(entityType == null)
					throw new IllegalArgumentException("Unknown entity type " + data[1]);
				
				if(!entityType.isSpawnable() || !entityType.isAlive() || entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER)
					throw new IllegalArgumentException("Cannot use " + data[1]);
				
				int delay = -1;
				if(data.length > 2)
				{
					try
					{
						delay = Integer.parseInt(data[2]);
						if(delay < 0)
							throw new IllegalArgumentException("Cannot have a negative delay");
					}
					catch(NumberFormatException e)
					{
						throw new IllegalArgumentException("Expected spawn delay as second argument");
					}
				}
				
				type = new SpawnerBlockType(entityType, delay);
			}
		}
		
		return type;
	}
	
	public void apply(Block block)
	{
		if(mData == -1)
			block.setTypeIdAndData(mMaterial.getId(), (byte)0, false);
		else
			block.setTypeIdAndData(mMaterial.getId(), mData, false);
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof BlockType))
			return false;
		
		BlockType type = (BlockType)obj;
		
		return mMaterial.equals(type.mMaterial) && mData == type.mData;
	}
	
	@Override
	public int hashCode()
	{
		return mMaterial.hashCode() ^ (mData * 19);
	}
}
