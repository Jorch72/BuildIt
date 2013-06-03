package au.com.mineauz.BuildIt.pattern;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.util.BlockVector;

import au.com.mineauz.BuildIt.types.BlockType;

public class RandomPattern implements Pattern
{
	private ArrayList<BlockType> mTypes;
	private ArrayList<Double> mWeight;
	
	private Random mRand;
	
	public RandomPattern()
	{
		mTypes = new ArrayList<BlockType>();
		mWeight = new ArrayList<Double>();
		mRand = new Random();
	}
	
	public void addType(BlockType type, double weight)
	{
		for(int i = 0; i < mWeight.size(); ++i)
		{
			if(weight > mWeight.get(i))
			{
				mWeight.add(i, weight);
				mTypes.add(i, type);
				return;
			}
		}
		
		mTypes.add(type);
		mWeight.add(weight);
	}
	
	@Override
	public BlockType getBlockType( BlockVector offset )
	{
		double val = mRand.nextDouble();
		
		for(int i = 0; i < mTypes.size(); ++i)
		{
			if(val < mWeight.get(i))
				return mTypes.get(i);
			
			val -= mWeight.get(i);
		}
		
		return mTypes.get(0);
	}
	
	public static RandomPattern parse(String patternString) throws IllegalArgumentException
	{
		String[] blocks = patternString.split(",");
		
		BlockType[] types = new BlockType[blocks.length];
		double[] weights = new double[blocks.length];
		
		int totalPercent = 0;
		int unknownWeights = 0;
		
		int i = 0;
		for(String block : blocks)
		{
			String[] idAndWeight = block.split("%");
			
			if(idAndWeight.length == 2)
			{
				try
				{
					int percentage = Integer.parseInt(idAndWeight[1]);
					
					if(percentage < 0 || percentage > 100)
						throw new IllegalArgumentException("The percentage specified for pattern " + (i+1) + " needs to be a whole number between the range of 0 and 100");
					
					totalPercent += percentage;
					weights[i] = percentage;
				}
				catch(NumberFormatException e)
				{
					throw new IllegalArgumentException("The percentage specified for pattern " + (i+1) + " needs to be a whole number between the range of 0 and 100");
				}
			}
			else
			{
				weights[i] = -1;
				unknownWeights++;
			}
			
			types[i] = BlockType.parse(idAndWeight[0]);
			++i;
		}
		
		int remainingPercent = Math.max(100 - totalPercent, 0);
		
		totalPercent += remainingPercent;
		
		RandomPattern pattern = new RandomPattern();
		
		// Recalculate weights
		for(i = 0; i < blocks.length; ++i)
		{
			if(weights[i] == -1)
				weights[i] = remainingPercent / unknownWeights;
			
			weights[i] /= totalPercent;
			
			pattern.addType(types[i], weights[i]);
		}
		
		return pattern;
	}

}
