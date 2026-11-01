package com.example.teerm.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevel.class, priority = 0)
public class EntityForceRemovalMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void forceRemoveZombieMobs(CallbackInfo ci) {
        ServerLevel level = (ServerLevel)(Object)this;
        
        level.getAllEntities().forEach(entity -> {
            if (entity.getTags().contains("terminator_executed") || entity.getTags().contains("ddd_killed")) {
                level.getChunkSource().removeEntity(entity);
                entity.setRemoved(Entity.RemovalReason.KILLED);
            }
        });
    }
}