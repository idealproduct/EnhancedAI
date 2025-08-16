package insane96mcp.enhancedai;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static insane96mcp.enhancedai.ModBlocks.PROTECTOR_MACHINE;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "enhancedai");

    public static final RegistryObject<Item> PROTECTOR_MACHINE_ITEM = ITEMS.register("protector_machine", () -> new BlockItem(PROTECTOR_MACHINE.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

}
