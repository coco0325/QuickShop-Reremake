package org.maxgamer.quickshop.event;

import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OfflineSupportListener implements Listener {

    @EventHandler
    public void onSyncComplete(SyncCompleteEvent e){
        if(e.getPlayer().isOnline()){

        }
    }
}
