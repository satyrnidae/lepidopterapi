package dev.satyrn.lepidoptera.client;

import dev.satyrn.lepidoptera.api.LepidopteraMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LepidopteraAPIClient implements LepidopteraMod {
    public static LepidopteraMod INSTANCE = new LepidopteraAPIClient();

    private LepidopteraAPIClient() {}
}
