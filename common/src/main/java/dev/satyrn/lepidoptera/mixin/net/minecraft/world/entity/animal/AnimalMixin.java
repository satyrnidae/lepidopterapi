package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity.animal;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.entity.HungryEntityRegistry;
import dev.satyrn.lepidoptera.api.entity.LivingEntityExtensions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {

    /**
     * Ticks remaining to emit {@code HAPPY_VILLAGER} particles after a successful feed (server-side).
     */
    @Unique private int lapi$feedTimer = 0;

    public abstract @Shadow boolean isFood(ItemStack stack);

    protected abstract @Shadow void usePlayerItem(Player player, InteractionHand hand, ItemStack stack);

    private AnimalMixin(final EntityType<? extends AgeableMob> type, final Level level) {
        super(type, level);
        NotInitializable.mixinClass(this);
    }

    /**
     * Intercepts {@code Animal.mobInteract} inside the {@code isFood} block, before breeding/aging
     * checks, to apply hunger to registered entity types.
     *
     * <p>If the entity is registered in {@link HungryEntityRegistry} and accepts the offered item
     * via {@link LivingEntityExtensions#eat(ItemStack)}, the item is consumed, a particle timer is
     * started, and the interaction is cancelled with {@code SUCCESS}. Vanilla breeding/aging proceed
     * normally for unregistered entities or when the entity is already full.</p>
     */
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;getAge()I", shift = At.Shift.AFTER), cancellable = true)
    private void lapi$onMobInteract(final Player player,
                                    final InteractionHand interactionHand,
                                    final CallbackInfoReturnable<InteractionResult> cir) {
        if (this.level().isClientSide) {
            return;
        }
        if (!HungryEntityRegistry.isRegistered(this.getType())) {
            return;
        }

        final ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!LivingEntityExtensions.cast(this).eat(itemStack)) {
            return;
        }

        this.usePlayerItem(player, interactionHand, itemStack);
        this.lapi$feedTimer = 40;

        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }

    /**
     * Emits one {@code HAPPY_VILLAGER} particle every 4 ticks while {@code lapi$feedTimer > 0},
     * producing a 2-second trickle effect after a successful feed.
     *
     * <p>Runs on the server entity via {@code ServerLevel.sendParticles()}, which forwards a
     * particle packet to nearby clients - no custom packet or entity data sync required.</p>
     */
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;aiStep()V", shift = At.Shift.AFTER))
    private void lapi$onAiStep(final CallbackInfo ci) {
        if (!this.level().isClientSide && this.lapi$feedTimer > 0) {
            if (this.lapi$feedTimer % 4 == 0 && this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5,
                        this.getRandomZ(1.0), 1, 0.0, 0.0, 0.0, 0.0);
            }
            this.lapi$feedTimer--;
        }
    }
}
