package wafflestomper.wafflecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldInfo{
	
	private static final int MIN_DELAY_MS = 2000;
	private static long lastRequest;
	private static long lastResponse;
	private static boolean requestDelayed = false;
	private SimpleNetworkWrapper channel;
	private static String worldID;
	private static String serverAddress;
	private static String niceServerAddress;
	private static Minecraft mc;
	private static final Logger logger = LogManager.getLogger("WaffleCore:WorldInfo");
	private static final String CHANNEL_NAME = "world_identifier";
	
	
	public void preInit(FMLPreInitializationEvent event) {
		mc = Minecraft.getMinecraft();
		try{
			channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);
			if (channel != null){
				channel.registerMessage(WorldListener.class, WorldIDPacket.class, 0, Side.CLIENT);
				logger.info("Successfully registered channel '" + CHANNEL_NAME + "'");
			}
		}
		catch (RuntimeException e){
			logger.warn("Failed to register '" + CHANNEL_NAME + "' channel!");
		}
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	
	public String getWorldName(){
		if (worldID == null){
			return("NO_WORLD_NAME");
		}
		else{
			return(worldID);
		}
	}
	
	
	public String getServerAddress(){
		if (serverAddress == null){
			return("NO_SERVER_IP");
		}
		else{
			return(serverAddress);
		}
	}
	
	
	/**
	 * Returns a version of the server IP/hostname without special characters or spaces
	 * It should be suitable for file/folder naming
	 */
	private static String cleanServerAddress(String dirtyServerAddress){
		if (dirtyServerAddress.contains(":")){
			dirtyServerAddress = dirtyServerAddress.substring(0, dirtyServerAddress.indexOf(':'));
		}
		if (dirtyServerAddress.contains("/")){
			dirtyServerAddress = dirtyServerAddress.substring(0, dirtyServerAddress.indexOf('/'));
		}
		return(dirtyServerAddress);
	}
	
	
	/**
	 * Returns a version of the server IP/hostname without special characters or spaces
	 * It should be suitable for file/folder naming
	 */
	public String getNiceServerIP(){
		return(niceServerAddress);
	}
	
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!mc.isSingleplayer() && mc.thePlayer != null && !mc.thePlayer.isDead) {
			if(mc.thePlayer.getDisplayName().equals(event.getEntity().getDisplayName())) {
				serverAddress = mc.getCurrentServerData().serverIP;
				niceServerAddress = cleanServerAddress(this.getServerAddress());
				worldID = null;
				logger.warn("Joined world! Requesting ID");
				if (this.channel != null){
					requestWorldID();
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event){
		if (mc.theWorld != null && !mc.isSingleplayer() && mc.thePlayer != null && !mc.thePlayer.isDead){
			if (this.requestDelayed){
				this.requestWorldID();
			}
		}
	}
	
	
	private void requestWorldID() {
		if (channel == null){ return; }
		long now = System.currentTimeMillis();
		if((lastRequest + MIN_DELAY_MS < now) && (lastResponse + MIN_DELAY_MS < now)) {
			requestDelayed = false;
			logger.warn("Actually sending request..");
			channel.sendToServer(new WorldIDPacket());
			lastRequest = System.currentTimeMillis();
		}
		else{
			requestDelayed = true;
		}
	}
	
	
	public static class WorldListener implements IMessageHandler<WorldIDPacket, IMessage> {
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(WorldIDPacket message, MessageContext ctx) {
			lastResponse = System.currentTimeMillis();
			worldID = message.getWorldID();
			logger.info("WaffleCore:Worldinfo received world ID: " + worldID + "@" + serverAddress);
			MinecraftForge.EVENT_BUS.post(new WorldInfoEvent(worldID, serverAddress, niceServerAddress));
			return null;
		}
	}
	
	
	public static class WorldIDPacket implements IMessage {
		
		public static final String CHANNEL_NAME = "world_id";
		private String worldID;
		
		
		public WorldIDPacket() {}
		
		
		public WorldIDPacket(String worldID) {
			this.worldID = worldID;
		}
		
		
		public String getWorldID() {
			return worldID;
		}

		
		@Override
		public void fromBytes(ByteBuf buf) {
			worldID = ByteBufUtils.readUTF8String(buf);
		}
		
		
		@Override
		public void toBytes(ByteBuf buf) {
			if(worldID != null) {
				ByteBufUtils.writeUTF8String(buf, worldID);
			}
		}
	}
	
	
	public WorldInfo getInstance(){
		return this;
	}
}