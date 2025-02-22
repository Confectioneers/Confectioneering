package dev.imabad.confectioneering.event;

import dev.imabad.confectioneering.content.PartyRingsFeature;
import dev.imabad.confectioneering.items.ConfectionItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public class VillagerTradeEvents {

    @SubscribeEvent
    public static void onCustomTrades(VillagerTradesEvent event){
        if(event.getType() == VillagerProfession.TOOLSMITH){
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(PartyRingsFeature.RINGED_BISCUIT_CUTTER.get(), 1),
                    10, 8, 0.02f
            ));
            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(PartyRingsFeature.RINGED_BISCUIT_CUTTER.get(), 1),
                    10, 8, 0.02f
            ));
        }
    }
}
