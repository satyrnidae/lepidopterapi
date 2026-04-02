package dev.satyrn.lepidoptera.api.config.sync;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyncedConfigTest {

    // --- Minimal stubs ---

    static final class Value implements ConfigData {
        final String text;

        Value(final String text) {
            this.text = text;
        }
    }

    static final class StubCodec implements ConfigCodec<Value> {
        static final StubCodec INSTANCE = new StubCodec();

        @Override
        public void encode(final Value value, final net.minecraft.network.FriendlyByteBuf buf) {
            buf.writeUtf(value.text);
        }

        @Override
        public Value decode(final net.minecraft.network.FriendlyByteBuf buf) {
            return new Value(buf.readUtf());
        }
    }

    /**
     * Minimal ConfigHolder stub. save/load are no-ops; listeners are stored but never fired
     * (registration tests are integration concerns). {@link #setConfig} lets tests mutate the
     * returned value to verify that {@link SyncedConfig#get()} is not a snapshot.
     */
    static final class StubHolder implements ConfigHolder<Value> {
        private Value config;

        StubHolder(final Value initial) {
            this.config = initial;
        }

        @Override
        public Class<Value> getConfigClass() {
            return Value.class;
        }

        @Override
        public void save() {
        }

        @Override
        public boolean load() {
            return true;
        }

        @Override
        public Value getConfig() {
            return config;
        }

        @Override
        public void registerSaveListener(final ConfigSerializeEvent.Save<Value> listener) {
        }

        @Override
        public void registerLoadListener(final ConfigSerializeEvent.Load<Value> listener) {
        }

        @Override
        public void resetToDefault() {
            config = new Value("default");
        }

        @Override
        public void setConfig(final Value c) {
            this.config = c;
        }
    }

    // --- Test setup ---

    private StubHolder holder;
    private SyncedConfig<Value> synced;

    @BeforeEach
    void setUp() {
        holder = new StubHolder(new Value("local"));
        synced = SyncedConfig.unregistered(holder, StubCodec.INSTANCE);
    }

    // --- get() ---

    @Nested
    class Get {
        @Test
        void returnsLocalConfig_whenNoOverlay() {
            assertEquals("local", synced.get().text);
        }

        @Test
        void returnsServerValue_whenOverlayApplied() {
            synced.applyOverlay(new Value("server"));
            assertEquals("server", synced.get().text);
        }

        @Test
        void returnsCurrentHolderValue_notSnapshot() {
            // Verify get() delegates to holder.getConfig() dynamically, not a snapshot.
            holder.setConfig(new Value("updated"));
            assertEquals("updated", synced.get().text);
        }

        @Test
        void returnsLocalConfig_afterOverlayCleared() {
            synced.applyOverlay(new Value("server"));
            synced.clearOverlay();
            assertEquals("local", synced.get().text);
        }

        @Test
        void returnsCurrentHolderValue_afterOverlayCleared_ifHolderChanged() {
            synced.applyOverlay(new Value("server"));
            holder.setConfig(new Value("newLocal"));
            synced.clearOverlay();
            assertEquals("newLocal", synced.get().text);
        }
    }

    // --- local() ---

    @Nested
    class Local {
        @Test
        void alwaysReturnsHolderConfig_whenNoOverlay() {
            assertEquals("local", synced.local().text);
        }

        @Test
        void alwaysReturnsHolderConfig_ignoringActiveOverlay() {
            synced.applyOverlay(new Value("server"));
            assertEquals("local", synced.local().text);
        }

        @Test
        void reflectsHolderMutation() {
            holder.setConfig(new Value("mutated"));
            assertEquals("mutated", synced.local().text);
        }
    }

    // --- onApply callbacks ---

    @Nested
    class OnApply {
        @Test
        void firesWithServerValue_whenOverlayApplied() {
            final List<String> received = new ArrayList<>();
            synced.onApply(v -> received.add(v.text));
            synced.applyOverlay(new Value("server"));
            assertEquals(List.of("server"), received);
        }

        @Test
        void multipleCallbacks_allFire() {
            final List<String> received = new ArrayList<>();
            synced.onApply(v -> received.add("a:" + v.text));
            synced.onApply(v -> received.add("b:" + v.text));
            synced.applyOverlay(new Value("x"));
            assertEquals(List.of("a:x", "b:x"), received);
        }

        @Test
        void returnsThis_forChaining() {
            assertSame(synced, synced.onApply(v -> {}));
        }
    }

    // --- onClear callbacks ---

    @Nested
    class OnClear {
        @Test
        void fires_afterClearOverlay() {
            final List<String> log = new ArrayList<>();
            synced.onClear(() -> log.add("cleared"));
            synced.applyOverlay(new Value("server"));
            synced.clearOverlay();
            assertEquals(List.of("cleared"), log);
        }

        @Test
        void multipleCallbacks_allFire() {
            final List<String> log = new ArrayList<>();
            synced.onClear(() -> log.add("a"));
            synced.onClear(() -> log.add("b"));
            synced.applyOverlay(new Value("x"));
            synced.clearOverlay();
            assertEquals(List.of("a", "b"), log);
        }

        @Test
        void returnsThis_forChaining() {
            assertSame(synced, synced.onClear(() -> {}));
        }
    }
}
