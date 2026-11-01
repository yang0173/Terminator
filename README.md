# Terminator - Admin & Testing Tools

> **⚠️ WARNING / 警告**
> This mod contains **forced entity removal** and **low-level data modification** features. 
> While it functions as a high-damage weapon by default, activating "[Judgment Mode]" allows it to bypass most "immortality" or "invincibility" protections (e.g., Immortalizer or certain Titan mods) and physically erase the target from the world data.
> **DO NOT use this in normal survival worlds**, as it may corrupt your save or break the balance of other mods.
> 
> 本模组包含**强制物理抹除**和**底层数据修改**功能。默认状态下它是一把高伤害武器，但当你开启“审判模式”后，它可以无视绝大多数模组的“无敌”或“不死”保护，直接从世界数据中移除目标。
> **请勿在正常的生存游戏中使用，以免损坏存档或破坏其他模组的设计平衡。**

---

## 📖 简介 / Description

**Terminator** 是一款专为整合包作者、服务器管理员和极限模组测试者设计的工具。提供拥有极高伤害的武器，以及一个能修改实体底层 NBT 数据的编辑器。特别适合用来测试那些号称“绝对无敌”的模组生物。

**Terminator** is a powerful testing tool designed for modpack authors, server admins, and players who want to push their modded Minecraft to the limits. It provides a high-damage weapon and an NBT editor that can modify the underlying data of entities. Perfect for testing mobs that claim to be "invincible."

---

## ✨ 核心功能 / Features

### 🗡️ Terminator 武器 (Terminator Weapon)
*   **右键切换模式** (Right-click to toggle modes)。
*   **[斩击模式] (Slash Mode)**：造成极高的常规伤害 (Deals massive conventional damage)。
*   **[审判模式] (Judgment Mode)**：造成伤害的同时，剥夺目标的物理碰撞、AI，并强制将其从世界中抹除。对穿戴绝对防御护甲的实体同样有效 (Deals damage, disables AI/collision, and forcefully removes the entity from the world. Bypasses absolute defense armors)。

### 🛠️ 管理员 NBT 编辑器 (Admin NBT Editor)
*   手持编辑器左键点击生物即可打开界面 (Left-click a mob with the editor to open the GUI)。
*   提供快捷输入框，可一键修改坐标（Pos）、血量（Health）、饥饿值（FoodLevel）和无敌状态（Invulnerable）(Quick input fields for Position, Health, FoodLevel, and Invulnerable status)。
*   支持直接修改原始 NBT 数据 (Supports direct modification of raw NBT data)。

---

## 🔧 前置要求 / Requirements

*   **Minecraft**: 1.21.11 (Fabric)
*   **Fabric API** (若使用 Fabric 版 / for Fabric version)
*   **MixinSquared** (用于拦截部分极端的底层保护，模组已内置，一般无需额外安装 / Bundled in the mod, no extra installation needed)

---

## 🛠️ 构建说明 / Building

如果你想自己编译这个模组，请确保安装了 **JDK 21**，并在项目根目录运行：

```bash
# Windows
gradlew build

# macOS / Linux
./gradlew build
