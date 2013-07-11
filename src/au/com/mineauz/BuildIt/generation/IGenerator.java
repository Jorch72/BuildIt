package au.com.mineauz.BuildIt.generation;

import au.com.mineauz.BuildIt.Mask;
import au.com.mineauz.BuildIt.pattern.Pattern;

public interface IGenerator
{
	public boolean isDone();
	
	public void generate(Pattern pattern, Mask mask);
}
