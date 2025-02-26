package bidahochi.foxdrives;

import bidahochi.foxdrives.entities.*;
import bidahochi.foxdrives.util.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cpw.mods.fml.common.registry.EntityRegistry.registerModEntity;

@Mod(modid = FoxDrives.MODID, version = FoxDrives.MOD_VERSION, name = "FoxDrives")
public class FoxDrives {
    public static final String MODID = "foxdrives";
    public static final String MOD_VERSION = "0.2.2";

    //used for TMT render caching. false improves performance dramatically, true can fix rare bugs.
    public static boolean disableCache=false;


    @Mod.Instance(MODID)

    public static FoxDrives instance;

    /**
     *Setup the proxy, this is used for managing some of the client and server specific features.
     *@see CommonProxy
     *@see ClientProxy
     */
    @SidedProxy(clientSide = "bidahochi.foxdrives.util.ClientProxy", serverSide = "bidahochi.foxdrives.util.CommonProxy")
    public static CommonProxy proxy;


    //the 'paintbucket' item
    public static Item wrap;
    //the creative tab
    public static CreativeTabs tab;
    //the networking channel
    public static SimpleNetworkWrapper interactChannel;
    //the entityID for the first entity registered. must be 18 or higher because forge is dumb.
    private static int registryPosition=18;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        //full length addresses for client only classes must be used to prevent
        // the import from being loaded on server and crashing.
        if(event.getSide().isClient()){
            cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(bidahochi.foxdrives.util.ClientProxy.KeyInventory);
            cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(bidahochi.foxdrives.util.ClientProxy.KeyBrake);

        }
        //init networking stuff
        interactChannel = NetworkRegistry.INSTANCE.newSimpleChannel("FD.key");
        interactChannel.registerMessage(HANDLERS[0], PacketInteract.class, 1, Side.SERVER);

        //init item stuff
        tab= new FoxTab("FoxDrives", "creativetab");
        wrap= RegisterItem(new Item(),"wrap", tab);

        //Fill CarType Registry
        CarType.TOYOTA_PICKUP_1992 = CarType.register("toyota_pickup_1992", EntityToyotaPickup1992.class)
            .recipe(
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane), new ItemStack(Blocks.stone),
                new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs), new ItemStack(Blocks.iron_door),
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane), new ItemStack(Blocks.stone)
            )
            .passpos(
                new float[]{ 0.3f, 0.25f, 0.25f },
                new float[]{ -0.3f, 0.25f, 0.25f }
            )
            .maxspeed(8, 7);;

        CarType.REDMUND_1972 = CarType.register("redmund_1972", EntityRedmund1972.class)
            .recipe(
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
            )
            .passpos(
                    new float[]{ 0.3f, 0.25f, 0.25f },
                    new float[]{ -0.3f, 0.25f, 0.25f }
                    )
            .acceleration(0.75f)
            .maxspeed(8, 6);

        CarType.WORKDAY_1980 = CarType.register("workday_1980", EntityWorkday1980.class)
                .recipe(
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(
                        new float[]{ 0.3f, 0.25f, 0.25f },
                        new float[]{ -0.3f, 0.25f, 0.25f }
                        )
                .acceleration(0.75f)
                .maxspeed(20, 10);

        CarType.WORKDAY_1980_Utility = CarType.register("workday_1980_utility", EntityWorkday1980Utility.class)
                .recipe(
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(
                        new float[]{ 0.3f, 0.25f, 0.25f },
                        new float[]{ -0.3f, 0.25f, 0.25f }
                )
                .acceleration(0.75f)
                .maxspeed(20, 10);

        CarType.WORKDAY_1980_Utility_Hirail = CarType.register("workday_1980_utility_hirail", EntityWorkday1980UtilityHirail.class)
                .recipe(
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(
                        new float[]{ 0.3f, 0.25f, 0.25f },
                        new float[]{ -0.3f, 0.25f, 0.25f }
                )
                .acceleration(0.75f)
                .maxspeed(20, 10);


        CarType.GILLIG_PHANTOM = CarType.register("gillig_phantom", EntityGilligPhantom.class)
            .recipe(
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
            )
            .passpos(
                new float[]{ 0.3f, 0.25f, 1.5f },
                new float[]{ -.3f, 0.25f, 1f },
                new float[]{ 0.3f, 0.25f, 1f },
                new float[]{ -.3f, 0.25f, 0.5f },
                new float[]{ 0.3f, 0.25f, 0.5f },
                new float[]{ -.3f, 0.25f, 0f },
                new float[]{ 0.3f, 0.25f, 0f },
                new float[]{ -.3f, 0.25f, -.5f },
                new float[]{ 0.3f, 0.25f, -.5f }
            )
            .maxspeed(6, 3);

        CarType.HYSTER_H80FT = CarType.register("hyster_H80FT", EntityHysterH80FT.class)
            .recipe(
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
            )
            .passpos(new float[]{ 0.0f, 0.5f, 0.0f })
            .rearsteer(true)
            .maxspeed(3, 3);

        CarType.MKDS_Standard = CarType.register("mkds_standard", EntityMKDS_Standard.class)
                .recipe(
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(new float[]{ 0.0f, 0.0f, 0.25f })//LR, UD, FB
                .acceleration(0.9f)
                .maxspeed(9, 4);

        CarType.WagonKart = CarType.register("wagonkart", EntityWagonKart.class)
                .recipe(
                        new ItemStack(Blocks.wooden_button), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(new float[]{ 0.0f, 0.3f, 0.0f })//LR, UD, FB
                .acceleration(0.9f)
                .maxspeed(9, 4);

        CarType.TurboToilet = CarType.register("turbotoilet", EntityTurboToilet.class)
                .recipe(
                        new ItemStack(Blocks.wooden_button), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
                )
                .passpos(new float[]{ 0.0f, 0.3f, 0.25f })//LR, UD, FB
                .acceleration(0.9f)
                .maxspeed(9, 4);

        //CarType Registry Entry registration
        for(CarType type : CarType.REGISTRY.values()){
            registerModEntity(type.clazz, MODID + "." + type.regname + ".entity", registryPosition, FoxDrives.instance, 1600, 3, true);
            RegisterItem(type.getItem(), type.regname, tab);
            if(type.getRecipe() != null){
                String firstRow="";
                firstRow+=type.getRecipe()[0]!=null?"A":" ";
                firstRow+=type.getRecipe()[1]!=null?"B":" ";
                firstRow+=type.getRecipe()[2]!=null?"C":" ";
                String secondRow="";
                secondRow+=type.getRecipe()[3]!=null?"D":" ";
                secondRow+=type.getRecipe()[4]!=null?"E":" ";
                secondRow+=type.getRecipe()[5]!=null?"F":" ";
                String thirdRow="";
                thirdRow+=type.getRecipe()[6]!=null?"G":" ";
                thirdRow+=type.getRecipe()[7]!=null?"H":" ";
                thirdRow+=type.getRecipe()[8]!=null?"I":" ";

                List<Object> recipe = new ArrayList<>();
                recipe.add(firstRow);
                recipe.add(secondRow);
                recipe.add(thirdRow);

                //loop for all the items in the array, then make an entry for the ones that exist
                //use a char array for a simple shorthand to figure out which char to use the the matching ID.
                char[] c = {'A','B','C','D','E','F','G','H','I'};
                for(int i=0; i<type.getRecipe().length;i++){
                    if(type.getRecipe()[i]!=null){
                        recipe.add(c[i]);
                        recipe.add(type.getRecipe()[i]);
                    }
                }
                GameRegistry.addRecipe(new ItemStack(type.getItem()), recipe.toArray());
            }
            proxy.registerCarRenderer(type.clazz);
            registryPosition++;
        }

        //register player scaler
        proxy.registerPlayerScaler();

        //register seat entity
        registerModEntity(EntitySeat.class, MODID + ".seat.entity", registryPosition, FoxDrives.instance, 1600, 1, true);

        //register the event handler, mainly for tracking inputs
        if(event.getSide().isClient()){
            MinecraftForge.EVENT_BUS.register(EventManager.instance);
            FMLCommonHandler.instance().bus().register(EventManager.instance);
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
    }

    //shorthand for registering an item, taken from TiM.
    public static Item RegisterItem(Item itm, String unlocalizedName, @Nullable CreativeTabs tab) {
        itm.setUnlocalizedName(MODID + "." + unlocalizedName);
        if (tab != null) {
            itm.setCreativeTab(tab);
        }
        if (FoxDrives.proxy.isClient()) {
            itm.setTextureName(MODID + ":" + unlocalizedName);
        }
        GameRegistry.registerItem(itm, unlocalizedName);
        return itm;
    }

    //each packet needs it's own entry in this, duplicates are not allowed, for whatever reason
    private static final IMessageHandler[] HANDLERS = new IMessageHandler[]{
            new IMessageHandler<IMessage, IMessage>() {
                @Override public IMessage onMessage(IMessage message, MessageContext ctx) {return null;}
            }
    };

}
