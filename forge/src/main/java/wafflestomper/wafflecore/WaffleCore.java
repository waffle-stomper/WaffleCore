package wafflestomper.wafflecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WaffleCore.MODID, version = WaffleCore.VERSION, name = WaffleCore.NAME, updateJSON = "https://raw.githubusercontent.com/waffle-stomper/WaffleCore/master/update.json")
public class WaffleCore{
	
    public static final String MODID = "wafflecore";
    public static final String VERSION = "0.1.6";
    public static final String NAME = "WaffleCore";
    
    public static WaffleCore INSTANCE;
    
    Minecraft mc;
    private boolean devEnv = true;
    public static WorldInfo worldInfo;
    private static final Logger logger = LogManager.getLogger("WaffleCore");
    
    public WaffleCore(){
    	INSTANCE = this;
    }
    
    @EventHandler
	public void preInit(FMLPreInitializationEvent event) {
    	this.mc = Minecraft.getMinecraft();
    	FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		this.devEnv = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		worldInfo = new WorldInfo();
    	worldInfo.preInit(event);
    }
}