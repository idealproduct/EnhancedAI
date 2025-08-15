package insane96mcp.enhancedai;

import insane96mcp.enhancedai.blocks.ProtectorMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;




public class Modblocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "enhancedai");

    public static final RegistryObject<Block> PROTECTOR_MACHINE =
            BLOCKS.register("protector_machine",
                    () -> new ProtectorMachineBlock(
                            BlockBehaviour.Properties.of(Material.METAL)
                                    .strength(3.5f)
                                    .requiresCorrectToolForDrops()
                    ));


    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
