package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;

public class FurnaceType extends InventoryHolderBlockType
{
	private int mCookTime;
	private int mBurnTime;
	
	public FurnaceType(Material material, int cookTime, int burnTime, Inventory inventory)
	{
		super(material, (byte)0, inventory);
		
		mCookTime = cookTime;
		mBurnTime = burnTime;
	}
	
	public FurnaceType(Material material, int cookTime, int burnTime)
	{
		super(material, (byte)0, 2);
		
		mCookTime = cookTime;
		mBurnTime = burnTime;
	}
	
	public int getCookTime()
	{
		return mCookTime;
	}
	
	public void setCookTime(int time)
	{
		mCookTime = time;
	}
	
	public int getBurnTime()
	{
		return mBurnTime;
	}
	
	public void setBurnTime(int time)
	{
		mBurnTime = time;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		Furnace furnace = (Furnace)block.getState();
		
		furnace.setBurnTime((short)mBurnTime);
		furnace.setCookTime((short)mCookTime);
		
		furnace.update();
	}
}
