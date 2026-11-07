package net.minecraft.src;

public class EntityAIPanic extends EntityAIBase {
	private EntityCreature field_48208_a;
	private float field_48206_b;
	private double field_48207_c;
	private double field_48204_d;
	private double field_48205_e;

	public EntityAIPanic(EntityCreature var1, float var2) {
		this.field_48208_a = var1;
		this.field_48206_b = var2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.field_48208_a.getAITarget() == null) {
			return false;
		} else {
			Vec3D var1 = RandomPositionGenerator.func_48396_a(this.field_48208_a, 5, 4);
			if(var1 == null) {
				return false;
			} else {
				this.field_48207_c = var1.xCoord;
				this.field_48204_d = var1.yCoord;
				this.field_48205_e = var1.zCoord;
				return true;
			}
		}
	}

	public void startExecuting() {
		this.field_48208_a.getNavigator().func_48658_a(this.field_48207_c, this.field_48204_d, this.field_48205_e, this.field_48206_b);
	}

	public boolean continueExecuting() {
		return !this.field_48208_a.getNavigator().noPath();
	}
}
