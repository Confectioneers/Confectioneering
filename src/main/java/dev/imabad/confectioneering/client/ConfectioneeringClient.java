package dev.imabad.confectioneering.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ConfectioneeringClient {

    public static void clientConstruct(IEventBus modEventBus, IEventBus forgeEventBus){
        modEventBus.addListener(ConfectioneeringClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event){
        ConfectionPartialModels.init();
    }
}
