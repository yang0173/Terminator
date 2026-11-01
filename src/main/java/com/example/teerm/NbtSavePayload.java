package com.example.teerm;

import org.jspecify.annotations.NonNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NbtSavePayload(int entityId, String nbtString) implements CustomPacketPayload {
    public static final Type<NbtSavePayload> TYPE = new Type<>(ExampleMod.id("nbt_save"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NbtSavePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, NbtSavePayload::entityId,
                    ByteBufCodecs.STRING_UTF8, NbtSavePayload::nbtString,
                    NbtSavePayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}