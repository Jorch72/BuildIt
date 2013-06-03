package au.com.mineauz.BuildIt.selection;

import java.lang.reflect.Constructor;

import org.bukkit.World;

import au.com.mineauz.BuildIt.selection.mode.*;

public enum SelectMode
{
	Cuboid("Cuboid", CuboidSelection.class),
	Extend("Extend", ExtendSelection.class),
	Sphere("Sphere", SphereSelection.class),
	SphereExtend("Sphere Extend", SphereExtendSelection.class);
	
	private Constructor<? extends Selection> mConstructor;
	private String mName;
	
	SelectMode(String name, Class<? extends Selection> clazz)
	{
		mName = name;
		try
		{
			mConstructor = clazz.getConstructor(World.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Selection newSelection(World world)
	{
		try
		{
			return mConstructor.newInstance(world);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return mName;
	}
}
