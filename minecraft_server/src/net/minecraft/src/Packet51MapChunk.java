package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {
	public int xCh;
	public int zCh;
	public int yChMin;
	public int yChMax;
	public byte[] chunkData;
	public boolean includeInitialize;
	private int tempLength;
	private int field_48110_h;
	private static byte[] temp = new byte[0];

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(Chunk var1, boolean var2, int var3) {
		this.isChunkDataPacket = true;
		this.xCh = var1.xPosition;
		this.zCh = var1.zPosition;
		this.includeInitialize = var2;
		if(var2) {
			var3 = '\uffff';
			var1.field_50025_o = true;
		}

		ExtendedBlockStorage[] var4 = var1.getBlockStorageArray();
		int var5 = 0;
		int var6 = 0;

		int var7;
		for(var7 = 0; var7 < var4.length; ++var7) {
			if(var4[var7] != null && (!var2 || !var4[var7].getIsEmpty()) && (var3 & 1 << var7) != 0) {
				this.yChMin |= 1 << var7;
				++var5;
				if(var4[var7].getBlockMSBArray() != null) {
					this.yChMax |= 1 << var7;
					++var6;
				}
			}
		}

		var7 = 2048 * (5 * var5 + var6);
		if(var2) {
			var7 += 256;
		}

		if(temp.length < var7) {
			temp = new byte[var7];
		}

		byte[] var8 = temp;
		int var9 = 0;

		int var10;
		for(var10 = 0; var10 < var4.length; ++var10) {
			if(var4[var10] != null && (!var2 || !var4[var10].getIsEmpty()) && (var3 & 1 << var10) != 0) {
				byte[] var11 = var4[var10].func_48590_g();
				System.arraycopy(var11, 0, var8, var9, var11.length);
				var9 += var11.length;
			}
		}

		NibbleArray var15;
		for(var10 = 0; var10 < var4.length; ++var10) {
			if(var4[var10] != null && (!var2 || !var4[var10].getIsEmpty()) && (var3 & 1 << var10) != 0) {
				var15 = var4[var10].func_48594_i();
				System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
				var9 += var15.data.length;
			}
		}

		for(var10 = 0; var10 < var4.length; ++var10) {
			if(var4[var10] != null && (!var2 || !var4[var10].getIsEmpty()) && (var3 & 1 << var10) != 0) {
				var15 = var4[var10].getBlocklightArray();
				System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
				var9 += var15.data.length;
			}
		}

		for(var10 = 0; var10 < var4.length; ++var10) {
			if(var4[var10] != null && (!var2 || !var4[var10].getIsEmpty()) && (var3 & 1 << var10) != 0) {
				var15 = var4[var10].getSkylightArray();
				System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
				var9 += var15.data.length;
			}
		}

		if(var6 > 0) {
			for(var10 = 0; var10 < var4.length; ++var10) {
				if(var4[var10] != null && (!var2 || !var4[var10].getIsEmpty()) && var4[var10].getBlockMSBArray() != null && (var3 & 1 << var10) != 0) {
					var15 = var4[var10].getBlockMSBArray();
					System.arraycopy(var15.data, 0, var8, var9, var15.data.length);
					var9 += var15.data.length;
				}
			}
		}

		if(var2) {
			byte[] var16 = var1.getBiomeArray();
			System.arraycopy(var16, 0, var8, var9, var16.length);
			var9 += var16.length;
		}

		Deflater var17 = new Deflater(-1);

		try {
			var17.setInput(var8, 0, var9);
			var17.finish();
			this.chunkData = new byte[var9];
			this.tempLength = var17.deflate(this.chunkData);
		} finally {
			var17.end();
		}

	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.xCh = var1.readInt();
		this.zCh = var1.readInt();
		this.includeInitialize = var1.readBoolean();
		this.yChMin = var1.readShort();
		this.yChMax = var1.readShort();
		this.tempLength = var1.readInt();
		this.field_48110_h = var1.readInt();
		if(temp.length < this.tempLength) {
			temp = new byte[this.tempLength];
		}

		var1.readFully(temp, 0, this.tempLength);
		int var2 = 0;

		int var3;
		for(var3 = 0; var3 < 16; ++var3) {
			var2 += this.yChMin >> var3 & 1;
		}

		var3 = 12288 * var2;
		if(this.includeInitialize) {
			var3 += 256;
		}

		this.chunkData = new byte[var3];
		Inflater var4 = new Inflater();
		var4.setInput(temp, 0, this.tempLength);

		try {
			var4.inflate(this.chunkData);
		} catch (DataFormatException var9) {
			throw new IOException("Bad compressed data format");
		} finally {
			var4.end();
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeInt(this.xCh);
		var1.writeInt(this.zCh);
		var1.writeBoolean(this.includeInitialize);
		var1.writeShort((short)(this.yChMin & '\uffff'));
		var1.writeShort((short)(this.yChMax & '\uffff'));
		var1.writeInt(this.tempLength);
		var1.writeInt(this.field_48110_h);
		var1.write(this.chunkData, 0, this.tempLength);
	}

	public void processPacket(NetHandler var1) {
		var1.func_48070_a(this);
	}

	public int getPacketSize() {
		return 17 + this.tempLength;
	}
}
