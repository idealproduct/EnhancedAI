package insane96mcp.enhancedai.blocks;

import com.mojang.datafixers.util.Pair;
import insane96mcp.enhancedai.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static insane96mcp.enhancedai.modules.zombie.ai.DiggingGoal.protectedAreas;

public class ProtectorMachineBlockEntity extends BlockEntity {
    public static class Range {
        public static final List<Integer> ranges = List.of(5, 10, 15, 20, 50, 100);
        private int selectedRange = 0;
        public Range() {
            selectedRange = 0;
        }
        public int getRange() {
            return ranges.get(selectedRange);
        }
        public void nextRange() {
            selectedRange += 1;
            if (selectedRange >= ranges.size()) selectedRange = 0;
        }
        public void previousRange() {
            selectedRange -= 1;
            if (selectedRange < 0) selectedRange = ranges.size() - 1;
        }
    }
    public final Range range = new Range();

    private final EnergyStorage energy = new EnergyStorage(100000, 5000, 5000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged(); // 讓方塊標記 dirty，下次會存進 NBT
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) {
                setChanged();
            }
            return extracted;
        }
    };
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy); // ← 這行是重點

    public ProtectorMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.PROTECTOR_MACHINE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProtectorMachineBlockEntity be) {
        final var entity = (ProtectorMachineBlockEntity) level.getBlockEntity(pos);
        assert entity != null;

        if (!level.isClientSide) {
            if (be.energy.getEnergyStored() >= 500) {
                be.energy.extractEnergy(500, false); // 每 tick 消耗
                // TODO: 這裡呼叫 EAI 的殭屍挖掘阻止邏輯
                be.setChanged();
                final var r = entity.range.getRange();
                final var aabb = AABB.ofSize(Vec3.atCenterOf(pos), r, r, r);
                protectedAreas.put(pos, Pair.of(level, aabb));
                return;
            }
            protectedAreas.remove(pos);
        }
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        range.selectedRange = tag.getInt("SelectedRange");
        if (tag.contains("Energy"))
            energy.deserializeNBT(tag.getCompound("Energy"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("SelectedRange", range.selectedRange);
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        protectedAreas.remove(worldPosition);
    }
}
