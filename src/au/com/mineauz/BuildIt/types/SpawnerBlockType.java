package au.com.mineauz.BuildIt.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class SpawnerBlockType extends BlockType
{
	private EntityType mEntity;
	private int mDelay;
	
	public SpawnerBlockType(EntityType entity, int delay)
	{
		super(Material.MOB_SPAWNER, (byte)0);
		mEntity = entity;
		mDelay = delay;
	}
	
	public int getDelay()
	{
		return mDelay;
	}
	
	public void setDelay(int delay)
	{
		mDelay = delay;
	}
	
	public EntityType getEntity()
	{
		return mEntity;
	}
	
	public void setEntity(EntityType entity)
	{
		mEntity = entity;
	}
	
	@Override
	public void apply( Block block )
	{
		super.apply(block);
		
		CreatureSpawner spawner = (CreatureSpawner)block.getState();
		
		spawner.setSpawnedType(mEntity);
		if(mDelay != -1)
			spawner.setDelay(mDelay);
		
		spawner.update();
	}
}
