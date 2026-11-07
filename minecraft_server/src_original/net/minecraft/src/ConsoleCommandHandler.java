package net.minecraft.src;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class ConsoleCommandHandler {
	private static Logger minecraftLogger = Logger.getLogger("Minecraft");
	private MinecraftServer minecraftServer;

	public ConsoleCommandHandler(MinecraftServer var1) {
		this.minecraftServer = var1;
	}

	public synchronized void handleCommand(ServerCommand var1) {
		String var2 = var1.command;
		String[] var3 = var2.split(" ");
		String var4 = var3[0];
		String var5 = var2.substring(var4.length()).trim();
		ICommandListener var6 = var1.commandListener;
		String var7 = var6.getUsername();
		ServerConfigurationManager var8 = this.minecraftServer.configManager;
		if(!var4.equalsIgnoreCase("help") && !var4.equalsIgnoreCase("?")) {
			if(var4.equalsIgnoreCase("list")) {
				var6.log("Connected players: " + var8.getPlayerList());
			} else if(var4.equalsIgnoreCase("stop")) {
				this.sendNoticeToOps(var7, "Stopping the server..");
				this.minecraftServer.initiateShutdown();
			} else {
				int var9;
				WorldServer var10;
				if(var4.equalsIgnoreCase("save-all")) {
					this.sendNoticeToOps(var7, "Forcing save..");
					if(var8 != null) {
						var8.savePlayerStates();
					}

					for(var9 = 0; var9 < this.minecraftServer.worldMngr.length; ++var9) {
						var10 = this.minecraftServer.worldMngr[var9];
						boolean var11 = var10.levelSaving;
						var10.levelSaving = false;
						var10.saveWorld(true, (IProgressUpdate)null);
						var10.levelSaving = var11;
					}

					this.sendNoticeToOps(var7, "Save complete.");
				} else if(var4.equalsIgnoreCase("save-off")) {
					this.sendNoticeToOps(var7, "Disabling level saving..");

					for(var9 = 0; var9 < this.minecraftServer.worldMngr.length; ++var9) {
						var10 = this.minecraftServer.worldMngr[var9];
						var10.levelSaving = true;
					}
				} else if(var4.equalsIgnoreCase("save-on")) {
					this.sendNoticeToOps(var7, "Enabling level saving..");

					for(var9 = 0; var9 < this.minecraftServer.worldMngr.length; ++var9) {
						var10 = this.minecraftServer.worldMngr[var9];
						var10.levelSaving = false;
					}
				} else if(var4.equalsIgnoreCase("op")) {
					var8.addOp(var5);
					this.sendNoticeToOps(var7, "Opping " + var5);
					var8.sendChatMessageToPlayer(var5, "\u00a7eYou are now op!");
				} else if(var4.equalsIgnoreCase("deop")) {
					var8.removeOp(var5);
					var8.sendChatMessageToPlayer(var5, "\u00a7eYou are no longer op!");
					this.sendNoticeToOps(var7, "De-opping " + var5);
				} else if(var4.equalsIgnoreCase("ban-ip")) {
					var8.banIP(var5);
					this.sendNoticeToOps(var7, "Banning ip " + var5);
				} else if(var4.equalsIgnoreCase("pardon-ip")) {
					var8.pardonIP(var5);
					this.sendNoticeToOps(var7, "Pardoning ip " + var5);
				} else {
					EntityPlayerMP var18;
					if(var4.equalsIgnoreCase("ban")) {
						var8.banPlayer(var5);
						this.sendNoticeToOps(var7, "Banning " + var5);
						var18 = var8.getPlayerEntity(var5);
						if(var18 != null) {
							var18.playerNetServerHandler.kickPlayer("Banned by admin");
						}
					} else if(var4.equalsIgnoreCase("pardon")) {
						var8.pardonPlayer(var5);
						this.sendNoticeToOps(var7, "Pardoning " + var5);
					} else {
						String var19;
						int var20;
						if(var4.equalsIgnoreCase("kick")) {
							var19 = var5;
							var18 = null;

							for(var20 = 0; var20 < var8.playerEntities.size(); ++var20) {
								EntityPlayerMP var12 = (EntityPlayerMP)var8.playerEntities.get(var20);
								if(var12.username.equalsIgnoreCase(var19)) {
									var18 = var12;
								}
							}

							if(var18 != null) {
								var18.playerNetServerHandler.kickPlayer("Kicked by admin");
								this.sendNoticeToOps(var7, "Kicking " + var18.username);
							} else {
								var6.log("Can\'t find user " + var19 + ". No kick.");
							}
						} else if(var4.equalsIgnoreCase("tp")) {
							if(var3.length == 3) {
								EntityPlayerMP var21 = var8.getPlayerEntity(var3[1]);
								var18 = var8.getPlayerEntity(var3[2]);
								if(var21 == null) {
									var6.log("Can\'t find user " + var3[1] + ". No tp.");
								} else if(var18 == null) {
									var6.log("Can\'t find user " + var3[2] + ". No tp.");
								} else if(var21.dimension != var18.dimension) {
									var6.log("User " + var3[1] + " and " + var3[2] + " are in different dimensions. No tp.");
								} else {
									var21.playerNetServerHandler.teleportTo(var18.posX, var18.posY, var18.posZ, var18.rotationYaw, var18.rotationPitch);
									this.sendNoticeToOps(var7, "Teleporting " + var3[1] + " to " + var3[2] + ".");
								}
							} else {
								var6.log("Syntax error, please provide a source and a target.");
							}
						} else if(var4.equalsIgnoreCase("give")) {
							if(var3.length != 3 && var3.length != 4 && var3.length != 5) {
								return;
							}

							var19 = var3[1];
							var18 = var8.getPlayerEntity(var19);
							if(var18 != null) {
								try {
									var20 = Integer.parseInt(var3[2]);
									if(Item.itemsList[var20] != null) {
										this.sendNoticeToOps(var7, "Giving " + var18.username + " some " + var20);
										int var22 = 1;
										int var13 = 0;
										if(var3.length > 3) {
											var22 = this.tryParse(var3[3], 1);
										}

										if(var3.length > 4) {
											var13 = this.tryParse(var3[4], 1);
										}

										if(var22 < 1) {
											var22 = 1;
										}

										if(var22 > 64) {
											var22 = 64;
										}

										var18.dropPlayerItem(new ItemStack(var20, var22, var13));
									} else {
										var6.log("There\'s no item with id " + var20);
									}
								} catch (NumberFormatException var16) {
									var6.log("There\'s no item with id " + var3[2]);
								}
							} else {
								var6.log("Can\'t find user " + var19);
							}
						} else if(var4.equalsIgnoreCase("xp")) {
							if(var3.length != 3) {
								return;
							}

							var19 = var3[1];
							var18 = var8.getPlayerEntity(var19);
							if(var18 != null) {
								try {
									var20 = Integer.parseInt(var3[2]);
									var20 = var20 > 5000 ? 5000 : var20;
									this.sendNoticeToOps(var7, "Giving " + var20 + " orbs to " + var18.username);
									var18.addExperience(var20);
								} catch (NumberFormatException var15) {
									var6.log("Invalid orb count: " + var3[2]);
								}
							} else {
								var6.log("Can\'t find user " + var19);
							}
						} else if(var4.equalsIgnoreCase("gamemode")) {
							if(var3.length != 3) {
								return;
							}

							var19 = var3[1];
							var18 = var8.getPlayerEntity(var19);
							if(var18 != null) {
								try {
									var20 = Integer.parseInt(var3[2]);
									var20 = WorldSettings.validGameType(var20);
									if(var18.itemInWorldManager.getGameType() != var20) {
										this.sendNoticeToOps(var7, "Setting " + var18.username + " to game mode " + var20);
										var18.itemInWorldManager.toggleGameType(var20);
										var18.playerNetServerHandler.sendPacket(new Packet70Bed(3, var20));
									} else {
										this.sendNoticeToOps(var7, var18.username + " already has game mode " + var20);
									}
								} catch (NumberFormatException var14) {
									var6.log("There\'s no game mode with id " + var3[2]);
								}
							} else {
								var6.log("Can\'t find user " + var19);
							}
						} else if(var4.equalsIgnoreCase("time")) {
							if(var3.length != 3) {
								return;
							}

							var19 = var3[1];

							try {
								int var23 = Integer.parseInt(var3[2]);
								WorldServer var24;
								if("add".equalsIgnoreCase(var19)) {
									for(var20 = 0; var20 < this.minecraftServer.worldMngr.length; ++var20) {
										var24 = this.minecraftServer.worldMngr[var20];
										var24.advanceTime(var24.getWorldTime() + (long)var23);
									}

									this.sendNoticeToOps(var7, "Added " + var23 + " to time");
								} else if("set".equalsIgnoreCase(var19)) {
									for(var20 = 0; var20 < this.minecraftServer.worldMngr.length; ++var20) {
										var24 = this.minecraftServer.worldMngr[var20];
										var24.advanceTime((long)var23);
									}

									this.sendNoticeToOps(var7, "Set time to " + var23);
								} else {
									var6.log("Unknown method, use either \"add\" or \"set\"");
								}
							} catch (NumberFormatException var17) {
								var6.log("Unable to convert time value, " + var3[2]);
							}
						} else if(var4.equalsIgnoreCase("say") && var5.length() > 0) {
							minecraftLogger.info("[" + var7 + "] " + var5);
							var8.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + var5));
						} else if(var4.equalsIgnoreCase("tell")) {
							if(var3.length >= 3) {
								var2 = var2.substring(var2.indexOf(" ")).trim();
								var2 = var2.substring(var2.indexOf(" ")).trim();
								minecraftLogger.info("[" + var7 + "->" + var3[1] + "] " + var2);
								var2 = "\u00a77" + var7 + " whispers " + var2;
								minecraftLogger.info(var2);
								if(!var8.sendPacketToPlayer(var3[1], new Packet3Chat(var2))) {
									var6.log("There\'s no player by that name online.");
								}
							}
						} else if(var4.equalsIgnoreCase("whitelist")) {
							this.handleWhitelist(var7, var2, var6);
						} else if(var4.equalsIgnoreCase("toggledownfall")) {
							this.minecraftServer.worldMngr[0].commandToggleDownfall();
							var6.log("Toggling rain and snow, hold on...");
						} else if(var4.equalsIgnoreCase("banlist")) {
							if(var3.length == 2) {
								if(var3[1].equals("ips")) {
									var6.log("IP Ban list:" + this.func_40648_a(this.minecraftServer.getBannedIPsList(), ", "));
								}
							} else {
								var6.log("Ban list:" + this.func_40648_a(this.minecraftServer.getBannedPlayersList(), ", "));
							}
						} else {
							minecraftLogger.info("Unknown console command. Type \"help\" for help.");
						}
					}
				}
			}
		} else {
			this.printHelp(var6);
		}

	}

	private void handleWhitelist(String var1, String var2, ICommandListener var3) {
		String[] var4 = var2.split(" ");
		if(var4.length >= 2) {
			String var5 = var4[1].toLowerCase();
			if("on".equals(var5)) {
				this.sendNoticeToOps(var1, "Turned on white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", true);
			} else if("off".equals(var5)) {
				this.sendNoticeToOps(var1, "Turned off white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", false);
			} else if("list".equals(var5)) {
				Set var6 = this.minecraftServer.configManager.getWhiteListedIPs();
				String var7 = "";

				String var9;
				for(Iterator var8 = var6.iterator(); var8.hasNext(); var7 = var7 + var9 + " ") {
					var9 = (String)var8.next();
				}

				var3.log("White-listed players: " + var7);
			} else {
				String var10;
				if("add".equals(var5) && var4.length == 3) {
					var10 = var4[2].toLowerCase();
					this.minecraftServer.configManager.addToWhiteList(var10);
					this.sendNoticeToOps(var1, "Added " + var10 + " to white-list");
				} else if("remove".equals(var5) && var4.length == 3) {
					var10 = var4[2].toLowerCase();
					this.minecraftServer.configManager.removeFromWhiteList(var10);
					this.sendNoticeToOps(var1, "Removed " + var10 + " from white-list");
				} else if("reload".equals(var5)) {
					this.minecraftServer.configManager.reloadWhiteList();
					this.sendNoticeToOps(var1, "Reloaded white-list from file");
				}
			}

		}
	}

	private void printHelp(ICommandListener var1) {
		var1.log("To run the server without a gui, start it like this:");
		var1.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
		var1.log("Console commands:");
		var1.log("   help  or  ?               shows this message");
		var1.log("   kick <player>             removes a player from the server");
		var1.log("   ban <player>              bans a player from the server");
		var1.log("   pardon <player>           pardons a banned player so that they can connect again");
		var1.log("   ban-ip <ip>               bans an IP address from the server");
		var1.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
		var1.log("   op <player>               turns a player into an op");
		var1.log("   deop <player>             removes op status from a player");
		var1.log("   tp <player1> <player2>    moves one player to the same location as another player");
		var1.log("   give <player> <id> [num]  gives a player a resource");
		var1.log("   tell <player> <message>   sends a private message to a player");
		var1.log("   stop                      gracefully stops the server");
		var1.log("   save-all                  forces a server-wide level save");
		var1.log("   save-off                  disables terrain saving (useful for backup scripts)");
		var1.log("   save-on                   re-enables terrain saving");
		var1.log("   list                      lists all currently connected players");
		var1.log("   say <message>             broadcasts a message to all players");
		var1.log("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
		var1.log("   gamemode <player> <mode>  sets player\'s game mode (0 or 1)");
		var1.log("   toggledownfall            toggles rain on or off");
		var1.log("   xp <player> <amount>      gives the player the amount of xp (0-5000)");
	}

	private void sendNoticeToOps(String var1, String var2) {
		String var3 = var1 + ": " + var2;
		this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + var3 + ")");
		minecraftLogger.info(var3);
	}

	private int tryParse(String var1, int var2) {
		try {
			return Integer.parseInt(var1);
		} catch (NumberFormatException var4) {
			return var2;
		}
	}

	private String func_40648_a(String[] var1, String var2) {
		int var3 = var1.length;
		if(0 == var3) {
			return "";
		} else {
			StringBuilder var4 = new StringBuilder();
			var4.append(var1[0]);

			for(int var5 = 1; var5 < var3; ++var5) {
				var4.append(var2).append(var1[var5]);
			}

			return var4.toString();
		}
	}
}
