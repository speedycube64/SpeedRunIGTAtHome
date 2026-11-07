package net.minecraft.src;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
	EntityTameable field_48304_a;
	EntityLiving field_48303_b;

	public EntityAIOwnerHurtTarget(EntityTameable var1) {
		super(var1, 32.0F, false);
		this.field_48304_a = var1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(!this.field_48304_a.isTamed()) {
			return false;
		} else {
			EntityLiving var1 = this.field_48304_a.getOwner();
			if(var1 == null) {
				return false;
			} else {
				this.field_48303_b = var1.getLastAttackingEntity();
				return this.func_48284_a(this.field_48303_b, false);
			}
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.field_48303_b);
		super.startExecuting();
	}
}
