package i.malding.hard.maldingreactors.content;

import com.mojang.logging.LogUtils;
import i.malding.hard.maldingreactors.MaldingReactors;
import i.malding.hard.maldingreactors.util.Tab;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class MaldingItems implements ItemRegistryContainer {

    public Logger LOGGER = LogUtils.getLogger();

    public static final Item COPIUM_INGOT = new Item(new OwoItemSettings().tab(1).group(MaldingReactors.GROUP));
    public static final Item MALDING_COPIUM_INGOT = new Item(new OwoItemSettings().tab(1).group(MaldingReactors.GROUP));

    private static int cachedTab = 1;

    @Nullable
    private static Field owo$tab_field = null;

    @Override
    public void postProcessField(String namespace, Item value, String identifier, Field field) {
        if (field.isAnnotationPresent(Tab.class)) cachedTab = field.getAnnotation(Tab.class).value();

        if(owo$tab_field == null) {
            for (Field field1 : Item.class.getFields()) {
                if (field1.getName().contains("owo$tab") && field1.getType().isInstance(int.class)) {
                    owo$tab_field = field1;

                    owo$tab_field.setAccessible(true);

                    break;
                }
            }
        }

        if(owo$tab_field != null){
            try {
                owo$tab_field.set(value, cachedTab);
            } catch (IllegalAccessException ignore) {}
        }
    }
}
