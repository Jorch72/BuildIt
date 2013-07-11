package au.com.mineauz.BuildIt.drawing;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.BuildIt;
import au.com.mineauz.BuildIt.generation.SphereGenerator;
import au.com.mineauz.BuildIt.pattern.Pattern;
import au.com.mineauz.BuildIt.selection.Selection;
import au.com.mineauz.BuildIt.selection.mode.SphereSelection;
import au.com.mineauz.BuildIt.tasks.GeneratorTask;

public class DrawSphere implements IDrawing
{
	private BlockVector mCenter;
	private double mRadius;
	
	public DrawSphere()
	{
		mCenter = null;
		mRadius = -1;
	}
	@Override
	public boolean addPoint( BlockVector point, Player who)
	{
		if(mCenter == null)
		{
			mCenter = point;
			who.sendMessage(ChatColor.DARK_PURPLE + "Sphere center set.");
		}
		else if(mRadius == -1)
		{
			mRadius = point.distance(mCenter);
			who.sendMessage(ChatColor.DARK_PURPLE + "Sphere radius set.");
		}
		else
			return false;
		
		return true;
	}

	@Override
	public boolean isDefined()
	{
		return mRadius != -1; 
	}
	
	@Override
	public Selection makeSelection( Player owner )
	{
		return new SphereSelection(owner.getWorld(), mCenter.clone(), mRadius);
	}

	@Override
	public void draw( Pattern pattern, World world )
	{
		BuildIt.instance.getTaskRunner().submit(new GeneratorTask(new SphereGenerator(mCenter.toLocation(world), mRadius), pattern));
	}

}
