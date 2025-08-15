package insane96mcp.enhancedai.blocks;

import insane96mcp.enhancedai.ModQuack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProtectorMachineBlockEntity extends BlockEntity {

    private final EnergyStorage energy = new EnergyStorage(100000, 500, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy); // ← 這行是重點
    private int range = 5;

    public ProtectorMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModQuack.PROTECTOR_MACHINE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProtectorMachineBlockEntity be) {
        if (!level.isClientSide) {
            if (be.energy.getEnergyStored() > 0) {
                be.energy.extractEnergy(10, false); // 每 tick 消耗
                // TODO: 這裡呼叫 EAI 的殭屍挖掘阻止邏輯
            }
        }
    }

    public void cycleRange() {
        range += 5;
        if (range > 20) range = 5;
        setChanged();
    }

    public int getRange() {
        return range;
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        range = tag.getInt("Range");
        if (tag.contains("Energy"))
            energy.deserializeNBT(tag.getCompound("Energy"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Range", range);
        tag.put("Energy", energy.serializeNBT());
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }
}
