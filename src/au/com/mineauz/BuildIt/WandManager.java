package au.com.mineauz.BuildIt;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.BuildIt.selection.SelectMode;
import au.com.mineauz.BuildIt.selection.SelectionManager;
import au.com.mineauz.BuildIt.selection.WandMode;

public class WandManager implements Listener
{
	public static ItemStack wandItem = new ItemStack(Material.STICK);
	
	public static boolean isWand(ItemStack item)
	{
		if(item == null || item.getType() != wandItem.getType() || item.getDurability() != wandItem.getDurability())
			return false;
		
		if(!item.hasItemMeta())
			return false;
		
		ItemMeta meta = item.getItemMeta();
		
		for(WandType type : WandType.values())
		{
			if(meta.getDisplayName().startsWith(getWandTitle(type)))
				return true;
		}
		return false;
	}
	
	public static WandType getWandType(ItemStack wand)
	{
		assert(isWand(wand));
		
		ItemMeta meta = wand.getItemMeta();
		
		for(WandType type : WandType.values())
		{
			if(meta.getDisplayName().startsWith(getWandTitle(type)))
				return type;
		}
		
		throw new IllegalArgumentException();
	}
	
	public static void setWandType(ItemStack wand, WandType type)
	{
		assert(isWand(wand));
		
		WandType old = getWandType(wand);
		
		if(old == type)
			return;
		
		ItemMeta meta = wand.getItemMeta();
		meta.setLore(new ArrayList<String>());
		
		String title = getWandTitle(type);
		switch(type)
		{
		case Drawing:
			break;
		case Selection:
			title += SelectionManager.getWandExtraTitle(WandMode.Normal, SelectMode.Cuboid);
			break;
		}
		
		meta.setDisplayName(title);
		wand.setItemMeta(meta);
		
		switch(type)
		{
		case Drawing:
			break;
		case Selection:
			SelectionManager.setWandMode(wand, WandMode.Normal);
			SelectionManager.setWandSelectionType(wand, SelectMode.Cuboid);
			break;
		}
	}
	
	public static String getWandTitle(WandType type)
	{
		return ChatColor.GOLD + "BuildIt " + type.toString() + " Wand ";
	}
	
	
	
	public static ItemStack makeWand()
	{
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(getWandTitle(WandType.Selection));
		item.setItemMeta(meta);
		
		SelectionManager.setWandMode(item, WandMode.Normal);
		SelectionManager.setWandSelectionType(item, SelectMode.Cuboid);
		
		return item;
	}
	
	public WandManager(BuildIt plugin)
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onInteract(PlayerInteractEvent event)
	{
		if(!event.hasItem() || !isWand(event.getItem()))
			return;
		
		if(event.useItemInHand() == Result.DENY)
			return;
		
		switch(getWandType(event.getItem()))
		{
		case Drawing:
			BuildIt.instance.getDrawingManager().onWandUse(event);
			break;
		case Selection:
			BuildIt.instance.getSelectionManager().onWandUse(event);
			break;
		}
	}
}
