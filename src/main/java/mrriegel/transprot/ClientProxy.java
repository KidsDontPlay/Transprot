package mrriegel.transprot;

import java.util.Random;

import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		Transprot.dispatcher.initModel();
		Transprot.linker.initModel();
		Transprot.upgrade.initModel();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new TransferRender());
	}

	@Override
	public void spawnParticles(NBTTagCompound nbt) {
		BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
		Vec3d vec = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
		double dx = vec.xCoord, dy = vec.yCoord, dz = vec.zCoord;
		for (int i = 0; i < 7; i++) {
			double xx = (new Random().nextDouble() - .5) / 2.3d;
			double yy = (new Random().nextDouble() - .5) / 2.3d;
			double zz = (new Random().nextDouble() - .5) / 2.3d;
			ParticleHelper.renderParticle(new CommonParticle(pos.getX() + .5 + xx, pos.getY() + .5 + yy, pos.getZ() + .5 + zz, dx, dy, dz).setScale(1.2F).setTexture(ParticleHelper.squareParticle).setColor(ColorHelper.brighter(0xffe3, 0.6), 0).setMaxAge2(new Random().nextInt(10) + 5));
			// Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.END_ROD,
			// pos.getX() + .5 + xx, pos.getY() + .5 + yy, pos.getZ() + .5 + zz,
			// dx, dy, dz);
		}
	}
}
