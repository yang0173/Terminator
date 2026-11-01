package com.example.teerm.client;

import com.example.teerm.NbtSavePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class NbtEditorScreen extends Screen {
    private MultiLineEditBox nbtField;
    private EditBox posField;
    private EditBox healthField;
    private EditBox foodField;
    private boolean invulnerableState = false;
    
    private final int entityId;
    private final String initialNbt;

    public NbtEditorScreen(int entityId, String initialNbt) {
        super(Component.literal("Terminator NBT Editor"));
        this.entityId = entityId;
        this.initialNbt = initialNbt;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        this.posField = new EditBox(this.font, centerX - 150, 30, 140, 20, Component.literal("Pos"));
        this.posField.setValue(extractField("Pos"));
        this.addRenderableWidget(this.posField);

        this.healthField = new EditBox(this.font, centerX + 10, 30, 140, 20, Component.literal("Health"));
        this.healthField.setValue(extractField("Health"));
        this.addRenderableWidget(this.healthField);

        this.foodField = new EditBox(this.font, centerX - 150, 60, 140, 20, Component.literal("FoodLevel"));
        this.foodField.setValue(extractField("foodLevel"));
        this.addRenderableWidget(this.foodField);

        this.invulnerableState = extractField("Invulnerable").equals("1b");
        this.addRenderableWidget(Button.builder(
            Component.literal("无敌: " + (invulnerableState ? "开启" : "关闭")), 
            b -> {
                invulnerableState = !invulnerableState;
                b.setMessage(Component.literal("无敌: " + (invulnerableState ? "开启" : "关闭")));
            }
        ).bounds(centerX + 10, 60, 140, 20).build());

        this.nbtField = new MultiLineEditBox.Builder()
                .setX(centerX - 150)
                .setY(90)
                .setPlaceholder(Component.literal("原始 NBT 数据..."))
                .build(this.font, 300, 80, Component.literal("NBT Editor"));
        this.nbtField.setValue(this.initialNbt);
        this.addRenderableWidget(this.nbtField);

        this.addRenderableWidget(Button.builder(Component.literal("强制应用"), b -> {
            String finalNbt = buildFinalNbt();
            ClientPlayNetworking.send(new NbtSavePayload(this.entityId, finalNbt));
            this.onClose();
        }).bounds(centerX - 60, 180, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawString(this.font, "坐标 (Pos):", this.width / 2 - 150, 18, 0xFFFFFF);
        graphics.drawString(this.font, "血量 (Health):", this.width / 2 + 10, 18, 0xFFFFFF);
        graphics.drawString(this.font, "饥饿值 (Food):", this.width / 2 - 150, 48, 0xFFFFFF);
        graphics.drawString(this.font, "额外 NBT:", this.width / 2 - 150, 78, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private String extractField(String key) {
        try {
            CompoundTag tag = TagParser.parseCompoundFully(this.initialNbt);
            // 修复：Tag 没有 getAsString()，用 toString()
            if (tag.contains(key)) {
                Tag t = tag.get(key);
                if (t != null) return t.toString();
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String buildFinalNbt() {
        try {
            CompoundTag tag = TagParser.parseCompoundFully(this.nbtField.getValue());

            String posStr = this.posField.getValue().trim();
            if (!posStr.isEmpty()) {
                try {
                    CompoundTag wrapper = TagParser.parseCompoundFully("{Pos:" + posStr + "}");
                    // 修复：加 Objects.requireNonNull 避免空值警告
                    tag.put("Pos", Objects.requireNonNull(wrapper.get("Pos")));
                } catch (Exception ignored) {}
            }

            String healthStr = this.healthField.getValue().trim();
            if (!healthStr.isEmpty()) {
                try { tag.putFloat("Health", Float.parseFloat(healthStr)); } catch (Exception ignored) {}
            }

            String foodStr = this.foodField.getValue().trim();
            if (!foodStr.isEmpty()) {
                try { tag.putInt("foodLevel", Integer.parseInt(foodStr)); } catch (Exception ignored) {}
            }

            tag.putBoolean("Invulnerable", this.invulnerableState);

            return tag.toString();
        } catch (Exception e) {
            return this.initialNbt;
        }
    }
}