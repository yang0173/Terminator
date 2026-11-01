package com.example.teerm;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public class ModEvents {

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(NbtSavePayload.TYPE, NbtSavePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(NbtOpenPayload.TYPE, NbtOpenPayload.CODEC);

        // 1. 右键切换
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

            ItemStack stack = player.getMainHandItem();
            if (!stack.is(ModItems.DDD)) return InteractionResult.PASS;

            ModItems.toggleState(stack);
            int state = ModItems.getState(stack);
            String msg = (state == 1) ? "§c§l[审判模式] - 物理超度" : "§d§l[斩击模式] - 溢出伤害";
            // 修复：强转 ServerPlayer 并调用 sendSystemMessage
            if (player instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(msg));
            }

            return InteractionResult.SUCCESS;
        });

        // 2. 左键攻击
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity living)) return InteractionResult.PASS;
            if (!player.getMainHandItem().is(ModItems.DDD)) return InteractionResult.PASS;
            if (!living.isAlive()) return InteractionResult.PASS;

            int state = ModItems.getState(player.getMainHandItem());

            if (state == 0) {
                living.hurt(world.damageSources().genericKill(), Float.MAX_VALUE);
            } else {
                living.addTag("terminator_executed");
                living.setInvulnerable(false);
                if (living instanceof Mob mob) {
                    mob.setNoAi(true);
                    mob.noPhysics = true;
                }
                living.setHealth(0);
                living.setRemoved(Entity.RemovalReason.KILLED);
                living.discard();
                // 修复：强转 ServerPlayer
                if (player instanceof ServerPlayer sp) {
                    sp.sendSystemMessage(Component.literal("§4目标已抹除: " + living.getName().getString()));
                }
            }
            return InteractionResult.SUCCESS;
        });

        // 3. NBT 编辑器触发逻辑
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!player.getMainHandItem().is(ModItems.NBT_EDITOR)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity living)) return InteractionResult.PASS;

            var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, living.level().registryAccess());
            living.saveWithoutId(output);
            CompoundTag fullTag = output.buildResult();
            
            ServerPlayNetworking.send(serverPlayer, new NbtOpenPayload(living.getId(), fullTag.toString()));
            return InteractionResult.SUCCESS; 
        });

        // 4. 接收 NBT
        ServerPlayNetworking.registerGlobalReceiver(NbtSavePayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                Entity entity = context.player().level().getEntity(payload.entityId());
                if (!(entity instanceof LivingEntity living)) return;

                try {
                    CompoundTag userTag = TagParser.parseCompoundFully(payload.nbtString());
                    var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, living.level().registryAccess());
                    living.saveWithoutId(output);
                    CompoundTag fullTag = output.buildResult();
                    fullTag.merge(userTag);
                    
                    var input = TagValueInput.create(ProblemReporter.DISCARDING, living.level().registryAccess(), fullTag);
                    living.load(input);

                    if (living instanceof ServerPlayer sp) {
                        sp.connection.teleport(sp.getX(), sp.getY(), sp.getZ(), sp.getYRot(), sp.getXRot());
                        sp.setHealth(sp.getHealth());
                        sp.inventoryMenu.broadcastChanges();
                        context.server().getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, sp));
                    }
                } catch (Exception e) {
                    if (context.player() instanceof ServerPlayer sp) {
                        sp.sendSystemMessage(Component.literal("§cNBT 语法错误或格式不兼容！"));
                    }
                }
            });
        });
    }
}