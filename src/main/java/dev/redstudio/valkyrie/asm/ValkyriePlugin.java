package dev.redstudio.valkyrie.asm;

import dev.redstudio.redcore.utils.OptiNotFine;
import dev.redstudio.valkyrie.config.ValkyrieConfig;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("dev.redstudio.valkyrie.asm")
public class ValkyriePlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public List<String> getMixinConfigs() {
		return Arrays.asList("mixins.valkyrie.json", "mixins.mc67532fix.json", "mixins.optifine.json", "mixins.valkyrieOptifineIncompatible.json");
	}

	@Override
	public boolean shouldMixinConfigQueue(String mixinConfig) {
		switch (mixinConfig) {
			case "mixins.mc67532fix.json":
				return ValkyrieConfig.mc67532Fix.enabled;
			case "mixins.optifine.json":
				return OptiNotFine.isOptiFineInstalled();
			case "mixins.valkyrieOptifineIncompatible.json":
				return !OptiNotFine.isOptiFineInstalled();
			default:
				return true;
		}
	}
}
