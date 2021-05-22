package alteran;

import alteran.commands.AlteranCommands;
import alteran.common.AlteranCommon;
import alteran.network.AlteranNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlteranCommon.modId)
public class Alteran {
  public Alteran() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    IEventBus forgeBus = MinecraftForge.EVENT_BUS;

    modBus.addListener(this::onCommonSetup);
    forgeBus.addListener(this::serverLoad);
  }

  public void onCommonSetup(FMLCommonSetupEvent e) {
    AlteranNetwork.registerMessages("alteran");
  }

  public void serverLoad(RegisterCommandsEvent event) {
    AlteranCommands.register(event.getDispatcher());
  }
}
