package com.example.teerm;

import org.jspecify.annotations.NonNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NbtOpenPayload(int entityId, String nbtString) implements CustomPacketPayload {
    public static final Type<NbtOpenPayload> TYPE = new Type<>(ExampleMod.id("nbt_open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NbtOpenPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, NbtOpenPayload::entityId,
                    ByteBufCodecs.STRING_UTF8, NbtOpenPayload::nbtString,
                    NbtOpenPayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}