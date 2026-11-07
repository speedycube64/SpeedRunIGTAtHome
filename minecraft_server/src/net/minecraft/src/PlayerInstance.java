package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

class PlayerInstance {
	private List players;
	private int chunkX;
	private int chunkZ;
	private ChunkCoordIntPair currentChunk;
	private short[] blocksToUpdate;
	private int numBlocksToUpdate;
	private int field_48475_h;
	final PlayerManager playerManager;

	public PlayerInstance(PlayerManager var1, int var2, int var3) {
		this.playerManager = var1;
		this.players = new ArrayList();
		this.blocksToUpdate = new short[64];
		this.numBlocksToUpdate = 0;
		this.chunkX = var2;
		this.chunkZ = var3;
		this.currentChunk = new ChunkCoordIntPair(var2, var3);
		var1.getMinecraftServer().chunkProviderServer.loadChunk(var2, var3);
	}

	public void addPlayer(EntityPlayerMP var1) {
		if(this.players.contains(var1)) {
			throw new IllegalStateException("Failed to add player. " + var1 + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
		} else {
			var1.listeningChunks.add(this.currentChunk);
			var1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, true));
			this.players.add(var1);
			var1.loadedChunks.add(this.currentChunk);
		}
	}

	public void removePlayer(EntityPlayerMP var1) {
		if(this.players.contains(var1)) {
			this.players.remove(var1);
			if(this.players.size() == 0) {
				long var2 = (long)this.chunkX + 2147483647L | (long)this.chunkZ + 2147483647L << 32;
				PlayerManager.getPlayerInstances(this.playerManager).remove(var2);
				if(this.numBlocksToUpdate > 0) {
					PlayerManager.getPlayerInstancesToUpdate(this.playerManager).remove(this);
				}

				this.playerManager.getMinecraftServer().chunkProviderServer.dropChunk(this.chunkX, this.chunkZ);
			}

			var1.loadedChunks.remove(this.currentChunk);
			if(var1.listeningChunks.contains(this.currentChunk)) {
				var1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
			}

		}
	}

	public void markBlockNeedsUpdate(int var1, int var2, int var3) {
		if(this.numBlocksToUpdate == 0) {
			PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
		}

		this.field_48475_h |= 1 << (var2 >> 4);
		if(this.numBlocksToUpdate < 64) {
			short var4 = (short)(var1 << 12 | var3 << 8 | var2);

			for(int var5 = 0; var5 < this.numBlocksToUpdate; ++var5) {
				if(this.blocksToUpdate[var5] == var4) {
					return;
				}
			}

			this.blocksToUpdate[this.numBlocksToUpdate++] = var4;
		}

	}

	public void sendPacketToPlayersInInstance(Packet var1) {
		for(int var2 = 0; var2 < this.players.size(); ++var2) {
			EntityPlayerMP var3 = (EntityPlayerMP)this.players.get(var2);
			if(var3.listeningChunks.contains(this.currentChunk) && !var3.loadedChunks.contains(this.currentChunk)) {
				var3.playerNetServerHandler.sendPacket(var1);
			}
		}

	}

	public void onUpdate() {
		WorldServer var1 = this.playerManager.getMinecraftServer();
		if(this.numBlocksToUpdate != 0) {
			int var2;
			int var3;
			int var4;
			if(this.numBlocksToUpdate == 1) {
				var2 = this.chunkX * 16 + (this.blocksToUpdate[0] >> 12 & 15);
				var3 = this.blocksToUpdate[0] & 255;
				var4 = this.chunkZ * 16 + (this.blocksToUpdate[0] >> 8 & 15);
				this.sendPacketToPlayersInInstance(new Packet53BlockChange(var2, var3, var4, var1));
				if(var1.func_48084_h(var2, var3, var4)) {
					this.updateTileEntity(var1.getBlockTileEntity(var2, var3, var4));
				}
			} else {
				int var5;
				if(this.numBlocksToUpdate == 64) {
					var2 = this.chunkX * 16;
					var3 = this.chunkZ * 16;
					this.sendPacketToPlayersInInstance(new Packet51MapChunk(var1.getChunkFromChunkCoords(this.chunkX, this.chunkZ), false, this.field_48475_h));

					for(var4 = 0; var4 < 16; ++var4) {
						if((this.field_48475_h & 1 << var4) != 0) {
							var5 = var4 << 4;
							List var6 = var1.getTileEntityList(var2, var5, var3, var2 + 16, var5 + 16, var3 + 16);

							for(int var7 = 0; var7 < var6.size(); ++var7) {
								this.updateTileEntity((TileEntity)var6.get(var7));
							}
						}
					}
				} else {
					this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, var1));

					for(var2 = 0; var2 < this.numBlocksToUpdate; ++var2) {
						var3 = this.chunkX * 16 + (this.blocksToUpdate[var2] >> 12 & 15);
						var4 = this.blocksToUpdate[var2] & 255;
						var5 = this.chunkZ * 16 + (this.blocksToUpdate[var2] >> 8 & 15);
						if(var1.func_48084_h(var3, var4, var5)) {
							this.updateTileEntity(var1.getBlockTileEntity(var3, var4, var5));
						}
					}
				}
			}

			this.numBlocksToUpdate = 0;
			this.field_48475_h = 0;
		}
	}

	private void updateTileEntity(TileEntity var1) {
		if(var1 != null) {
			Packet var2 = var1.getDescriptionPacket();
			if(var2 != null) {
				this.sendPacketToPlayersInInstance(var2);
			}
		}

	}
}
