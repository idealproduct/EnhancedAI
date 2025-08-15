package insane96mcp.enhancedai;

import insane96mcp.enhancedai.blocks.ProtectorMachineBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "enhancedai");

    public static final RegistryObject<BlockEntityType<ProtectorMachineBlockEntity>> PROTECTOR_MACHINE =
            TILE_ENTITIES.register("protector_machine",
                    () -> BlockEntityType.Builder.of(
                            ProtectorMachineBlockEntity::new, ModBlocks.PROTECTOR_MACHINE.get()).build(null));
}
