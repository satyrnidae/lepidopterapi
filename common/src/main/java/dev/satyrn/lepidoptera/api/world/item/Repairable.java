package dev.satyrn.lepidoptera.api.world.item;

public interface Repairable {
    default boolean preventRepair() {
        return false;
    }
}
