package i.malding.hard.maldingreactors.content.reactor;

import i.malding.hard.maldingreactors.content.MaldingBlockEntities;
import i.malding.hard.maldingreactors.content.MaldingFluids;
import i.malding.hard.maldingreactors.content.handlers.ReactorScreenHandler;
import i.malding.hard.maldingreactors.multiblock.ReactorMultiblock;
import i.malding.hard.maldingreactors.util.CollectionNbtKey;
import i.malding.hard.maldingreactors.util.ReactorValidator;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ops.WorldOps;
import me.alphamode.star.transfer.FluidTank;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class ReactorControllerBlockEntity extends ReactorBaseBlockEntity implements ReactorMultiblock, NamedScreenHandlerFactory {

    public static final NbtKey.Type<BlockPos> BLOCK_POS = NbtKey.Type.LONG.then(BlockPos::fromLong, BlockPos::asLong);

    //Droplets per tick
    private static final int reactionRate = 1;

    //Energy Per Droplet
    private static final int energyConversionAmount = 50;

    private final FluidTank fuelTank = new FluidTank(FluidConstants.BUCKET * 8);
    private final FluidTank wasteTank = new FluidTank(FluidConstants.BUCKET * 8);

    protected final EnergyStorage energyStorage = new SimpleEnergyStorage(4000 * 50, Long.MAX_VALUE, Long.MAX_VALUE);
    private int coreHeat, casingHeat;

    private static final CollectionNbtKey<BlockPos, Set<BlockPos>> FUEL_RODS_KEY = new CollectionNbtKey<>("FuelRods", BLOCK_POS, LinkedHashSet::new);
    private static final CollectionNbtKey<BlockPos, Set<BlockPos>> FUEL_RODS_CONTROLLERS_KEY = new CollectionNbtKey<>("FuelRodControllers", BLOCK_POS, LinkedHashSet::new);

    private static final CollectionNbtKey<BlockPos, Set<BlockPos>> ITEM_PORTS_KEY = new CollectionNbtKey<>("ItemPorts", BLOCK_POS, LinkedHashSet::new);
    private static final CollectionNbtKey<BlockPos, Set<BlockPos>> POWER_PORTS_KEY = new CollectionNbtKey<>("PowerPorts", BLOCK_POS, LinkedHashSet::new);

    public Set<BlockPos> fuelRods = new HashSet<>();
    public Set<BlockPos> rodControllers = new HashSet<>();

    public Set<BlockPos> itemPorts = new HashSet<>();
    public Set<BlockPos> powerPorts = new HashSet<>();

    private ReactorValidator validator = null;
    private boolean isMultiBlock = false;

    private boolean isReactorOnline = false;

    public ReactorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(MaldingBlockEntities.REACTOR_CONTROLLER, pos, state);
    }

    public void clientTick() {}

    public void serverTick() {
        if (validator == null) {
            validator = new ReactorValidator(this.world, this.pos);
        }

        if (isValid()) {
            if (isReactorOnline) {
                float totalRodAbsorptionRate = 0f;

                int totalReatorRods = 0;
                int totalReactorControlRods = 0;

                for (BlockPos rodControllerPos : rodControllers) {
                    Optional<ReactorFuelRodControllerBlockEntity> possibleBlockEntity = world.getBlockEntity(rodControllerPos, MaldingBlockEntities.REACTOR_FUEL_ROD_CONTROLLER);

                    if(possibleBlockEntity.isEmpty()){
                        continue;
                    }

                    ReactorFuelRodControllerBlockEntity rodController = possibleBlockEntity.get();

                    totalRodAbsorptionRate += rodController.reactionRate / 100f;

                    totalReactorControlRods++;
                }

                totalReatorRods += this.fuelRods.size();

                long consumedFuel = convertFuelToWaste(MathHelper.floor(reactionRate * (totalRodAbsorptionRate / totalReactorControlRods)) * totalReatorRods);

                if (consumedFuel != 0) {
                    long energyCreated = consumedFuel * energyConversionAmount;

                    try (Transaction t = Transaction.openOuter()) {
                        energyStorage.insert(energyCreated, t);

                        t.commit();
                    }
                }
            }
        }
    }

    public long convertFuelToWaste(int maxFuelConsumption) {
        if (fuelTank.getAmount() == 0) {
            return 0;
        }

        try (Transaction t = Transaction.openOuter()) {
            long amountExtracted = fuelTank.extract(FluidVariant.of(MaldingFluids.COPIUM.still()), maxFuelConsumption, t);

            wasteTank.insert(FluidVariant.of(MaldingFluids.MALDING_COPIUM.still()), amountExtracted, t);

            return amountExtracted;
        }
    }

    public void handleBlockRemovable(BlockEntityType<?> type, BlockPos pos){
        if(type == MaldingBlockEntities.REACTOR_FUEL_ROD_CONTROLLER){
            rodControllers.remove(pos);
        } else if(type == MaldingBlockEntities.REACTOR_FUEL_ROD){
            fuelRods.remove(pos);
        } else if(type == MaldingBlockEntities.REACTOR_ITEM_PORT){
            itemPorts.remove(pos);
        } else if(type == MaldingBlockEntities.REACTOR_POWER_PORT){
            powerPorts.remove(pos);
        }

        this.setValid(false);
    }

    //---------------------------------------------------

    public ReactorValidator getValidator() {
        return this.validator;
    }

    public void setRodControllers(Set<BlockPos> reactorRods) {
        this.rodControllers = reactorRods;
    }

    @Override
    public @NotNull FluidTank getFuelTank() {
        return fuelTank;
    }

    public FluidTank getWasteTank() {
        return wasteTank;
    }

    @Override
    public @NotNull EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public int getCoreHeat() {
        return coreHeat;
    }

    @Override
    public int getCasingHeat() {
        return casingHeat;
    }

    @Override
    public void setValid(boolean result) {
        this.isMultiBlock = result;
    }

    @Override
    public boolean isValid() {
        return this.isMultiBlock;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        fuelRods = FUEL_RODS_KEY.getCollection(nbt);
        rodControllers = FUEL_RODS_CONTROLLERS_KEY.getCollection(nbt);

        itemPorts = ITEM_PORTS_KEY.getCollection(nbt);
        powerPorts = POWER_PORTS_KEY.getCollection(nbt);

        fuelTank.fromNbt(nbt, "FuelTank");
        wasteTank.fromNbt(nbt, "WasteTank");

        this.setValid(nbt.getBoolean("IsMultiBlock"));
        isReactorOnline = nbt.getBoolean("IsOnline");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        FUEL_RODS_KEY.putCollection(nbt, fuelRods);
        FUEL_RODS_CONTROLLERS_KEY.putCollection(nbt, rodControllers);

        ITEM_PORTS_KEY.putCollection(nbt, itemPorts);
        POWER_PORTS_KEY.putCollection(nbt, powerPorts);

        fuelTank.toNbt(nbt, "FuelTank");
        wasteTank.toNbt(nbt, "WasteTank");

        nbt.putBoolean("IsMultiBlock", this.isValid());
        nbt.putBoolean("IsOnline", isReactorOnline);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(this.world, this.pos);
    }

    //--------------------------------------------------------------------------------------------------------------

    @Override
    public Text getDisplayName() {
        return Text.of("");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        PacketByteBuf openingData = PacketByteBufs.create();
        openingData.writeBlockPos(pos);
        return new ReactorScreenHandler(syncId, inv, openingData);
    }
}
