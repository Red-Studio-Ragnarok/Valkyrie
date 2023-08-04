package io.redstudioragnarok.valkyrie.asm;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public class ValkyrieLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.appleskin.json", "mixins.tinyinv.json", "mixins.mantle.json", "mixins.overloadedarmorbar.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig) {
            case "mixins.appleskin.json":
                return Loader.isModLoaded("appleskin");
            case "mixins.tinyinv.json":
                return Loader.isModLoaded("tinyinv");
            case "mixins.mantle.json":
                return Loader.isModLoaded("mantle");
            case "mixins.overloadedarmorbar.json":
                return Loader.isModLoaded("overpoweredarmorbar");
            default:
                return true;
        }
    }
}
