package i.malding.hard.maldingreactors.multiblock;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public interface ReactorMultiblock extends ReactorPart {
    SingleVariantStorage<FluidVariant> getFuelTank();

    EnergyStorage getEnergyStorage();

    int getCoreHeat();

    int getCasingHeat();
}
