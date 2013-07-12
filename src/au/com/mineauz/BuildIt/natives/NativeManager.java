package au.com.mineauz.BuildIt.natives;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.InvalidPluginException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.mineauz.BuildIt.BuildIt;

public class NativeManager
{
	private static HashMap<Class<?> , Class<?>> mMappings = new HashMap<Class<?>, Class<?>>();
	
	private static String mCBVersion = null;
	
	private static Constructor<? extends NativeWorld> mWorldConstructor;
	
	public static String getCraftBukkitVersion()
	{
		if(mCBVersion != null)
			return mCBVersion;
		
		try
		{
			InputStream stream = Bukkit.class.getClassLoader().getResourceAsStream("META-INF/maven/org.bukkit/craftbukkit/pom.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);
		
			doc.getDocumentElement().normalize();
			
			NodeList list = doc.getDocumentElement().getElementsByTagName("properties");
			mCBVersion = ((Element)list.item(0)).getElementsByTagName("minecraft_version").item(0).getTextContent();
			stream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch ( SAXException e )
		{
			e.printStackTrace();
		}

		return mCBVersion;
	}
	
	private static boolean isClassPresent(String className)
	{
		try
		{
			Class.forName(className);
			return true;
		}
		catch(Throwable e)
		{
			return false;
		}
	}
	
	private static Class<?> loadClass(String className)
	{
		try
		{
			return Class.forName(className);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private static void addMapping(Class<?> interfaceClass, String mappedClass) throws InvalidPluginException
	{
		if(isClassPresent(mappedClass))
		{
			Class<?> clazz = loadClass(mappedClass);
			if(interfaceClass.isAssignableFrom(clazz))
				mMappings.put(interfaceClass, clazz);
			else
				throw new InvalidPluginException();
		}
		else
			throw new InvalidPluginException();
	}
	
	public static void initialize() throws InvalidPluginException
	{
		BuildIt.instance.getLogger().info("Loading craftbukkit/nms classes");
		
		addMapping(NativeWorld.class, "au.com.mineauz.BuildIt.natives.concrete.World" + getCraftBukkitVersion());
		addMapping(NativeChunk.class, "au.com.mineauz.BuildIt.natives.concrete.Chunk" + getCraftBukkitVersion());
	}
	
	public static NativeWorld getNativeWorld(World world)
	{
		try
		{
			if(mWorldConstructor == null)
			{
				@SuppressWarnings( "unchecked" )
				Class<? extends NativeWorld> clazz = (Class<? extends NativeWorld>) mMappings.get(NativeWorld.class);
				
				mWorldConstructor = clazz.getConstructor(World.class);
			}
			
			return mWorldConstructor.newInstance(world);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
