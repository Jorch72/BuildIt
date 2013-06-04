package au.com.mineauz.BuildIt.types;

import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignBlockType extends BlockType
{
	private String[] mLines;
	
	public SignBlockType(Material signType, byte data, String... lines )
	{
		super(signType, data);
		
		Validate.isTrue(signType == Material.SIGN_POST || signType == Material.WALL_SIGN);
		
		mLines = Arrays.copyOf(lines, 4);
	}
	
	public String[] getLines()
	{
		return mLines;
	}
	
	public void setLines(String[] lines)
	{
		mLines = Arrays.copyOf(lines, 4);
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		Sign sign = (Sign)block.getState();
		
		sign.setLine(0, mLines[0]);
		sign.setLine(1, mLines[1]);
		sign.setLine(2, mLines[2]);
		sign.setLine(3, mLines[3]);
		
		sign.update();
	}
}
