package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChunkProviderServer implements IChunkProvider {
	private Set droppedChunksSet = new HashSet();
	private Chunk dummyChunk;
	private IChunkProvider serverChunkGenerator;
	private IChunkLoader chunkLoader;
	public boolean chunkLoadOverride = false;
	private LongHashMap id2ChunkMap = new LongHashMap();
	private List field_727_f = new ArrayList();
	private WorldServer world;

	public ChunkProviderServer(WorldServer var1, IChunkLoader var2, IChunkProvider var3) {
		this.dummyChunk = new EmptyChunk(var1, 0, 0);
		this.world = var1;
		this.chunkLoader = var2;
		this.serverChunkGenerator = var3;
	}

	public boolean chunkExists(int var1, int var2) {
		return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
	}

	public void dropChunk(int var1, int var2) {
		if(this.world.worldProvider.canRespawnHere()) {
			ChunkCoordinates var3 = this.world.getSpawnPoint();
			int var4 = var1 * 16 + 8 - var3.posX;
			int var5 = var2 * 16 + 8 - var3.posZ;
			short var6 = 128;
			if(var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6) {
				this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(var1, var2)));
			}
		} else {
			this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(var1, var2)));
		}

	}

	public void unloadAllChunks() {
		Iterator var1 = this.field_727_f.iterator();

		while(var1.hasNext()) {
			Chunk var2 = (Chunk)var1.next();
			this.dropChunk(var2.xPosition, var2.zPosition);
		}

	}

	public Chunk loadChunk(int var1, int var2) {
		long var3 = ChunkCoordIntPair.chunkXZ2Int(var1, var2);
		this.droppedChunksSet.remove(Long.valueOf(var3));
		Chunk var5 = (Chunk)this.id2ChunkMap.getValueByKey(var3);
		if(var5 == null) {
			var5 = this.loadChunkFromFile(var1, var2);
			if(var5 == null) {
				if(this.serverChunkGenerator == null) {
					var5 = this.dummyChunk;
				} else {
					var5 = this.serverChunkGenerator.provideChunk(var1, var2);
				}
			}

			this.id2ChunkMap.add(var3, var5);
			this.field_727_f.add(var5);
			if(var5 != null) {
				var5.func_4053_c();
				var5.onChunkLoad();
			}

			var5.populateChunk(this, this, var1, var2);
		}

		return var5;
	}

	public Chunk provideChunk(int var1, int var2) {
		Chunk var3 = (Chunk)this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
		return var3 == null ? (!this.world.findingSpawnPoint && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(var1, var2)) : var3;
	}

	private Chunk loadChunkFromFile(int var1, int var2) {
		if(this.chunkLoader == null) {
			return null;
		} else {
			try {
				Chunk var3 = this.chunkLoader.loadChunk(this.world, var1, var2);
				if(var3 != null) {
					var3.lastSaveTime = this.world.getWorldTime();
				}

				return var3;
			} catch (Exception var4) {
				var4.printStackTrace();
				return null;
			}
		}
	}

	private void saveChunkExtraData(Chunk var1) {
		if(this.chunkLoader != null) {
			try {
				this.chunkLoader.saveExtraChunkData(this.world, var1);
			} catch (Exception var3) {
				var3.printStackTrace();
			}

		}
	}

	private void saveChunkData(Chunk var1) {
		if(this.chunkLoader != null) {
			try {
				var1.lastSaveTime = this.world.getWorldTime();
				this.chunkLoader.saveChunk(this.world, var1);
			} catch (IOException var3) {
				var3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider var1, int var2, int var3) {
		Chunk var4 = this.provideChunk(var2, var3);
		if(!var4.isTerrainPopulated) {
			var4.isTerrainPopulated = true;
			if(this.serverChunkGenerator != null) {
				this.serverChunkGenerator.populate(var1, var2, var3);
				var4.setChunkModified();
			}
		}

	}

	public boolean saveChunks(boolean var1, IProgressUpdate var2) {
		int var3 = 0;

		for(int var4 = 0; var4 < this.field_727_f.size(); ++var4) {
			Chunk var5 = (Chunk)this.field_727_f.get(var4);
			if(var1) {
				this.saveChunkExtraData(var5);
			}

			if(var5.needsSaving(var1)) {
				this.saveChunkData(var5);
				var5.isModified = false;
				++var3;
				if(var3 == 24 && !var1) {
					return false;
				}
			}
		}

		if(var1) {
			if(this.chunkLoader == null) {
				return true;
			}

			this.chunkLoader.saveExtraData();
		}

		return true;
	}

	public boolean unload100OldestChunks() {
		if(!this.world.levelSaving) {
			for(int var1 = 0; var1 < 100; ++var1) {
				if(!this.droppedChunksSet.isEmpty()) {
					Long var2 = (Long)this.droppedChunksSet.iterator().next();
					Chunk var3 = (Chunk)this.id2ChunkMap.getValueByKey(var2.longValue());
					var3.onChunkUnload();
					this.saveChunkData(var3);
					this.saveChunkExtraData(var3);
					this.droppedChunksSet.remove(var2);
					this.id2ChunkMap.remove(var2.longValue());
					this.field_727_f.remove(var3);
				}
			}

			if(this.chunkLoader != null) {
				this.chunkLoader.chunkTick();
			}
		}

		return this.serverChunkGenerator.unload100OldestChunks();
	}

	public boolean canSave() {
		return !this.world.levelSaving;
	}

	public String func_46040_d() {
		return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
	}

	public List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4) {
		return this.serverChunkGenerator.getPossibleCreatures(var1, var2, var3, var4);
	}

	public ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5) {
		return this.serverChunkGenerator.findClosestStructure(var1, var2, var3, var4, var5);
	}
}
