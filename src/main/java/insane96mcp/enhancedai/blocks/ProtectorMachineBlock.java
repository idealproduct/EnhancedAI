package insane96mcp.enhancedai.blocks;

import insane96mcp.enhancedai.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ProtectorMachineBlock extends Block implements EntityBlock {

    public ProtectorMachineBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModEntities.PROTECTOR_MACHINE.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ProtectorMachineBlockEntity machine) {
                machine.cycleRange(); // 無 GUI 調整範圍
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "Protection range: " + machine.getRange() + " blocks"
                        ), true
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ProtectorMachineBlockEntity machine) {
            return machine.getEnergyStored() > 0 ? 15 : 0;
        }
        return 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == ModEntities.PROTECTOR_MACHINE.get()
                ? (lvl, pos, st, be) -> ProtectorMachineBlockEntity.tick(lvl, pos, st, (ProtectorMachineBlockEntity) be)
                : null;
    }
}
