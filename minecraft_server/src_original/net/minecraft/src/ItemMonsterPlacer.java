package net.minecraft.src;

public class ItemMonsterPlacer extends Item {
	public ItemMonsterPlacer(int var1) {
		super(var1);
		this.setHasSubtypes(true);
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
		if(var3.isRemote) {
			return true;
		} else {
			int var8 = var3.getBlockId(var4, var5, var6);
			var4 += Facing.offsetsXForSide[var7];
			var5 += Facing.offsetsYForSide[var7];
			var6 += Facing.offsetsZForSide[var7];
			double var9 = 0.0D;
			if(var7 == 1 && var8 == Block.fence.blockID || var8 == Block.netherFence.blockID) {
				var9 = 0.5D;
			}

			if(func_48390_a(var3, var1.getItemDamage(), (double)var4 + 0.5D, (double)var5 + var9, (double)var6 + 0.5D) && !var2.capabilities.isCreativeMode) {
				--var1.stackSize;
			}

			return true;
		}
	}

	public static boolean func_48390_a(World var0, int var1, double var2, double var4, double var6) {
		if(!EntityList.entityEggs.containsKey(Integer.valueOf(var1))) {
			return false;
		} else {
			Entity var8 = EntityList.createEntityByID(var1, var0);
			if(var8 != null) {
				var8.setLocationAndAngles(var2, var4, var6, var0.rand.nextFloat() * 360.0F, 0.0F);
				var0.spawnEntityInWorld(var8);
				((EntityLiving)var8).playLivingSound();
			}

			return var8 != null;
		}
	}
}
