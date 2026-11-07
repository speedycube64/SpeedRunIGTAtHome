package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ThreadServerSleep extends Thread {
	final MinecraftServer mc;

	public ThreadServerSleep(MinecraftServer var1) {
		this.mc = var1;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(2147483647L);
			} catch (InterruptedException var2) {
			}
		}
	}
}
