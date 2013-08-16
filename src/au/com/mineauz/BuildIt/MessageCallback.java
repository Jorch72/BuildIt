package au.com.mineauz.BuildIt;

import org.bukkit.entity.Player;

public class MessageCallback implements Runnable
{
	private String mMessage;
	private Player mPlayer;
	
	public MessageCallback(Player player, String message)
	{
		mPlayer = player;
		mMessage = message;
	}
	
	@Override
	public void run()
	{
		if(mPlayer.isOnline())
			mPlayer.sendMessage(mMessage);
	}

}
