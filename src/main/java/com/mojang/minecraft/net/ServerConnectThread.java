package com.mojang.minecraft.net;

import java.net.InetSocketAddress;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.event.EventFactory;
import ch.spacebase.openclassic.api.event.player.PlayerConnectEvent;
import ch.spacebase.openclassic.api.event.player.PlayerConnectEvent.Result;
import ch.spacebase.openclassic.api.util.Constants;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

public final class ServerConnectThread extends Thread {

	private String server;
	private int port;
	private String username;
	private String key;
	private Minecraft mc;
	private NetworkManager netManager;

	public ServerConnectThread(NetworkManager netManager, String server, int port, String username, String key, Minecraft mc) {
		super("Client-Server Connect Thread");
		this.netManager = netManager;
		this.server = server;
		this.port = port;
		this.username = username;
		this.key = key;
		this.mc = mc;
	}

	public void run() {
		try {
			this.netManager.netHandler = new NetworkHandler(this.server, this.port, this.netManager.mc);
			this.netManager.netHandler.netManager = this.netManager;
			PlayerConnectEvent event = EventFactory.callEvent(new PlayerConnectEvent(this.mc.data.username, InetSocketAddress.createUnresolved(this.server, this.port)));
			if(event.getResult() != Result.ALLOWED) {
				this.mc.online = false;
				this.mc.netManager = null;
				this.mc.setCurrentScreen(new ErrorScreen(OpenClassic.getGame().getTranslator().translate("disconnect.plugin-disallow"), event.getKickMessage()));
				this.netManager.successful = false;
				return;
			}
			
			this.netManager.netHandler.send(PacketType.IDENTIFICATION, new Object[] { Constants.PROTOCOL_VERSION, this.username, this.key, Constants.OPENCLASSIC_PROTOCOL_VERSION });
			this.netManager.successful = true;
		} catch (Exception e) {
			this.mc.online = false;
			this.mc.netManager = null;
			this.mc.setCurrentScreen(new ErrorScreen(OpenClassic.getGame().getTranslator().translate("connecting.fail-connect"), OpenClassic.getGame().getTranslator().translate("connecting.probably-down")));
			e.printStackTrace();
			this.netManager.successful = false;
		}
	}
}
