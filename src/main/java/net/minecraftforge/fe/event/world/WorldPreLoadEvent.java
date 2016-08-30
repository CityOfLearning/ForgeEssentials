package net.minecraftforge.fe.event.world;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class WorldPreLoadEvent extends WorldEvent {

	public final int dim;

	public WorldPreLoadEvent(World world, int dim) {
		super(world);
		this.dim = dim;
	}

}
