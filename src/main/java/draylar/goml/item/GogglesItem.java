package draylar.goml.item;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimUtils;
import draylar.goml.api.WorldParticleUtils;
import eu.pb4.polymer.common.impl.tweaker.PacketContext;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class GogglesItem extends Item implements PolymerItem {
    public GogglesItem(Properties settings) {
        super(settings.humanoidArmor(ArmorMaterials.IRON, ArmorType.HELMET).component(DataComponents.MAX_DAMAGE, null));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof ServerPlayer player && slot != null) {
            if (player.tickCount % 70 == 0) {
                var distance = player.level().getServer().getPlayerList().getViewDistance() * 16;

                ClaimUtils.getClaimsInBox(
                        world,
                        entity.blockPosition().offset(-distance, -distance, -distance),
                        entity.blockPosition().offset(distance, distance, distance)).forEach(
                        claim -> {
                            ClaimUtils.drawClaimInWorld(player, claim.getValue());
                        });
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return Items.IRON_HELMET;
    }
}
