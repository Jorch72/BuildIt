package au.com.mineauz.BuildIt.generation;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.commands.generation.GeneratorCommand;
import au.com.mineauz.BuildIt.commands.generation.IGeneratorCommandHandler;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.selection.mode.SphereSelection;
import au.com.mineauz.BuildIt.tasks.GeneratorTask;
import au.com.mineauz.BuildIt.types.BlockType;


public class HollowSphereGenerator implements IGenerator
{
	private SphereSelection mSelection;
	private Iterator<BlockVector> mIterator;
	
	public HollowSphereGenerator(Location center, double radius)
	{
		mSelection = new SphereSelection(center.getWorld(), center.toVector().toBlockVector(), radius);
		mIterator = mSelection.iterator();
	}
	
	@Override
	public boolean isDone()
	{
		return !mIterator.hasNext();
	}
	
	@Override
	public void generate(Pattern pattern, Mask mask)
	{
		BlockVector pos = mIterator.next();
		
		if(pos == null)
			return;
		
		// at least one face must be exposed to be used
		boolean doIt = false;
		if(!mSelection.isInSelection(new BlockVector(pos.getBlockX()-1, pos.getBlockY(), pos.getBlockZ())))
			doIt = true;
		else if(!mSelection.isInSelection(new BlockVector(pos.getBlockX()+1, pos.getBlockY(), pos.getBlockZ())))
			doIt = true;
		else if(!mSelection.isInSelection(new BlockVector(pos.getBlockX(), pos.getBlockY()-1, pos.getBlockZ())))
			doIt = true;
		else if(!mSelection.isInSelection(new BlockVector(pos.getBlockX(), pos.getBlockY()+1, pos.getBlockZ())))
			doIt = true;
		else if(!mSelection.isInSelection(new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()-1)))
			doIt = true;
		else if(!mSelection.isInSelection(new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()+1)))
			doIt = true;
		
		if(!doIt)
			return;
		
		BlockType type = pattern.getBlockType((BlockVector)pos.clone().subtract(mSelection.getMinPoint()));
		
		Block block = mSelection.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		
		if(mask.isEmpty() || mask.isPresent(block))
			type.apply(block);
	}
	
	@GeneratorCommand(name="hsphere", perm="buildit.generators.hsphere", description="Generates a hollow sphere at your location", args={double.class, Pattern.class})
	public static class CommandHandler implements IGeneratorCommandHandler
	{
		@Override
		public boolean onCommand( Player player, Object... arguments )
		{
			return onCommand(player, (Double)arguments[0], (Pattern)arguments[1]);
		}
		
		private boolean onCommand (Player player, double radius, Pattern pattern)
		{
			Location loc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			
			BuildIt.instance.getUndoManager().addStep(new SphereSelection(player.getWorld(), loc.toVector().toBlockVector(), radius), player);
			BuildIt.instance.getTaskRunner().submit(new GeneratorTask(new HollowSphereGenerator(loc, radius), pattern));
			return true;
		}
	}
}
