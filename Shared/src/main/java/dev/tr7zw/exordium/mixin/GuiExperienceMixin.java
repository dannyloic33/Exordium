package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiExperienceMixin {

    @Shadow
    private Minecraft minecraft;
    private int lastlevel = 0;
    private float lastprogress = 0;

    private BufferedComponent experienceBuffer = new BufferedComponent(ExordiumModBase.instance.config.experienceSettings) {

        @Override
        public boolean needsRender() {
            return minecraft.player.experienceLevel != lastlevel || minecraft.player.experienceProgress != lastprogress;
        }

        @Override
        public void captureState() {
            lastlevel = minecraft.player.experienceLevel;
            lastprogress = minecraft.player.experienceProgress;
        }
    };

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"),
    })
    private void renderExperienceBarWrapper(Gui gui, PoseStack poseStack, int i, final Operation<Void> operation) {
        if (!experienceBuffer.render()) {
            operation.call(gui, poseStack, i);
        }
        experienceBuffer.renderEnd();
    }

}
