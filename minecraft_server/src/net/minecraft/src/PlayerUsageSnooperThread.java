package net.minecraft.src;

class PlayerUsageSnooperThread extends Thread {
	final PlayerUsageSnooper field_52017_a;

	PlayerUsageSnooperThread(PlayerUsageSnooper var1, String var2) {
		super(var2);
		this.field_52017_a = var1;
	}

	public void run() {
		PostHttp.func_52010_a(PlayerUsageSnooper.func_52013_a(this.field_52017_a), PlayerUsageSnooper.func_52011_b(this.field_52017_a), true);
	}
}
