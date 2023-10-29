package dev.redstudio.valkyrie.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.redstudio.valkyrie.utils.ValkyrieUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.redstudio.valkyrie.ProjectConstants.VERSION;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class FMLCommonHandlerMixin {

    private static final String LATEST_MIXIN_BOOTER = "8.3";

    @ModifyReturnValue(method = "getBrandings", at = @At("RETURN"), remap = false)
    private List<String> replaceBranding(final List<String> original) {
        final List<String> valkyrieBranding = new ArrayList<>();

        if (!Objects.equals(ForgeVersion.getVersion(), "14.23.5.2860") && !FMLLaunchHandler.isDeobfuscatedEnvironment())
            valkyrieBranding.add(String.format("%sForge is outdated please update to 14.23.5.2860", TextFormatting.DARK_RED));

        if (ValkyrieUtils.isVersionOutdated(Loader.instance().getIndexedModList().get("mixinbooter").getVersion(), LATEST_MIXIN_BOOTER))
            valkyrieBranding.add(String.format("%sMixin Booter is outdated please update to " + LATEST_MIXIN_BOOTER, TextFormatting.DARK_RED));

        valkyrieBranding.add(String.format("Powered by %sValkyrie %s%s", TextFormatting.RED, VERSION, TextFormatting.RESET));

        final int totalModCount = Loader.instance().getModList().size();
        final int activeModCount = Loader.instance().getActiveModList().size();

        valkyrieBranding.add(String.format("%d mod%s loaded, %d mod%s active", totalModCount, totalModCount!=1 ? "s" :"", activeModCount, activeModCount!=1 ? "s" :"" ));

        return valkyrieBranding;
    }
}
