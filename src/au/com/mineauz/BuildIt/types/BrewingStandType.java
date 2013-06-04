package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.Inventory;

public class BrewingStandType extends InventoryHolderBlockType
{
	private int mBrewTime;
	
	public BrewingStandType(int brewTime, Inventory inventory)
	{
		super(Material.BREWING_STAND, (byte)0, inventory);
		
		mBrewTime = brewTime;
	}
	
	public BrewingStandType(int brewTime)
	{
		super(Material.BREWING_STAND, (byte)0, 4);
		
		mBrewTime = brewTime;
	}
	
	public int getBrewTime()
	{
		return mBrewTime;
	}
	
	public void setBrewTime(int time)
	{
		mBrewTime = time;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		BrewingStand stand = (BrewingStand)block.getState();
		
		stand.setBrewingTime(mBrewTime);
		
		stand.update();
	}
}
