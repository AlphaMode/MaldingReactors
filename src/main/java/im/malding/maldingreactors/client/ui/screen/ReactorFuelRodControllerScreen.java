package im.malding.maldingreactors.client.ui.screen;

import im.malding.maldingreactors.client.MaldingTextures;
import im.malding.maldingreactors.content.handlers.ReactorFuelRodControllerScreenHandler;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ReactorFuelRodControllerScreen extends BaseOwoHandledScreen<FlowLayout, ReactorFuelRodControllerScreenHandler> {
    public ReactorFuelRodControllerScreen(ReactorFuelRodControllerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(
                Containers.verticalFlow(Sizing.fixed(176), Sizing.fixed(166))
                        .surface(Surface.PANEL)
        );

        rootComponent.positioning(Positioning.relative(50, 50));
    }
}
