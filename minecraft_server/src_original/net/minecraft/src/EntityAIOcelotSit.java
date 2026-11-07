package net.minecraft.src;

public class EntityAIOcelotSit extends EntityAIBase {
	private final EntityOcelot field_50019_a;
	private final float field_50017_b;
	private int field_50018_c = 0;
	private int field_52005_h = 0;
	private int field_50015_d = 0;
	private int field_50016_e = 0;
	private int field_50013_f = 0;
	private int field_50014_g = 0;

	public EntityAIOcelotSit(EntityOcelot var1, float var2) {
		this.field_50019_a = var1;
		this.field_50017_b = var2;
		this.setMutexBits(5);
	}

	public boolean shouldExecute() {
		return this.field_50019_a.isTamed() && !this.field_50019_a.isSitting() && this.field_50019_a.getRNG().nextDouble() <= (double)0.0065F && this.func_50012_f();
	}

	public boolean continueExecuting() {
		return this.field_50018_c <= this.field_50015_d && this.field_52005_h <= 60 && this.func_50011_a(this.field_50019_a.worldObj, this.field_50016_e, this.field_50013_f, this.field_50014_g);
	}

	public void startExecuting() {
		this.field_50019_a.getNavigator().func_48658_a((double)((float)this.field_50016_e) + 0.5D, (double)(this.field_50013_f + 1), (double)((float)this.field_50014_g) + 0.5D, this.field_50017_b);
		this.field_50018_c = 0;
		this.field_52005_h = 0;
		this.field_50015_d = this.field_50019_a.getRNG().nextInt(this.field_50019_a.getRNG().nextInt(1200) + 1200) + 1200;
		this.field_50019_a.func_50021_C().func_48210_a(false);
	}

	public void resetTask() {
		this.field_50019_a.func_48369_c(false);
	}

	public void updateTask() {
		++this.field_50018_c;
		this.field_50019_a.func_50021_C().func_48210_a(false);
		if(this.field_50019_a.getDistanceSq((double)this.field_50016_e, (double)(this.field_50013_f + 1), (double)this.field_50014_g) > 1.0D) {
			this.field_50019_a.func_48369_c(false);
			this.field_50019_a.getNavigator().func_48658_a((double)((float)this.field_50016_e) + 0.5D, (double)(this.field_50013_f + 1), (double)((float)this.field_50014_g) + 0.5D, this.field_50017_b);
			++this.field_52005_h;
		} else if(!this.field_50019_a.isSitting()) {
			this.field_50019_a.func_48369_c(true);
		} else {
			--this.field_52005_h;
		}

	}

	private boolean func_50012_f() {
		int var1 = (int)this.field_50019_a.posY;
		double var2 = (double)Integer.MAX_VALUE;

		for(int var4 = (int)this.field_50019_a.posX - 8; (double)var4 < this.field_50019_a.posX + 8.0D; ++var4) {
			for(int var5 = (int)this.field_50019_a.posZ - 8; (double)var5 < this.field_50019_a.posZ + 8.0D; ++var5) {
				if(this.func_50011_a(this.field_50019_a.worldObj, var4, var1, var5) && this.field_50019_a.worldObj.isAirBlock(var4, var1 + 1, var5)) {
					double var6 = this.field_50019_a.getDistanceSq((double)var4, (double)var1, (double)var5);
					if(var6 < var2) {
						this.field_50016_e = var4;
						this.field_50013_f = var1;
						this.field_50014_g = var5;
						var2 = var6;
					}
				}
			}
		}

		return var2 < (double)Integer.MAX_VALUE;
	}

	private boolean func_50011_a(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		int var6 = var1.getBlockMetadata(var2, var3, var4);
		if(var5 == Block.chest.blockID) {
			TileEntityChest var7 = (TileEntityChest)var1.getBlockTileEntity(var2, var3, var4);
			if(var7.numUsingPlayers < 1) {
				return true;
			}
		} else {
			if(var5 == Block.stoneOvenActive.blockID) {
				return true;
			}

			if(var5 == Block.bed.blockID && !BlockBed.isBlockFootOfBed(var6)) {
				return true;
			}
		}

		return false;
	}
}
