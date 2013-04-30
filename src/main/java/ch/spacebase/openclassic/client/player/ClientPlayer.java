package ch.spacebase.openclassic.client.player;

import java.net.SocketAddress;
import java.util.List;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.Position;
import ch.spacebase.openclassic.api.data.NBTData;
import ch.spacebase.openclassic.api.event.EventFactory;
import ch.spacebase.openclassic.api.event.player.PlayerTeleportEvent;
import ch.spacebase.openclassic.api.level.Level;
import ch.spacebase.openclassic.api.network.msg.PlayerChatMessage;
import ch.spacebase.openclassic.api.permissions.Group;
import ch.spacebase.openclassic.api.player.Player;
import ch.spacebase.openclassic.api.player.Session;
import ch.spacebase.openclassic.api.plugin.RemotePluginInfo;
import ch.spacebase.openclassic.api.util.Constants;
import ch.spacebase.openclassic.client.level.ClientLevel;
import ch.spacebase.openclassic.client.util.GeneralUtils;

public class ClientPlayer implements Player {
	
	private com.mojang.minecraft.player.Player handle;
	private ClientSession session;
	private byte placeMode = 0;
	private NBTData data = new NBTData("Player");
	
	public ClientPlayer(com.mojang.minecraft.player.Player handle) {
		this.handle = handle;
		this.session = new ClientSession(this);
		
		this.data.load(OpenClassic.getClient().getDirectory().getPath() + "/player.nbt");
	}
	
	@Override
	public void sendMessage(String message) {
		OpenClassic.getClient().getMainScreen().addChat(message);
	}

	@Override
	public boolean hasPermission(String permission) {
		return true;
	}

	@Override
	public String getCommandPrefix() {
		return "/";
	}

	@Override
	public Session getSession() {
		return this.session;
	}

	@Override
	public byte getPlayerId() {
		return -1;
	}

	@Override
	public Position getPosition() {
		return new Position(this.handle.level.openclassic, this.handle.x, this.handle.y, this.handle.z, (byte) this.handle.yRot, (byte) this.handle.xRot);
	}

	@Override
	public String getName() {
		if(GeneralUtils.getMinecraft().data == null) return "Player";
		return GeneralUtils.getMinecraft().data.username;
	}

	@Override
	public String getDisplayName() {
		return this.getName();
	}

	@Override
	public void setDisplayName(String name) {
	}

	@Override
	public byte getPlaceMode() {
		return this.placeMode;
	}

	@Override
	public void setPlaceMode(int type) {
		this.placeMode = (byte) type;
	}

	@Override
	public void moveTo(Position pos) {
		this.moveTo(pos.getLevel(), pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
	}

	@Override
	public void moveTo(float x, float y, float z) {
		this.moveTo(this.handle.level.openclassic, x, y, z, (byte) 0, (byte) 0);
	}

	@Override
	public void moveTo(float x, float y, float z, float yaw, float pitch) {
		this.moveTo(this.handle.level.openclassic, x, y, z, yaw, pitch);
	}

	@Override
	public void moveTo(Level level, float x, float y, float z) {
		this.moveTo(level, x, y, z, (byte) 0, (byte) 0);
	}

	@Override
	public void moveTo(Level level, float x, float y, float z, float yaw, float pitch) {
		PlayerTeleportEvent event = EventFactory.callEvent(new PlayerTeleportEvent(this, this.getPosition(), new Position(level, x, y, z, yaw, pitch)));
		if(event.isCancelled()) {
			return;
		}
		
		if(event.getTo().getLevel() != null && !this.handle.level.name.equals(event.getTo().getLevel().getName())) {
			this.handle.setLevel(((ClientLevel) event.getTo().getLevel()).getHandle());
			GeneralUtils.getMinecraft().setLevel(((ClientLevel) event.getTo().getLevel()).getHandle());
		}
		
		this.handle.moveTo(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ(), event.getTo().getYaw(), event.getTo().getPitch());
	}

	@Override
	public Group getGroup() {
		return null;
	}

	@Override
	public void setGroup(Group group) {
	}

	@Override
	public String getIp() {
		return this.getAddress().toString().replace("/", "").split(":")[0];
	}

	@Override
	public SocketAddress getAddress() {
		return this.session.getAddress();
	}

	@Override
	public void disconnect(String reason) {
		this.session.disconnect(reason);
	}
	
	public com.mojang.minecraft.player.Player getHandle() {
		return this.handle;
	}

	@Override
	public boolean hasCustomClient() {
		return true;
	}

	@Override
	public String getClientVersion() {
		return Constants.CLIENT_VERSION;
	}

	@Override
	public NBTData getData() {
		return this.data;
	}

	@Override
	public List<RemotePluginInfo> getPlugins() {
		return GeneralUtils.getMinecraft().serverPlugins;
	}

	@Override
	public void chat(String message) {
		this.session.send(new PlayerChatMessage((byte) -1, message));
	}

	@Override
	public void hidePlayer(Player player) {
	}

	@Override
	public void showPlayer(Player player) {
	}

	@Override
	public boolean canSee(Player player) {
		return true;
	}

	@Override
	public String getLanguage() {
		return OpenClassic.getGame().getLanguage();
	}

}
