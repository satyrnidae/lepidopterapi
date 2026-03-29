package dev.satyrn.lepidoptera.api.compatibility;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the set of {@link CompatibilityProvider} subclasses that a mod registers.
 *
 * <p>Place this annotation on your mod's main class alongside {@link dev.satyrn.lepidoptera.api.ModMeta @ModMeta}
 * and call {@link Compatibility#registerAll(Class)} during
 * {@link dev.satyrn.lepidoptera.api.LepidopteraMod#preInit() preInit} to register all
 * listed providers in a single call instead of one {@link Compatibility#register(String)}
 * call per provider:</p>
 *
 * <pre>{@code
 * @ModMeta(value = "mymod", name = "My Mod", semVer = "1.0.0+1.21.1")
 * @CompatibilityProviders({
 *     "com.example.mymod.compat.JeiCompatProvider",
 *     "com.example.mymod.compat.CuriosCompatProvider"
 * })
 * public class MyMod implements LepidopteraMod {
 *     public void preInit() {
 *         Compatibility.registerAll(MyMod.class);
 *         Compatibility.preInit();
 *     }
 * }
 * }</pre>
 *
 * <p>Each string must be a fully-qualified name of a concrete {@link CompatibilityProvider}
 * subclass with a public no-argument constructor. Class loading is deferred to
 * {@link Compatibility#registerAll(Class)} — if a class cannot be found (because the
 * target mod is absent), that entry is skipped silently, just as with
 * {@link Compatibility#register(String)}.</p>
 *
 * @since 1.0.1-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.1+1.21.1")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompatibilityProviders {

    /**
     * Fully-qualified class names of {@link CompatibilityProvider} subclasses to register.
     *
     * @return the provider class names
     *
     * @since 1.0.1-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.1+1.21.1")
    String[] value();
}
