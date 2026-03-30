package dev.satyrn.lepidoptera.api.compatibility;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the set of {@link CompatibilityProvider} and {@link ClientCompatibilityProvider}
 * subclasses that a mod registers.
 *
 * <p>Place this annotation on your mod's main class alongside {@link dev.satyrn.lepidoptera.api.ModMeta @ModMeta}
 * and call {@link Compatibility#registerAll(Class)} during
 * {@link dev.satyrn.lepidoptera.api.LepidopteraMod#preInit() preInit} to register all
 * listed providers in a single call instead of one {@link Compatibility#register(String)}
 * call per provider:</p>
 *
 * <pre>{@code
 * @ModMeta(value = "mymod", name = "My Mod", semVer = "1.0.0+1.21.1")
 * @CompatibilityProviders(
 *     value = {"com.example.mymod.compat.CuriosCompatProvider"},
 *     client = {"com.example.mymod.compat.JeiClientProvider"}
 * )
 * public class MyMod implements LepidopteraMod {
 *     public void preInit() {
 *         Compatibility.registerAll(MyMod.class);
 *         Compatibility.preInit();
 *     }
 * }
 * }</pre>
 *
 * <p>Each string must be a fully-qualified name of a concrete subclass with a public
 * no-argument constructor. Class loading is deferred to {@link Compatibility#registerAll(Class)}
 * — if a class cannot be found (because the target mod is absent), that entry is skipped
 * silently. Client providers listed in {@link #client()} are additionally skipped silently on
 * a dedicated server.</p>
 *
 * @since 1.0.1-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.1+1.21.1")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompatibilityProviders {

    /**
     * Fully-qualified class names of {@link CompatibilityProvider} subclasses to register
     * as common (server + client) providers.
     *
     * @return the provider class names
     *
     * @since 1.0.1-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.1+1.21.1")
    String[] value() default {};

    /**
     * Fully-qualified class names of {@link ClientCompatibilityProvider} subclasses to
     * register as client-only providers. Registration is silently skipped on a dedicated
     * server.
     *
     * @return the client provider class names
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    String[] client() default {};
}
