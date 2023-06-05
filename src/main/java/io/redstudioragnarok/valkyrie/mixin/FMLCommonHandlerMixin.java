package io.redstudioragnarok.valkyrie.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.redstudioragnarok.valkyrie.utils.ModReference;
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

@Mixin(FMLCommonHandler.class)
public class FMLCommonHandlerMixin {

    @ModifyReturnValue(method = "getBrandings", at = @At("RETURN"))
    public List<String> replaceBranding(final List<String> original) {
        final List<String> valkyrieBranding = new ArrayList<>();

        if (!Objects.equals(ForgeVersion.getVersion(), "14.23.5.2860") && !FMLLaunchHandler.isDeobfuscatedEnvironment())
            valkyrieBranding.add(String.format("%sForge is outdated please update to 14.23.5.2860", TextFormatting.DARK_RED));

        valkyrieBranding.add(String.format("Powered by %sValkyrie %s%s", TextFormatting.RED, ModReference.version, TextFormatting.RESET));

        final int totalModCount = Loader.instance().getModList().size();
        final int activeModCount = Loader.instance().getActiveModList().size();

        valkyrieBranding.add(String.format("%d mod%s loaded, %d mod%s active", totalModCount, totalModCount!=1 ? "s" :"", activeModCount, activeModCount!=1 ? "s" :"" ));

        return valkyrieBranding;
    }
}