package com.example.teerm.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ForceKillMixin {

    @Mixin(value = LivingEntity.class, priority = 1)
    public static class LivingEntityHurtMixin {
        @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
        private void forceGenericKill(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                LivingEntity self = (LivingEntity)(Object)this;
                // 这里检测 tag 来决定是否已经被处理过
                if (self.getTags().contains("terminator_executed") || self.getTags().contains("ddd_killed")) {
                    self.setHealth(0);
                    self.deathTime = 20;
                    self.discard();
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Mixin(value = Entity.class, priority = 1)
    public static class EntityRemovalMixin {
        @Inject(method = "discard", at = @At("HEAD"))
        private void forceDiscard(CallbackInfo ci) {
            Entity self = (Entity)(Object)this;
            if (self instanceof LivingEntity living && living.getHealth() <= 0) {
                living.setHealth(-1.0f);
            }
        }

        @Inject(method = "setRemoved", at = @At("HEAD"))
        private void forceSetRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
            Entity self = (Entity)(Object)this;
            if (self instanceof LivingEntity living && living.getHealth() <= 0) {
                living.setHealth(-1.0f);
            }
        }
    }
}