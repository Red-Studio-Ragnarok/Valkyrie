package io.redstudioragnarok.valkyrie.renderer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
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

        mc.entityRenderer.setupFogColor(false);

        GlStateManager.glNormal3f(0, -1, 0);
        GlStateManager.color(1, 1, 1, 1);

        IBlockState iBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partialTicks);

        final float hook = ForgeHooksClient.getFogDensity(mc.entityRenderer, entity, iBlockState, partialTicks, 0.1F);
        if (hook >= 0)
            GlStateManager.setFogDensity(hook);
        else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
            final int effectDuration = ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
            float strength = 5;

            if (effectDuration < 20)
                strength = 5 + (farPlaneDistance - 5) * (1 - (float) effectDuration / 20);

            GlStateManager.setFog(GlStateManager.FogMode.LINEAR);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0);
                GlStateManager.setFogEnd(strength * 0.8F);
            } else {
                GlStateManager.setFogStart(strength * 0.25F);
                GlStateManager.setFogEnd(strength);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
                GlStateManager.glFogi(34138, 34139);
        } else if (iBlockState.getMaterial() == Material.WATER) {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);

            if (entity instanceof EntityLivingBase) {
                if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING))
                    GlStateManager.setFogDensity(0.01F);
                else
                    GlStateManager.setFogDensity(0.1F - (float) EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) * 0.03F);
            } else
                GlStateManager.setFogDensity(0.1F);
        } else if (iBlockState.getMaterial() == Material.LAVA) {
            GlStateManager.setFog(GlStateManager.FogMode.EXP);
            GlStateManager.setFogDensity(2);
        } else {
            GlStateManager.setFog(GlStateManager.FogMode.LINEAR);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0);
                GlStateManager.setFogEnd(farPlaneDistance);
            } else {
                GlStateManager.setFogStart(farPlaneDistance * 0.75F);
                GlStateManager.setFogEnd(farPlaneDistance);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
                GlStateManager.glFogi(34138, 34139);

            if (mc.world.provider.doesXZShowFog((int) entity.posX, (int) entity.posZ) || mc.ingameGUI.getBossOverlay().shouldCreateFog()) {
                GlStateManager.setFogStart(farPlaneDistance * 0.05F);
                GlStateManager.setFogEnd(Math.min(farPlaneDistance, 192) * 0.5F);
            }

            ForgeHooksClient.onFogRender(mc.entityRenderer, entity, iBlockState, partialTicks, startCoords, farPlaneDistance);
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }
}
