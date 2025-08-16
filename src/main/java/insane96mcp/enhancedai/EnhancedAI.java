package insane96mcp.enhancedai;

import insane96mcp.enhancedai.modules.animal.feature.AnimalAttacking;
import insane96mcp.enhancedai.modules.base.feature.Attacking;
import insane96mcp.enhancedai.modules.base.feature.Targeting;
import insane96mcp.enhancedai.setup.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.DebugPathCommand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static insane96mcp.enhancedai.ModBlocks.BLOCKS;
import static insane96mcp.enhancedai.ModItems.ITEMS;
import static insane96mcp.enhancedai.ModTileEntities.TILE_ENTITIES;
import static insane96mcp.enhancedai.modules.zombie.ai.DiggingGoal.protectedAreas;

@Mod(EnhancedAI.MOD_ID)
public class EnhancedAI
{
	public static final String MOD_ID = "enhancedai";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public EnhancedAI() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        EASounds.SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
		EAAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
		EAEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        Reflection.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(AnimalAttacking::attribute);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Attacking::attackRangeAttribute);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Targeting::xrayRangeAttribute);

        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            /*if (event.phase != TickEvent.Phase.END)
                return;
            protectedAreas.forEach((machinePos, pair) -> {
                final var level = pair.getFirst();
                final var aabb = pair.getSecond();
                level.setBlock(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), Blocks.CYAN_WOOL.defaultBlockState(), 3);
                level.setBlock(new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ), Blocks.PINK_WOOL.defaultBlockState(), 3);
            });*/
        });
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        DebugPathCommand.register(event.getDispatcher());
    }
}
