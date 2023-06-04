package io.redstudioragnarok.valkyrie.renderer;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GLContext;

import static io.redstudioragnarok.valkyrie.Valkyrie.mc;

public class FogRenderer {

    public static void setupFog(final int startCoords, final float farPlaneDistance, final float partialTicks) {
        final Entity entity = mc.getRenderViewEntity();
        final EntityLivingBase livingEntity = entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
        final boolean hasBlindness = livingEntity != null && livingEntity.isPotionActive(MobEffects.BLINDNESS);

        if (!ValkyrieConfig.general.fogEnabled && !hasBlindness) {
            GlStateManager.disableFog();
            return;
        }

        final EntityRenderer entityRenderer = mc.entityRenderer;

        entityRenderer.setupFogColor(false);

        GlStateManager.glNormal3f(0, -1, 0);
        GlStateManager.color(1, 1, 1, 1);

        final IBlockState iBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partialTicks);
        final Material material = iBlockState.getMaterial();

        final float fogDensity = ForgeHooksClient.getFogDensity(entityRenderer, entity, iBlockState, partialTicks, 0.1F);
        if (fogDensity >= 0) {
            GlStateManager.setFogDensity(fogDensity);
            return;
        }

        final GlStateManager.FogMode linear = GlStateManager.FogMode.LINEAR;
        final GlStateManager.FogMode exp = GlStateManager.FogMode.EXP;

        if (hasBlindness) {
            final int effectDuration = livingEntity.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
            float strength = 5;

            if (effectDuration < 20)
                strength = 5 + (farPlaneDistance - 5) * (1 - (float) effectDuration / 20);

            GlStateManager.setFog(linear);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0);
                GlStateManager.setFogEnd(strength * 0.8F);
            } else {
                GlStateManager.setFogStart(strength * 0.25F);
                GlStateManager.setFogEnd(strength);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
                GlStateManager.glFogi(34138, 34139);
        } else if (material == Material.WATER) {
            GlStateManager.setFog(exp);

            if (livingEntity != null)
                GlStateManager.setFogDensity(livingEntity.isPotionActive(MobEffects.WATER_BREATHING) ? 0.01F : 0.1F - (float) EnchantmentHelper.getRespirationModifier(livingEntity) * 0.03F);
            else
                GlStateManager.setFogDensity(0.1F);
        } else if (material == Material.LAVA) {
            GlStateManager.setFog(exp);
            GlStateManager.setFogDensity(2);
        } else {
            GlStateManager.setFog(linear);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0);
                GlStateManager.setFogEnd(farPlaneDistance);
            } else {
                GlStateManager.setFogStart(farPlaneDistance * 0.75F);
                GlStateManager.setFogEnd(farPlaneDistance);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
                GlStateManager.glFogi(34138, 34139);

            final int posX = (int) entity.posX;
            final int posZ = (int) entity.posZ;
            if (mc.world.provider.doesXZShowFog(posX, posZ) || mc.ingameGUI.getBossOverlay().shouldCreateFog()) {
                GlStateManager.setFogStart(farPlaneDistance * 0.05F);
                GlStateManager.setFogEnd(Math.min(farPlaneDistance, 192) * 0.5F);
            }

            ForgeHooksClient.onFogRender(entityRenderer, entity, iBlockState, partialTicks, startCoords, farPlaneDistance);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }
}
