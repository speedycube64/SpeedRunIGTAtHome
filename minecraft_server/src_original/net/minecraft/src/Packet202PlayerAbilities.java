package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet202PlayerAbilities extends Packet {
	public boolean field_50007_a = false;
	public boolean field_50005_b = false;
	public boolean field_50006_c = false;
	public boolean field_50004_d = false;

	public Packet202PlayerAbilities() {
	}

	public Packet202PlayerAbilities(PlayerCapabilities var1) {
		this.field_50007_a = var1.disableDamage;
		this.field_50005_b = var1.isFlying;
		this.field_50006_c = var1.allowFlying;
		this.field_50004_d = var1.isCreativeMode;
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.field_50007_a = var1.readBoolean();
		this.field_50005_b = var1.readBoolean();
		this.field_50006_c = var1.readBoolean();
		this.field_50004_d = var1.readBoolean();
	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeBoolean(this.field_50007_a);
		var1.writeBoolean(this.field_50005_b);
		var1.writeBoolean(this.field_50006_c);
		var1.writeBoolean(this.field_50004_d);
	}

	public void processPacket(NetHandler var1) {
		var1.func_50003_a(this);
	}

	public int getPacketSize() {
		return 1;
	}
}
