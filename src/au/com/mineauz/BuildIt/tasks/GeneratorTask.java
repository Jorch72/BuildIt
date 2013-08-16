package au.com.mineauz.BuildIt.tasks;

import org.apache.commons.lang.Validate;
import org.bukkit.World;

import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.generation.IGenerator;
import au.com.mineauz.BuildIt.pattern.Pattern;

public class GeneratorTask implements IncrementalTask
{
	private IGenerator mGenerator;
	private Pattern mPlacePattern;
	private Mask mMask;

	public GeneratorTask(IGenerator generator, Pattern placePattern)
	{
		this(generator, placePattern, new Mask());
	}
	
	public GeneratorTask(IGenerator generator, Pattern placePattern, Mask mask)
	{
		Validate.notNull(generator);
		
		Validate.notNull(placePattern);
		Validate.notNull(mask);
		
		mGenerator = generator;
		mPlacePattern = placePattern;
		mMask = mask;
	}
	
	@Override
	public float doSome()
	{
		if(mGenerator.isDone())
			return 0;
		
		try
		{
			mGenerator.generate(mPlacePattern, mMask);
		}
		catch(Exception e)
		{
			return 0;
		}
		
		return 4;
	}
	
	@Override
	public boolean isDone()
	{
		return mGenerator.isDone();
	}
	
	@Override
	public World getWorld()
	{
		return null;
	}
}
