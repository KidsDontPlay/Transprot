package mrriegel.transprot;

import mrriegel.limelib.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class ParticleMessage extends AbstractMessage<ParticleMessage> {

	public ParticleMessage() {
		super();
	}

	public ParticleMessage(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		Transprot.proxy.spawnParticles(nbt);
	}

}
