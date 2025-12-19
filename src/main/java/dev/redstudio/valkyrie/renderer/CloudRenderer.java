package dev.redstudio.valkyrie.renderer;

import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.jafama.FastMath;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

import static dev.redstudio.valkyrie.Valkyrie.MC;

// Todo: Make this faster
// Todo: Fix the weird flicker / lines
public class CloudRenderer implements ISelectiveResourceReloadListener {

	private static final int TOP_SECTIONS = 12;
	private static final int HEIGHT = 4;

	private static final float INSET = 0.001F;
	private static final float ALPHA = 0.8F;
	private static final float PX_SIZE = 1 / 256F;

	private static final ResourceLocation MAP = new ResourceLocation("textures/environment/clouds.png");

	private static final VertexFormat FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR;

	private int renderDistance = -1;
	private int layers = -1;
	private int textureWidth;
	private int textureHeight;
	private int currentColor = 0;

	private float partialTicks = 0;

	private VertexBuffer vbo;

	private DynamicTexture COLOR_TEX = null;

	public CloudRenderer() {
		((IReloadableResourceManager) MC.getResourceManager()).registerReloadListener(this);
	}

	private float ceilToScale(final float value) {
		return FastMath.ceil(value / 12) * 12;
	}

	private void vertices(final BufferBuilder buffer) {
		final float CULL_DIST = 2 * 24;

		final float bCol = 0.7F;

		final float sectEnd = ceilToScale((renderDistance * 2) * 16);
		final float sectStart = -sectEnd;

		final float sectStep = ceilToScale(sectEnd * 2 / TOP_SECTIONS);
		final float sectPx = PX_SIZE / 12;

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

				final float u0 = sectX0 * sectPx;
				final float u1 = sectX1 * sectPx;
				final float v0 = sectZ0 * sectPx;
				final float v1 = sectZ1 * sectPx;

				// Bottom
				buffer.pos(sectX0, 0, sectZ0).tex(u0, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
				buffer.pos(sectX1, 0, sectZ0).tex(u1, v0).color(bCol, bCol, bCol, ALPHA).endVertex();
				buffer.pos(sectX1, 0, sectZ1).tex(u1, v1).color(bCol, bCol, bCol, ALPHA).endVertex();
				buffer.pos(sectX0, 0, sectZ1).tex(u0, v1).color(bCol, bCol, bCol, ALPHA).endVertex();

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

					slice += 12;

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

					slice += 12;

					if (slice <= CULL_DIST) {
						slice -= INSET;
						buffer.pos(sectX1, 0, slice).tex(u1, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
						buffer.pos(sectX1, HEIGHT, slice).tex(u1, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
						buffer.pos(sectX0, HEIGHT, slice).tex(u0, sliceCoord1).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
						buffer.pos(sectX0, 0, slice).tex(u0, sliceCoord0).color(0.8F, 0.8F, 0.8F, ALPHA).endVertex();
						slice += INSET;
					}
				}

				sectZ0 = sectZ1;
			}

			sectX0 = sectX1;
		}
	}

	private void dispose() {
		partialTicks = 0;

		if (vbo != null) {
			vbo.deleteGlBuffers();
			vbo = null;
		}
	}

	private void build() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if (vbo == null)
			vbo = new VertexBuffer(FORMAT);

		vertices(buffer);

		buffer.finishDrawing();
		buffer.reset();
		vbo.bufferData(buffer.getByteBuffer());
	}

	private int fullCoord(final double coord) {
		return ((int) coord / 12) - (coord < 0 ? 1 : 0);
	}

	private boolean isBuilt() {
		return vbo != null;
	}

	public void updateSettings() {
		final boolean enabled = ValkyrieConfig.graphics.clouds.enabled && MC.world != null && MC.world.provider.isSurfaceWorld();

		if (isBuilt() && (!enabled || ValkyrieConfig.graphics.clouds.renderDistance != renderDistance || ValkyrieConfig.graphics.clouds.layers != layers))
			dispose();

		renderDistance = ValkyrieConfig.graphics.clouds.renderDistance;
		layers = ValkyrieConfig.graphics.clouds.layers;

		if (enabled && !isBuilt())
			build();
	}

	public void render(final int cloudTicks, final float partialTicks) {
		this.partialTicks = partialTicks;

		for (int i = 0; i < layers; i++) {
			if (!isBuilt())
				return;

			final Entity entity = MC.getRenderViewEntity();

			final double totalOffset = cloudTicks + partialTicks;

			double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks + totalOffset * 0.03;
			double y = (ValkyrieConfig.graphics.clouds.height + (i * 10)) - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) + 0.33;
			double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

			final int scale = 12;

			z += 0.33 * scale;

			// Integer UVs to translate the texture matrix by.
			int offU = fullCoord(x);
			int offV = fullCoord(z);

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
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

			if (COLOR_TEX == null) {
				COLOR_TEX = new DynamicTexture(1, 1);
				updateCloudColour();
			}

			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.bindTexture(COLOR_TEX.getGlTextureId());
			GlStateManager.enableTexture2D();

			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			MC.renderEngine.bindTexture(MAP);

			ByteBuffer buffer = Tessellator.getInstance().getBuffer().getByteBuffer();

			vbo.bindBuffer();

			int stride = FORMAT.getSize();
			GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, stride, 0);
			GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12);
			GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, stride, 20);
			GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);

			GlStateManager.colorMask(false, false, false, false);
			vbo.drawArrays(GL11.GL_QUADS);
			GlStateManager.colorMask(true, true, true, true);

			if (ValkyrieConfig.debug.enabled && ValkyrieConfig.debug.wireframeClouds) {
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
			vbo.unbindBuffer();

			buffer.limit(0);
			for (int j = 0; j < FORMAT.getElementCount(); j++)
				FORMAT.getElements().get(j).getUsage().postDraw(FORMAT, j, FORMAT.getSize(), buffer);
			buffer.position(0);

			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.disableTexture2D();
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.disableBlend();
			GlStateManager.enableCull();

			GlStateManager.popMatrix();
		}
	}

	public void updateCloudColour() {
		if (MC.world == null || COLOR_TEX == null)
			return;

		final Vec3d cloudColor = MC.world.getCloudColour(partialTicks);
		final float[] skyColor = MC.world.provider.calcSunriseSunsetColors(MC.world.getCelestialAngle(partialTicks), partialTicks);

		final float r = (float) (skyColor == null || (skyColor[0] + skyColor[1] + skyColor[2]) >= 1.7 ? cloudColor.x : skyColor[0] * ValkyrieConfig.graphics.clouds.saturation + cloudColor.x * (1 - ValkyrieConfig.graphics.clouds.saturation));
		final float g = (float) (skyColor == null || (skyColor[0] + skyColor[1] + skyColor[2]) >= 1.7 ? cloudColor.y : skyColor[1] * ValkyrieConfig.graphics.clouds.saturation + cloudColor.y * (1 - ValkyrieConfig.graphics.clouds.saturation));
		final float b = (float) (skyColor == null || (skyColor[0] + skyColor[1] + skyColor[2]) >= 1.7 ? cloudColor.z : skyColor[2] * ValkyrieConfig.graphics.clouds.saturation + cloudColor.z * (1 - ValkyrieConfig.graphics.clouds.saturation));

		final int targetColor = 255 << 24 | ((int) (r * 255)) << 16 | ((int) (g * 255)) << 8 | (int) (b * 255);

		currentColor = lerpColor(currentColor, targetColor);

		COLOR_TEX.getTextureData()[0] = currentColor;
		COLOR_TEX.updateDynamicTexture();
	}

	private static int lerpColor(int from, int to) {
		return (int) lerp(from >> 24 & 0xff, to >> 24 & 0xff) << 24 | (int) lerp(from >> 16 & 0xff, to >> 16 & 0xff) << 16 | (int) lerp(from >> 8 & 0xff, to >> 8 & 0xff) << 8 | (int) lerp(from & 0xff, to & 0xff);
	}

	private static float lerp(int from, int to) {
		return from + (to - from) * (float) 0.05;
	}

	public void setupFog(final float partialTicks) {
		FogRenderer.setupFog(0, renderDistance * 16, partialTicks);
	}

	@Override
	public void onResourceManagerReload(final @Nonnull IResourceManager resourceManager, final @Nonnull Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(VanillaResourceType.TEXTURES) && MC.renderEngine != null) {
			MC.renderEngine.bindTexture(MAP);

			textureWidth = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			textureHeight = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		}
	}
}
