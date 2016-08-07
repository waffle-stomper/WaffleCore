package wafflestomper.wafflecore;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WorldInfoEvent extends Event{
	public String worldID;
	public String dirtyServerAddress;
	public String cleanServerAddress;
	
	public WorldInfoEvent(String _worldID, String _dirtyServerAddress, String _cleanServerAddress){
		this.worldID = _worldID;
		this.dirtyServerAddress = _dirtyServerAddress;
		this.cleanServerAddress = _cleanServerAddress;
	}
}