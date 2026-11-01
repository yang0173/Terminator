package com.example.teerm;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Function;

public class ModItems {

    public static final ResourceKey<CreativeModeTab> TEERM_GROUP_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            ExampleMod.id("teerm")
    );

    public static final String STATE_TAG = "TerminatorState";

    public static final Item DDD = register("ddd", Item::new, new Item.Properties()
            .attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(ExampleMod.id("ddd_attack_damage"), Double.MAX_VALUE, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .build())
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            .component(DataComponents.CUSTOM_NAME, Component.literal("Terminator [斩击]").withStyle(ChatFormatting.LIGHT_PURPLE))
            .component(DataComponents.CUSTOM_DATA, createInitialData())
    );

    public static final Item NBT_EDITOR = register("nbt_editor", Item::new, new Item.Properties()
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final CreativeModeTab TEERM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.DDD))
            .title(Component.translatable("itemGroup.teerm.teerm"))
            .displayItems((params, output) -> {
                output.accept(DDD);
                output.accept(NBT_EDITOR);
            })
            .build();

    private static CustomData createInitialData() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(STATE_TAG, 0);
        return CustomData.of(tag);
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ExampleMod.id(name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TEERM_GROUP_KEY, TEERM_GROUP);
    }

    public static int getState(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        // 修复：1.21.11 getInt 返回 Optional<Integer>，用 orElse(0)
        return data.copyTag().getInt(STATE_TAG).orElse(0);
    }

    public static void toggleState(ItemStack stack) {
        int newState = (getState(stack) == 0) ? 1 : 0;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(STATE_TAG, newState);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        ChatFormatting color = (newState == 1) ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE;
        String suffix = (newState == 1) ? " [审判]" : " [斩击]";
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Terminator" + suffix).withStyle(color));
    }
}