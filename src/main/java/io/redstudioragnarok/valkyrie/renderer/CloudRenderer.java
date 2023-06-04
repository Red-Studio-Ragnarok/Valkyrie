package io.redstudioragnarok.valkyrie.renderer;

import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

import static io.redstudioragnarok.valkyrie.utils.ModReference.log;

public class CloudRenderer implements ISelectiveResourceReloadListener {

    // Shared constants.
    private static final float PX_SIZE = 1 / 256F;

    // Building constants.
    private static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR;
    private static final int TOP_SECTIONS = 12;    // Number of slices a top face will span.
    private static final int HEIGHT = 4;
    private static final float INSET = 0.001F;
    private static final float ALPHA = 0.8F;

    // Debug
    private static final boolean WIREFRAME = false;

    // Instance fields
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation texture = new ResourceLocation("textures/environment/clouds.png");

    private VertexBuffer vbo;
    private int cloudMode = -1;
    private int renderDistance = -1;

    private DynamicTexture COLOR_TEX = null;

    private int textureWidth;
    private int textureHeight;

    public CloudRenderer() {
        // Resource manager should always be reloadable.
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this);
    }

    private int getScale() {
        return cloudMode == 2 ? 12 : 8;
    }

    private float ceilToScale(float value) {
        float scale = getScale();
        return MathHelper.ceil(value / scale) * scale;
    }

    private void vertices(BufferBuilder buffer) {
        boolean fancy = cloudMode == 2;    // Defines whether to hide all but the bottom.

        float scale = getScale();
        float CULL_DIST = 2 * scale;

        float bCol = fancy ? 0.7F : 1F;

        float sectEnd = ceilToScale((renderDistance * 2) * 16);
        float sectStart = -sectEnd;

        float sectStep = ceilToScale(sectEnd * 2 / TOP_SECTIONS);
        float sectPx = PX_SIZE / scale;

        buffer.begin(GL11.GL_QUADS, FORMAT);

        float sectX0 = sectStart;
        float sectX1 = sectX0;

        while (sectX1 < sectEnd) {
            sectX1 += sectStep;

            if (sectX1 > sectEnd)
                sectX1 = sectEnd;

            float sectZ0 = sectStart;
            float sectZ1 = sectZ0;

            while (sectZ1 < sectEnd) {
                sectZ1 += sectStep;

                if (sectZ1 > sectEnd)
                    sectZ1 = sectEnd;

                float u0 = sectX0 * sectPx;
                float u1 = sectX1 * sectPx;
                float v0 = sectZ0 * sectPx;
                float v1 = sectZ1 * sectPx;

                // Bottom
                buffer.pos(sectX0, 0, sectZ0).tex(u0, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ0).tex(u1, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX1, 0, sectZ1).tex(u1, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
                buffer.pos(sectX0, 0, sectZ1).tex(u0, v1).color(bCol, bCol, bCol, ALPHA).endVertex();

                if (fancy) {
                    // Top
                    buffer.pos(sectX0, HEIGHT, sectZ0).tex(u0, v0).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX0, HEIGHT, sectZ1).tex(u0, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ1).tex(u1, v1).color(1, 1, 1, ALPHA).endVertex();
                    buffer.pos(sectX1, HEIGHT, sectZ0).tex(u1, v0).color(1, 1, 1, ALPHA).endVertex();

                    float slice;
                    float sliceCoord0;
                    float sliceCoord1;

                    for (slice = sectX0; slice < sectX1; ) {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // X sides
                        if (slice > -CULL_DIST) {
                            slice += INSET;
                            buffer.pos(slice, 0, sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0, sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= CULL_DIST) {
                            slice -= INSET;
                            buffer.pos(slice, 0, sectZ0).tex(sliceCoord0, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ0).tex(sliceCoord1, v0).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, HEIGHT, sectZ1).tex(sliceCoord1, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            buffer.pos(slice, 0, sectZ1).tex(sliceCoord0, v1).color(0.9F, 0.9F, 0.9F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }

                    for (slice = sectZ0; slice < sectZ1; ) {
                        sliceCoord0 = slice * sectPx;
                        sliceCoord1 = sliceCoord0 + PX_SIZE;

                        // Z sides
                        if (slice > -CULL_DIST) {
                            slice += INSET;
                            buffer.pos(sectX0, 0, slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, 0, slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice -= INSET;
                        }

                        slice += scale;

                        if (slice <= CULL_DIST) {
                            slice -= INSET;
                            buffer.pos(sectX1, 0, slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            buffer.pos(sectX0, 0, slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
                            slice += INSET;
                        }
                    }
                }

                sectZ0 = sectZ1;
            }

            sectX0 = sectX1;
        }
    }

    private void dispose() {
        if (vbo != null) {
            vbo.deleteGlBuffers();
            vbo = null;
        }
    }

    private void build() {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        vbo = new VertexBuffer(FORMAT);

        vertices(buffer);

        if (OpenGlHelper.useVbo()) {
            buffer.finishDrawing();
            buffer.reset();
            vbo.bufferData(buffer.getByteBuffer());
        } else {
            tess.draw();
            GlStateManager.glEndList();
        }
    }

    private int fullCoord(double coord, int scale) {   // Corrects misalignment of UV offset when on negative coords.
        return ((int) coord / scale) - (coord < 0 ? 1 : 0);
    }

    private boolean isBuilt() {
        return vbo != null;
    }

    public void checkSettings() {
        final boolean enabled = mc.gameSettings.shouldRenderClouds() != 0 && mc.world !=null && mc.world.provider.isSurfaceWorld();

        if (isBuilt() && (!enabled || mc.gameSettings.shouldRenderClouds() != cloudMode || ValkyrieConfig.clouds.renderDistance != renderDistance))
            dispose();

        cloudMode = mc.gameSettings.shouldRenderClouds();
        renderDistance = ValkyrieConfig.clouds.renderDistance;

        if (enabled && !isBuilt())
            build();
    }

    public boolean render(int cloudTicks, float partialTicks) {
        if (!isBuilt())
            return false;

        Entity entity = mc.getRenderViewEntity();

        double totalOffset = cloudTicks + partialTicks;

        double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks
                + totalOffset * 0.03;
        double y = ValkyrieConfig.clouds.height - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) + 0.33;
        double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

        int scale = getScale();

        if (cloudMode == 2)
            z += 0.33 * scale;

        // Integer UVs to translate the texture matrix by.
        int offU = fullCoord(x, scale);
        int offV = fullCoord(z, scale);

        GlStateManager.pushMatrix();

        // Translate by the remainder after the UV offset.
        GlStateManager.translate((offU * scale) - x, y, (offV * scale) - z);

        // Modulo to prevent texture samples becoming inaccurate at extreme offsets.
        offU = offU % textureWidth;
        offV = offV % textureHeight;

        // Translate the texture.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.translate(offU * PX_SIZE, offV * PX_SIZE, 0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.disableCull();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // Color multiplier.
        Vec3d color = mc.world.getCloudColour(partialTicks);
        float r = (float) color.x;
        float g = (float) color.y;
        float b = (float) color.z;

        if (mc.gameSettings.anaglyph) {
            float tempR = r * 0.3F + g * 0.59F + b * 0.11F;
            float tempG = r * 0.3F + g * 0.7F;
            float tempB = r * 0.3F + b * 0.7F;
            r = tempR;
            g = tempG;
            b = tempB;
        }

        if (COLOR_TEX == null)
            COLOR_TEX = new DynamicTexture(1, 1);

        // Apply a color multiplier through a texture upload if shaders aren't supported.
        COLOR_TEX.getTextureData()[0] = 255 << 24
                | ((int) (r * 255)) << 16
                | ((int) (g * 255)) << 8
                | (int) (b * 255);
        COLOR_TEX.updateDynamicTexture();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.bindTexture(COLOR_TEX.getGlTextureId());
        GlStateManager.enableTexture2D();

        // Bind the clouds texture last so the shader's sampler2D is correct.
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        mc.renderEngine.bindTexture(texture);

        ByteBuffer buffer = Tessellator.getInstance().getBuffer().getByteBuffer();

        // Set up pointers for the display list/VBO.
        if (OpenGlHelper.useVbo()) {
            vbo.bindBuffer();

            int stride = FORMAT.getSize();
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20);
            GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
        } else {
            buffer.limit(FORMAT.getSize());
            for (int i = 0; i < FORMAT.getElementCount(); i++)
                FORMAT.getElements().get(i).getUsage().preDraw(FORMAT, i, FORMAT.getSize(), buffer);
            buffer.position(0);
        }

        // Depth pass to prevent insides rendering from the outside.
        GlStateManager.colorMask(false, false, false, false);
        vbo.drawArrays(GL11.GL_QUADS);

        // Full render.
        if (!mc.gameSettings.anaglyph) {
            GlStateManager.colorMask(true, true, true, true);
        } else {
            switch (EntityRenderer.anaglyphField) {
                case 0:
                    GlStateManager.colorMask(false, true, true, true);
                    break;
                case 1:
                    GlStateManager.colorMask(true, false, false, true);
                    break;
            }
        }

        // Wireframe for debug.
        if (WIREFRAME) {
            GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableFog();
            vbo.drawArrays(GL11.GL_QUADS);
            GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableFog();
        }

        vbo.drawArrays(GL11.GL_QUADS);
        vbo.unbindBuffer(); // Unbind buffer and disable pointers.

        buffer.limit(0);
        for (int i = 0; i < FORMAT.getElementCount(); i++)
            FORMAT.getElements().get(i).getUsage().postDraw(FORMAT, i, FORMAT.getSize(), buffer);
        buffer.position(0);

        // Disable our coloring.
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        // Reset texture matrix.
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();

        GlStateManager.popMatrix();

        return true;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES) && mc.renderEngine != null) {
            mc.renderEngine.bindTexture(texture);

            textureWidth = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            textureHeight = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        }
    }
}
