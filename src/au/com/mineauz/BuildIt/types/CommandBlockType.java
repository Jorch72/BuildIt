package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;

public class CommandBlockType extends BlockType
{
	private String mCommand;
	private String mName;
	
	public CommandBlockType( String name, String command )
	{
		super(Material.COMMAND, (byte)0);
		mCommand = command;
		mName = name;
	}
	
	public String getCommand()
	{
		return mCommand;
	}
	
	public void setCommand(String command)
	{
		mCommand = command;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		CommandBlock cmd = (CommandBlock)block.getState();
		
		cmd.setName(mName);
		cmd.setCommand(mCommand);
		
		cmd.update();
	}

}
