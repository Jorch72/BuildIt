package au.com.mineauz.BuildIt;

import java.util.ArrayList;

public class MessageHandler
{
	private ArrayList<String> mMessages;
	
	public MessageHandler()
	{
		mMessages = new ArrayList<String>();
	}
	
	public void addMessage(String format, Object... args)
	{
		mMessages.add(String.format(format, args));
	}
	
	public String[] getMessages() { return mMessages.toArray(new String[mMessages.size()]); }
}
