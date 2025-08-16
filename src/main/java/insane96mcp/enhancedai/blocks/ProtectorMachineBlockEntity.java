package insane96mcp.enhancedai.blocks;

import com.mojang.datafixers.util.Pair;
import insane96mcp.enhancedai.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

import static insane96mcp.enhancedai.modules.zombie.ai.DiggingGoal.protectedAreas;

public class ProtectorMachineBlockEntity extends BlockEntity {

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
    };    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy); // ← 這行是重點
    private int range = 5;

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
                final var aabb = AABB.ofSize(Vec3.atCenterOf(pos), entity.range, entity.range, entity.range);
                protectedAreas.put(pos, Pair.of(level, aabb));
                return;
            }
            protectedAreas.remove(pos);
        }
    }

    public void cycleRange() {
        range += 5;
        if (range > 50) range = 5;
        setChanged();
    }

    public void reducecycleRange() {
        range -= 5;
        if (range < 5) range = 5;
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        protectedAreas.remove(worldPosition);
    }
}
