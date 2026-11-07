package net.minecraft.src;

public class EntityDamageSourceIndirect extends EntityDamageSource {
	private Entity indirectEntity;

	public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
		super(var1, var2);
		this.indirectEntity = var3;
	}

	public Entity getSourceOfDamage() {
		return this.damageSourceEntity;
	}

	public Entity getEntity() {
		return this.indirectEntity;
	}

	public String getDeathMessage(EntityPlayer var1) {
		return StatCollector.translateToLocalFormatted("death." + this.damageType, new Object[]{var1.username, this.indirectEntity.getUsername()});
	}
}
