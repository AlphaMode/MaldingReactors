package i.malding.hard.maldingreactors.content;

import i.malding.hard.maldingreactors.content.reactor.*;
import i.malding.hard.maldingreactors.content.reactor.ReactorBaseBlockEntity;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class MaldingBlockEntities implements AutoRegistryContainer<BlockEntityType<?>> {

    public static final BlockEntityType<ReactorBaseBlockEntity> REACTOR_CASING = FabricBlockEntityTypeBuilder.create((pos, state) -> new ReactorBaseBlockEntity(MaldingBlockEntities.REACTOR_CASING, pos, state), MaldingBlocks.REACTOR_CASING).build();
    public static final BlockEntityType<ReactorBaseBlockEntity> REACTOR_GLASS = FabricBlockEntityTypeBuilder.create((pos, state) -> new ReactorBaseBlockEntity(MaldingBlockEntities.REACTOR_GLASS, pos, state), MaldingBlocks.REACTOR_GLASS).build();



    public static final BlockEntityType<ReactorControllerBlockEntity> REACTOR_CONTROLLER = FabricBlockEntityTypeBuilder.create(ReactorControllerBlockEntity::new, MaldingBlocks.REACTOR_CONTROLLER).build();
    public static final BlockEntityType<ReactorCasingBlockBlockEntity> REACTOR_CASING = FabricBlockEntityTypeBuilder.create(ReactorCasingBlockBlockEntity::new, MaldingBlocks.REACTOR_CASING).build();

    public static final BlockEntityType<ReactorItemPortBlockEntity> REACTOR_ITEM_PORT = FabricBlockEntityTypeBuilder.create(ReactorItemPortBlockEntity::new, MaldingBlocks.REACTOR_ITEM_PORT).build();
    public static final BlockEntityType<ReactorPowerPortBlockEntity> REACTOR_POWER_PORT = FabricBlockEntityTypeBuilder.create(ReactorPowerPortBlockEntity::new, MaldingBlocks.REACTOR_POWER_PORT).build();

    public static final BlockEntityType<ReactorFuelRodBlockEntity> REACTOR_FUEL_ROD = FabricBlockEntityTypeBuilder.create(ReactorFuelRodBlockEntity::new, MaldingBlocks.REACTOR_FUEL_ROD).build();
    public static final BlockEntityType<ReactorFuelRodControllerBlockEntity> REACTOR_FUEL_ROD_CONTROLLER = FabricBlockEntityTypeBuilder.create(ReactorFuelRodControllerBlockEntity::new, MaldingBlocks.REACTOR_FUEL_ROD_CONTROLLER).build();


    @Override
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
    }
}
