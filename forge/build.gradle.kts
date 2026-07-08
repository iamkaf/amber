import org.gradle.api.tasks.Sync

plugins {
    id("com.iamkaf.multiloader.forge")
}

val minecraftVersion = project.name

if (minecraftVersion == "1.16.5") {
    tasks.named<Sync>("stageMergedJavaSources").configure {
        inputs.property("amberLegacy1165ForgeSourceTransform", "v3-kotlin")
        filesMatching("**/*.java") {
            filter { line: String ->
                mapOf(
                    "net.minecraft.ChatFormatting" to "net.minecraft.util.text.TextFormatting",
                    "net.minecraft.SharedConstants" to "net.minecraft.util.SharedConstants",
                    "net.minecraft.commands.CommandSourceStack" to "net.minecraft.command.CommandSource",
                    "net.minecraft.commands.Commands" to "net.minecraft.command.Commands",
                    "net.minecraft.network.chat.MutableComponent" to "net.minecraft.util.text.IFormattableTextComponent",
                    "net.minecraft.network.chat.TextComponent" to "net.minecraft.util.text.StringTextComponent",
                    "net.minecraft.network.chat.Component" to "net.minecraft.util.text.ITextComponent",
                    "net.minecraft.core.BlockPos" to "net.minecraft.util.math.BlockPos",
                    "net.minecraft.core.Direction" to "net.minecraft.util.Direction",
                    "net.minecraft.core.NonNullList" to "net.minecraft.util.NonNullList",
                    "net.minecraft.core.Registry" to "net.minecraft.util.registry.Registry",
                    "net.minecraft.core.RegistryAccess" to "net.minecraft.util.registry.DynamicRegistries",
                    "net.minecraft.resources.ResourceKey" to "net.minecraft.util.RegistryKey",
                    "net.minecraft.resources.ResourceLocation" to "net.minecraft.util.ResourceLocation",
                    "net.minecraft.network.FriendlyByteBuf" to "net.minecraft.network.PacketBuffer",
                    "net.minecraft.network.protocol.Packet" to "net.minecraft.network.IPacket",
                    "net.minecraft.server.level.ServerLevel" to "net.minecraft.world.server.ServerWorld",
                    "net.minecraft.server.level.ServerPlayer" to "net.minecraft.entity.player.ServerPlayerEntity",
                    "net.minecraft.world.entity.player.Player" to "net.minecraft.entity.player.PlayerEntity",
                    "net.minecraft.world.entity.LivingEntity" to "net.minecraft.entity.LivingEntity",
                    "net.minecraft.world.entity.AgableMob" to "net.minecraft.entity.AgeableEntity",
                    "net.minecraft.world.entity.AgeableMob" to "net.minecraft.entity.AgeableEntity",
                    "net.minecraft.world.entity.Entity" to "net.minecraft.entity.Entity",
                    "net.minecraft.world.entity.EquipmentSlot" to "net.minecraft.inventory.EquipmentSlotType",
                    "net.minecraft.world.entity.item.ItemEntity" to "net.minecraft.entity.item.ItemEntity",
                    "net.minecraft.world.entity.animal.Animal" to "net.minecraft.entity.passive.AnimalEntity",
                    "net.minecraft.world.item.context.UseOnContext" to "net.minecraft.item.ItemUseContext",
                    "net.minecraft.world.item.CreativeModeTab" to "net.minecraft.item.ItemGroup",
                    "net.minecraft.world.item.ItemStack" to "net.minecraft.item.ItemStack",
                    "net.minecraft.world.item.Item" to "net.minecraft.item.Item",
                    "net.minecraft.world.item.BoneMealItem" to "net.minecraft.item.BoneMealItem",
                    "net.minecraft.world.item.TooltipFlag" to "net.minecraft.client.util.ITooltipFlag",
                    "net.minecraft.world.level.block.state.BlockState" to "net.minecraft.block.BlockState",
                    "net.minecraft.world.level.block.FarmBlock" to "net.minecraft.block.FarmlandBlock",
                    "net.minecraft.world.level.block.FarmlandBlock" to "net.minecraft.block.FarmlandBlock",
                    "net.minecraft.world.level.block.CropBlock" to "net.minecraft.block.CropsBlock",
                    "net.minecraft.world.level.ItemLike" to "net.minecraft.item.IItemProvider",
                    "net.minecraft.world.level.ClipContext" to "net.minecraft.util.math.RayTraceContext",
                    "net.minecraft.world.level.Level" to "net.minecraft.world.World",
                    "net.minecraft.world.level.ServerLevelAccessor" to "net.minecraft.world.IServerWorld",
                    "net.minecraft.world.level.biome.Biome" to "net.minecraft.world.biome.Biome",
                    "net.minecraft.world.level.storage.loot.LootTable" to "net.minecraft.loot.LootTable",
                    "net.minecraft.world.level.storage.loot.LootPool" to "net.minecraft.loot.LootPool",
                    "net.minecraft.world.damagesource.DamageSource" to "net.minecraft.util.DamageSource",
                    "net.minecraft.world.InteractionHand" to "net.minecraft.util.Hand",
                    "net.minecraft.world.InteractionResult" to "net.minecraft.util.ActionResultType",
                    "net.minecraft.world.Container" to "net.minecraft.inventory.IInventory",
                    "net.minecraft.world.inventory.PlayerEnderChestContainer" to "net.minecraft.inventory.EnderChestInventory",
                    "net.minecraft.world.food.FoodData" to "net.minecraft.util.FoodStats",
                    "net.minecraft.world.level.GameType" to "net.minecraft.world.GameType",
                    "net.minecraft.world.level.LevelAccessor" to "net.minecraft.world.IWorld",
                    "net.minecraft.world.WorldAccessor" to "net.minecraft.world.IWorld",
                    "net.minecraft.world.DifficultyInstance" to "net.minecraft.world.DifficultyInstance",
                    "net.minecraft.world.Difficulty" to "net.minecraft.world.Difficulty",
                    "net.minecraft.world.phys.AABB" to "net.minecraft.util.math.AxisAlignedBB",
                    "net.minecraft.world.phys.BlockHitResult" to "net.minecraft.util.math.BlockRayTraceResult",
                    "net.minecraft.world.phys.Vec3" to "net.minecraft.util.math.vector.Vector3d",
                    "net.minecraft.world.entity.ai.attributes.AttributeModifier" to "net.minecraft.entity.ai.attributes.AttributeModifier",
                    "net.minecraft.world.entity.ai.attributes.Attributes" to "net.minecraft.entity.ai.attributes.Attributes",
                    "net.minecraft.world.entity.ai.attributes.Attribute" to "net.minecraft.entity.ai.attributes.Attribute",
                    "net.minecraft.sounds.SoundEvent" to "net.minecraft.util.SoundEvent",
                    "net.minecraft.sounds.SoundSource" to "net.minecraft.util.SoundCategory",
                    "net.minecraft.client.KeyMapping" to "net.minecraft.client.settings.KeyBinding",
                    "net.minecraft.client.Camera" to "net.minecraft.client.renderer.ActiveRenderInfo",
                    "net.minecraft.client.gui.Font" to "net.minecraft.client.gui.FontRenderer",
                    "net.minecraft.client.gui.screens.Screen" to "net.minecraft.client.gui.screen.Screen",
                    "net.minecraft.client.player.LocalPlayer" to "net.minecraft.client.entity.player.ClientPlayerEntity",
                    "net.minecraft.client.multiplayer.ClientLevel" to "net.minecraft.client.world.ClientWorld",
                    "net.minecraft.client.renderer.MultiBufferSource" to "net.minecraft.client.renderer.IRenderTypeBuffer",
                    "net.minecraft.client.renderer.LevelRenderer" to "net.minecraft.client.renderer.WorldRenderer",
                    "net.minecraft.client.gui.Gui" to "net.minecraft.client.gui.IngameGui",
                    "net.minecraft.client.gui.screens.LevelLoadingScreen" to "net.minecraft.client.gui.screen.DownloadTerrainScreen",
                    "net.minecraft.client.MouseHandler" to "net.minecraft.client.MouseHelper",
                    "net.minecraft.core.GlobalPos" to "net.minecraft.util.math.GlobalPos",
                    "net.minecraft.core.Vec3i" to "net.minecraft.util.math.vector.Vector3i",
                    "net.minecraft.world.inventory.CraftingContainer" to "net.minecraft.inventory.CraftingInventory",
                    "net.minecraft.world.inventory.ResultSlot" to "net.minecraft.inventory.container.CraftingResultSlot",
                    "net.minecraft.world.phys.HitResult" to "net.minecraft.util.math.RayTraceResult",
                    "com.mojang.blaze3d.vertex.VertexConsumer" to "com.mojang.blaze3d.vertex.IVertexBuilder",
                    "net.minecraft.network.protocol.game.ClientboundSetTitlesPacket" to "net.minecraft.network.play.server.STitlePacket",
                    "net.minecraft.network.protocol.game.ClientboundSoundPacket" to "net.minecraft.network.play.server.SPlaySoundEffectPacket",
                    "net.minecraft.world.entity.player.Abilities" to "net.minecraft.entity.player.PlayerAbilities",
                    "net.minecraft.world.entity.player.Inventory" to "net.minecraft.entity.player.PlayerInventory",
                    "net.minecraft.world.entity.LightningBolt" to "net.minecraft.entity.effect.LightningBoltEntity",
                    "net.minecraft.world.item.ArmorItem" to "net.minecraft.item.ArmorItem",
                    "net.minecraft.world.item.ShieldItem" to "net.minecraft.item.ShieldItem",
                    "net.minecraft.world.item.enchantment.Enchantment" to "net.minecraft.enchantment.Enchantment",
                    "net.minecraft.world.item.crafting.Ingredient" to "net.minecraft.item.crafting.Ingredient",
                    "net.minecraft.world.level.block.entity.BlockEntity" to "net.minecraft.tileentity.TileEntity",
                    "com.mojang.blaze3d.vertex.PoseStack" to "com.mojang.blaze3d.matrix.MatrixStack",
                    "net.minecraftforge.fmllegacy.RegistryObject" to "net.minecraftforge.fml.RegistryObject",
                    "net.minecraftforge.fmllegacy.network.NetworkRegistry" to "net.minecraftforge.fml.network.NetworkRegistry",
                    "net.minecraftforge.fmllegacy.network.simple.SimpleChannel" to "net.minecraftforge.fml.network.simple.SimpleChannel",
                    "net.minecraftforge.fmllegacy.network.PacketDistributor" to "net.minecraftforge.fml.network.PacketDistributor",
                    "net.minecraftforge.fmlclient.registry.ClientRegistry" to "net.minecraftforge.fml.client.registry.ClientRegistry",
                    "net.minecraftforge.fml.event.IModBusEvent" to "net.minecraftforge.fml.event.lifecycle.IModBusEvent",
                ).entries.fold(line) { current, entry -> current.replace(entry.key, entry.value) }
                    .replace(Regex("\\bMutableComponent\\b"), "IFormattableTextComponent")
                    .replace(Regex("\\bTextComponent\\b"), "StringTextComponent")
                    .replace(Regex("\\bComponent\\b"), "ITextComponent")
                    .replace(Regex("\\bCompoundTag\\b"), "CompoundNBT")
                    .replace(Regex("\\bListTag\\b"), "ListNBT")
                    .replace(Regex("\\bChatFormatting\\b"), "TextFormatting")
                    .replace(Regex("\\bCommandSourceStack\\b"), "CommandSource")
                    .replace(Regex("\\bFriendlyByteBuf\\b"), "PacketBuffer")
                    .replace(Regex("\\bServerPlayer\\b"), "ServerPlayerEntity")
                    .replace(Regex("\\bPlayer\\b"), "PlayerEntity")
                    .replace(Regex("\\bKeyMapping\\b"), "KeyBinding")
                    .replace(Regex("\\bInteractionResult\\b"), "ActionResultType")
                    .replace(Regex("\\bResourceKey\\b"), "RegistryKey")
                    .replace(Regex("\\bCreativeModeTab\\b"), "ItemGroup")
                    .replace(Regex("\\bItemLike\\b"), "IItemProvider")
                    .replace(Regex("\\bServerLevelAccessor\\b"), "IServerWorld")
                    .replace(Regex("\\bServerLevel\\b"), "ServerWorld")
                    .replace(Regex("\\bLevel\\b"), "World")
                    .replace(Regex("\\bBlockHitResult\\b"), "BlockRayTraceResult")
                    .replace(Regex("\\bClipContext\\b"), "RayTraceContext")
                    .replace(Regex("\\bVec3i\\b"), "Vector3i")
                    .replace(Regex("\\bVec3\\b"), "Vector3d")
                    .replace(Regex("\\bAABB\\b"), "AxisAlignedBB")
                    .replace(Regex("\\bSoundSource\\b"), "SoundCategory")
                    .replace(Regex("\\bEquipmentSlot\\b"), "EquipmentSlotType")
                    .replace(Regex("\\bFoodData\\b"), "FoodStats")
                    .replace(Regex("\\bContainer\\b"), "IInventory")
                    .replace(Regex("\\bPlayerEnderChestContainer\\b"), "EnderChestInventory")
                    .replace(Regex("\\bPoseStack\\b"), "MatrixStack")
                    .replace(Regex("\\bFont\\b"), "FontRenderer")
                    .replace(Regex("\\bInventory\\b"), "PlayerInventory")
                    .replace(Regex("\\bAbilities\\b"), "PlayerAbilities")
                    .replace(Regex("\\bItemLike\\b"), "IItemProvider")
                    .replace("net.minecraft.item.IItemProvider", "net.minecraft.util.IItemProvider")
                    .replace(Regex("\\bUseOnContext\\b"), "ItemUseContext")
                    .replace(Regex("\\bAnimal\\b"), "AnimalEntity")
                    .replace(Regex("\\bAgableMob\\b"), "AgeableEntity")
                    .replace(Regex("\\bDamageSource\\b"), "DamageSource")
                    .replace(Regex("\\bInteractionHand\\b"), "Hand")
                    .replace(Regex("\\bBlockEntity\\b"), "TileEntity")
                    .replace(Regex("\\bLightningBolt\\b"), "LightningBoltEntity")
                    .replace(Regex("\\bCamera\\b"), "ActiveRenderInfo")
                    .replace(Regex("\\bMultiBufferSource\\b"), "IRenderTypeBuffer")
                    .replace(Regex("\\bRegistryAccess\\b"), "DynamicRegistries")
                    .replace(Regex("\\bLevelAccessor\\b"), "IWorld")
                    .replace(Regex("\\bWorldAccessor\\b"), "IWorld")
                    .replace(Regex("\\bCropBlock\\b"), "CropsBlock")
                    .replace(Regex("\\bFarmBlock\\b"), "FarmlandBlock")
                    .replace(Regex("\\bLevelRenderer\\b"), "WorldRenderer")
                    .replace(Regex("\\bGui\\b"), "IngameGui")
                    .replace(Regex("\\bMouseHandler\\b"), "MouseHelper")
                    .replace(Regex("\\bCraftingContainer\\b"), "CraftingInventory")
                    .replace(Regex("\\bResultSlot\\b"), "CraftingResultSlot")
                    .replace(Regex("\\bVertexConsumer\\b"), "IVertexBuilder")
                    .replace(Regex("\\bHitResult\\b"), "RayTraceResult")
                    .replace("Biome.Precipitation", "Biome.RainType")
                    .replace("Commands.CommandSelection", "Commands.EnvironmentType")
                    .replace("event.getWorld().getServer()", "((net.minecraft.world.World) event.getWorld()).getServer()")
                    .replace("sendPacket(ServerPlayerEntity player, Packet<?> packet)", "sendPacket(ServerPlayerEntity player, IPacket<?> packet)")
                    .replace("net.minecraft.nbt.CompoundTag", "net.minecraft.nbt.CompoundNBT")
                    .replace("net.minecraft.world.item.TieredItem", "net.minecraft.item.TieredItem")
                    .replace(Regex("\\bClientboundSetTitlesPacket\\b"), "STitlePacket")
                    .replace(Regex("\\bClientboundSoundPacket\\b"), "SPlaySoundEffectPacket")
                    .replace(Regex("\\bLocalPlayer\\b"), "ClientPlayerEntity")
                    .replace(Regex("\\bClientLevel\\b"), "ClientWorld")
                    .replace(Regex("\\bLevelLoadingScreen\\b"), "DownloadTerrainScreen")
                    .replace("RayTraceContext.Block.", "RayTraceContext.BlockMode.")
                    .replace("RayTraceContext.Fluid.", "RayTraceContext.FluidMode.")
                    .replace("Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", "Lnet/minecraft/entity/LivingEntity;actuallyHurt(Lnet/minecraft/util/DamageSource;F)V")
                    .replace("Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/level/World;Lnet/minecraft/world/entity/player/PlayerEntity;I)V", "Lnet/minecraft/item/ItemStack;onCraftedBy(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V")
                    .replace("Lnet/minecraft/world/level/block/FarmlandBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/World;Lnet/minecraft/core/BlockPos;)V", "Lnet/minecraft/block/FarmlandBlock;turnToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
            }
        }
    }
}
