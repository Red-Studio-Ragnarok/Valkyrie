package io.redstudioragnarok.valkyrie.asm;

import dev.redstudio.redcore.utils.OptiNotFine;
import io.redstudioragnarok.valkyrie.config.ValkyrieConfig;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public class ValkyrieLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.valkyrieOptifineAndEssentialIncompatible.json", "mixins.tinyinv.json", "mixins.tinyinvmc67532fix.json", "mixins.appleskinmc67532fix.json", "mixins.mantlemc67532fix.json", "mixins.overloadedarmorbarmc67532fix.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig) {
            case "mixins.tinyinv.json":
                return Loader.isModLoaded("tinyinv");
            case "mixins.tinyinvmc67532fix.json":
                return Loader.isModLoaded("tinyinv") && ValkyrieConfig.mc67532Fix.enabled;
            case "mixins.appleskinmc67532fix.json":
                return Loader.isModLoaded("appleskin") && ValkyrieConfig.mc67532Fix.enabled;
            case "mixins.mantlemc67532fix.json":
                return Loader.isModLoaded("mantle") && ValkyrieConfig.mc67532Fix.enabled;
            case "mixins.overloadedarmorbarmc67532fix.json":
                return Loader.isModLoaded("overpoweredarmorbar") && ValkyrieConfig.mc67532Fix.enabled;
            case "mixins.valkyrieOptifineAndEssentialIncompatible.json":
                return !OptiNotFine.isOptiFineInstalled() && !Loader.isModLoaded("essential");
            default:
                return true;
        }
    }
}
