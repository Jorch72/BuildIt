package au.com.mineauz.BuildIt.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.MessageHandler;
import au.com.mineauz.BuildIt.WandManager;
import au.com.mineauz.BuildIt.WandType;
import au.com.mineauz.BuildIt.selection.mode.Cutaway;
import au.com.mineauz.BuildIt.selection.mode.Difference;
import au.com.mineauz.BuildIt.selection.mode.Intersection;
import au.com.mineauz.BuildIt.selection.mode.Union;
import au.com.mineauz.BuildIt.tasks.IncrementalTask;

public class SelectionManager implements Listener
{
	private WeakHashMap<Player, Selection> mSelections;
	private WeakHashMap<Player, Selection> mLastSelections;
	
	private WeakHashMap<Player, List<BlockVector>> mDisplaying;
	
	
	public static String getWandExtraTitle(WandMode wandMode, SelectMode selectMode)
	{
		return ChatColor.WHITE + wandMode.toString() + ", " + selectMode.toString();
	}
	
	public static WandMode getWandMode(ItemStack wand)
	{
		assert(WandManager.isWand(wand) && WandManager.getWandType(wand) == WandType.Selection);
		
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		
		if(lore == null || lore.size() == 0)
			return WandMode.Normal;
		
		for(String line : lore)
		{
			line = ChatColor.stripColor(line);
			if(line.startsWith("Mode: "))
			{
				String modeName = line.substring(6);
				for(WandMode mode : WandMode.values())
				{
					if(modeName.equalsIgnoreCase(mode.toString()))
						return mode;
				}
				
				break;
			}
		}
		
		return WandMode.Normal;
	}
	
	public static void setWandMode(ItemStack wand, WandMode mode)
	{
		assert(WandManager.isWand(wand) && WandManager.getWandType(wand) == WandType.Selection);
		
		ItemMeta meta = wand.getItemMeta();
		
		ArrayList<String> lore;
		
		if(meta.hasLore())
			lore = new ArrayList<String>(meta.getLore());
		else
			lore = new ArrayList<String>();
		
		boolean done = false;
		for(int i = 0; i < lore.size(); ++i)
		{
			String line = ChatColor.stripColor(lore.get(i));
			if(line.startsWith("Mode: "))
			{
				lore.set(i, ChatColor.WHITE + "Mode: " + ChatColor.AQUA + mode.toString());
				done = true;
				break;
			}
		}
		
		if(!done)
			lore.add(ChatColor.WHITE + "Mode: " + ChatColor.AQUA + mode.toString());
		
		meta.setLore(lore);
		
		meta.setDisplayName(WandManager.getWandTitle(WandType.Selection) + getWandExtraTitle(mode, getWandSelectionMode(wand)));
		
		wand.setItemMeta(meta);
		
	}
	
	public static SelectMode getWandSelectionMode(ItemStack wand)
	{
		assert(WandManager.isWand(wand) && WandManager.getWandType(wand) == WandType.Selection);
		
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		
		if(lore == null || lore.size() == 0)
			return SelectMode.Cuboid;
		
		for(String line : lore)
		{
			line = ChatColor.stripColor(line);
			if(line.startsWith("Selection: "))
			{
				String modeName = line.substring(11);
				for(SelectMode mode : SelectMode.values())
				{
					if(modeName.equalsIgnoreCase(mode.toString()))
						return mode;
				}
				
				break;
			}
		}
		
		return SelectMode.Cuboid;
	}
	
	public static void setWandSelectionType(ItemStack wand, SelectMode mode)
	{
		assert(WandManager.isWand(wand) && WandManager.getWandType(wand) == WandType.Selection);
		
		ItemMeta meta = wand.getItemMeta();
		
		ArrayList<String> lore;
		
		if(meta.hasLore())
			lore = new ArrayList<String>(meta.getLore());
		else
			lore = new ArrayList<String>();
		
		boolean done = false;
		for(int i = 0; i < lore.size(); ++i)
		{
			String line = ChatColor.stripColor(lore.get(i));
			if(line.startsWith("Selection: "))
			{
				lore.set(i, ChatColor.WHITE + "Selection: " + ChatColor.AQUA + mode.toString());
				done = true;
				break;
			}
		}
		
		if(!done)
			lore.add(ChatColor.WHITE + "Selection: " + ChatColor.AQUA + mode.toString());
		
		meta.setLore(lore);
		meta.setDisplayName(WandManager.getWandTitle(WandType.Selection) + getWandExtraTitle(getWandMode(wand), mode));
		
		wand.setItemMeta(meta);
	}
	
	public SelectionManager()
	{
		mSelections = new WeakHashMap<Player, Selection>();
		mLastSelections = new WeakHashMap<Player, Selection>();
		mDisplaying = new WeakHashMap<Player, List<BlockVector>>();
		Bukkit.getPluginManager().registerEvents(this, BuildIt.instance);
	}

	/**
	 * Gets the current selection for that player. It returns only complete selections only
	 * @return The current selection, or null if there isnt a complete one
	 */
	public Selection getSelection(Player player)
	{
		Selection sel = mSelections.get(player);
		
		if(sel == null || !sel.isComplete())
			return null;
		
		return sel;
	}
	
	public void setSelection( Player player, Selection sel )
	{
		Validate.isTrue(sel.isComplete());
		mSelections.put(player, sel);
		displaySelection(player, sel);
	}
	

	public void onWandUse(PlayerInteractEvent event)
	{
		if(!event.hasItem() || !WandManager.isWand(event.getItem()) || WandManager.getWandType(event.getItem()) != WandType.Selection)
			return;
		
		if(!event.hasBlock())
		{
			if(event.getAction() == Action.LEFT_CLICK_AIR)
			{
				// Change the wand mode
				WandMode current = getWandMode(event.getItem());

				int i = current.ordinal();
				
				if(event.getPlayer().isSneaking())
				{
					--i;
					if(i < 0)
						i = WandMode.values().length-1;
				}
				else
				{
					++i;
					if(i >= WandMode.values().length)
						i = 0;
				}
				
				current = WandMode.values()[i];
				
				//event.getPlayer().sendMessage("Changed wand mode to " + ChatColor.GOLD + current);
				setWandMode(event.getItem(), current);
				event.getPlayer().setItemInHand(event.getItem());
			}
			else if(event.getAction() == Action.RIGHT_CLICK_AIR)
			{
				// Change the selection mode
				SelectMode current = getWandSelectionMode(event.getItem());
				int i = current.ordinal();
				
				if(event.getPlayer().isSneaking())
				{
					--i;
					if(i < 0)
						i = SelectMode.values().length-1;
				}
				else
				{
					++i;
					if(i >= SelectMode.values().length)
						i = 0;
				}
				
				current = SelectMode.values()[i];
				
				//event.getPlayer().sendMessage("Changed selection mode to " + ChatColor.GOLD + current);
				setWandSelectionType(event.getItem(), current);
				event.getPlayer().setItemInHand(event.getItem());
				
				Selection sel = mSelections.get(event.getPlayer());
				if(sel != null && !sel.isComplete())
				{
					mSelections.remove(event.getPlayer());
					hideSelection(event.getPlayer());
				}
			}
		}
		else
		{
			SelectMode mode = getWandSelectionMode(event.getItem());
			
			MessageHandler messages = new MessageHandler();
			
			if(event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				WandMode wandMode = getWandMode(event.getItem());
				
				Selection old = mSelections.get(event.getPlayer());
				
				if(wandMode != WandMode.Normal)
					mLastSelections.put(event.getPlayer(), old);
				
				// Start a new selection regardless
				Selection sel = mode.newSelection(event.getPlayer().getWorld());
				if(sel.addPoint(event.getClickedBlock().getLocation().toVector().toBlockVector(), messages))
				{
					hideSelection(event.getPlayer());
					if(old != null && old.isComplete())
					{
						switch(wandMode)
						{
						case Union:
							event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Union with new " + mode + " selection.");
							mSelections.put(event.getPlayer(), new Union(old, sel));
							break;
						case Intersection:
							event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Intersection with new " + mode + " selection.");
							mSelections.put(event.getPlayer(), new Intersection(old, sel));
							break;
						case Difference:
							event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Difference with new " + mode + " selection.");
							mSelections.put(event.getPlayer(), new Difference(old, sel));
							break;
						case Cutaway:
							event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Cutaway with new " + mode + " selection.");
							mSelections.put(event.getPlayer(), new Cutaway(old, sel));
							break;
						default:
							event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + mode.toString() + " selection started.");
							mSelections.put(event.getPlayer(), sel);
							break;
						}
					}
					else
					{
						event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + mode.toString() + " selection started.");
						mSelections.put(event.getPlayer(), sel);
					}
				
					if(sel.isComplete())
					{
						int dx = (sel.getMaxPoint().getBlockX() - sel.getMinPoint().getBlockX()) + 1;
						int dy = (sel.getMaxPoint().getBlockY() - sel.getMinPoint().getBlockY()) + 1;
						int dz = (sel.getMaxPoint().getBlockZ() - sel.getMinPoint().getBlockZ()) + 1;
						
						int vol = dx * dy * dz;
						
						messages.addMessage(ChatColor.GREEN + "Selection Bounds { %d, %d, %d Volume: %d }", dx, dy, dz, vol);
						displaySelection(event.getPlayer(), sel);
					}
				}
				
				
			}
			else if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Selection sel = mSelections.get(event.getPlayer());
				
				if(sel != null && sel.getWorld().equals(event.getPlayer().getWorld()))
				{
					hideSelection(event.getPlayer());
					sel.addPoint(event.getClickedBlock().getLocation().toVector().toBlockVector(), messages);
					
					if(sel.isComplete())
					{
						int dx = (sel.getMaxPoint().getBlockX() - sel.getMinPoint().getBlockX()) + 1;
						int dy = (sel.getMaxPoint().getBlockY() - sel.getMinPoint().getBlockY()) + 1;
						int dz = (sel.getMaxPoint().getBlockZ() - sel.getMinPoint().getBlockZ()) + 1;
						
						int vol = dx * dy * dz;
						
						messages.addMessage(ChatColor.GREEN + "Selection Bounds { %d, %d, %d Volume: %d }", dx, dy, dz, vol);
						displaySelection(event.getPlayer(), sel);
					}
				}
			}
			
			event.getPlayer().sendMessage(messages.getMessages());
		}
		
		event.setCancelled(true);
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);
	}

	public void reshowSelections(World world)
	{
		for(Entry<Player, Selection> entry : mSelections.entrySet())
		{
			if(entry.getValue().isComplete() && entry.getValue().getWorld().equals(world))
				displaySelection(entry.getKey(), entry.getValue());
		}
	}
	public void reshowSelection(Player player)
	{
		Selection sel = getSelection(player);
		if(sel != null)
			displaySelection(player, sel);
	}
	
	public void removeAllSelections()
	{
		for(Player player : mSelections.keySet())
			hideSelection(player);
		
		mSelections.clear();
	}
	
	private void displaySelection(Player player, Selection sel)
	{
		Validate.isTrue(sel.isComplete());
		
		if(mDisplaying.containsKey(player))
			hideSelection(player);
		
		if(!sel.getWorld().equals(player.getWorld()))
			return;
		
		List<BlockVector> blocks = sel.getPointsForDisplay();
		
		BuildIt.instance.getTaskRunner().submit(new DisplaySelectionTask(blocks, sel.getWorld(), player, true));

		mDisplaying.put(player, blocks);
	}
	
	private void hideSelection(Player player)
	{
		List<BlockVector> blocks = mDisplaying.get(player);
		
		if(blocks == null)
			return;
		
		BuildIt.instance.getTaskRunner().submit(new DisplaySelectionTask(blocks, player.getWorld(), player, false));
		
		mDisplaying.remove(player);
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerChangeWorld(PlayerChangedWorldEvent event)
	{
		Selection sel = getSelection(event.getPlayer()); 
		if(sel != null)
		{
			if(event.getPlayer().getWorld().equals(sel.getWorld()))
				displaySelection(event.getPlayer(), sel);
			else
				mDisplaying.remove(event.getPlayer());
		}
	}
	
	public static Iterator<BlockVector> makeIterator(Selection selection)
	{
		return new SelIterator(selection);
	}
	
	private static class SelIterator implements Iterator<BlockVector>
	{
		private BlockVector mVec;
		private BlockVector mMin;
		private BlockVector mMax;
		
		private Selection mSel;
		
		public SelIterator(Selection sel)
		{
			mSel = sel;
			
			mMin = sel.getMinPoint().clone();
			mMax = sel.getMaxPoint().clone();
			
			if(mMin.getBlockY() < 0)
				mMin.setY(0);
			
			if(mMin.getBlockY() > 255)
				mMin.setY(255);
			
			if(mMax.getBlockY() < 0)
				mMax.setY(0);
			
			if(mMax.getBlockY() > 255)
				mMax.setY(255);
			
			mVec = (BlockVector) mMin.clone().setX(mMin.getBlockX()-1);
		}
		
		@Override
		public boolean hasNext()
		{
			BlockVector vec = mVec.clone();
			return offsetToNext(vec);
		}
		
		private boolean offsetToNext(BlockVector vec)
		{
			do
			{
				if(!offsetVector(vec))
					return false;
				
			} while(!mSel.isInSelection(vec));
			
			return true;
		}
		
		private boolean offsetVector(BlockVector vec)
		{
			vec.setX(vec.getBlockX()+1);
			
			if(vec.getBlockX() > mMax.getBlockX())
			{
				vec.setX(mMin.getBlockX());
				vec.setZ(vec.getBlockZ()+1);
				
				if(vec.getBlockZ() > mMax.getBlockZ())
				{
					vec.setZ(mMin.getBlockZ());
					
					if(vec.getBlockY() == mMax.getBlockY())
						return false;
					
					vec.setY(vec.getBlockY()+1);
				}
			}
			
			return true;
		}
		
		@Override
		public BlockVector next()
		{
			if(offsetToNext(mVec))
				return mVec.clone();
			
			return null;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Remove is not supported");
		}
		
	}
	
	private class DisplaySelectionTask implements IncrementalTask
	{
		private List<BlockVector> mBlocks;
		private World mWorld;
		private Player mPlayer;
		private boolean mSet;
		
		private int mIndex;
		
		public DisplaySelectionTask(List<BlockVector> blocks, World world, Player player, boolean set)
		{
			mBlocks = blocks;
			mWorld = world;
			mPlayer = player;
			mSet = set;
			mIndex = 0;
		}
		@Override
		public World getWorld()
		{
			return null;
		}

		@Override
		public float doSome()
		{
			if(mIndex < mBlocks.size())
			{
				if(mSet)
					mPlayer.sendBlockChange(mBlocks.get(mIndex++).toLocation(mWorld), Material.GOLD_BLOCK, (byte)0);
				else
				{
					BlockVector block = mBlocks.get(mIndex++);
					Block b = mWorld.getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ());
					mPlayer.sendBlockChange(block.toLocation(mWorld), b.getType(), b.getData());
				}
			}
			
			return 0;
		}

		@Override
		public boolean isDone()
		{
			return mIndex >= mBlocks.size();
		}
		
	}
	
	
}
