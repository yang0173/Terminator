package com.example.teerm.client;

import com.example.teerm.NbtOpenPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NbtOpenPayload.TYPE, (payload, context) -> {
            context.client().execute(() ->
                    context.client().setScreen(new NbtEditorScreen(payload.entityId(), payload.nbtString()))
            );
        });
    }
}