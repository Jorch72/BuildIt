package au.com.mineauz.BuildIt.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryHolderBlockType extends BlockType
{
	private Inventory mInventory;
	
	public InventoryHolderBlockType(Material material, byte data, int size)
	{
		super(material, data);
		mInventory = Bukkit.createInventory(null, size);
	}
	
	public InventoryHolderBlockType(Material material, byte data, Inventory source)
	{
		super(material, data);
		mInventory = Bukkit.createInventory(null, source.getSize());
		mInventory.setContents(source.getContents());
	}
	
	public Inventory getInventory()
	{
		return mInventory;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		InventoryHolder holder = (InventoryHolder)block.getState();
		
		if(holder instanceof Chest)
			((Chest)holder).getBlockInventory().setContents(mInventory.getContents());
		else
			holder.getInventory().setContents(mInventory.getContents());
	}
}
