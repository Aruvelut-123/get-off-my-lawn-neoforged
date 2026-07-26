package draylar.goml.item;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimUtils;
import draylar.goml.api.WorldParticleUtils;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GogglesItem extends ArmorItem {
    public GogglesItem(Properties settings) {
        super(ArmorMaterials.IRON, Type.HELMET, settings.durability(-1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (entity instanceof ServerPlayer player
                && (selected || player.getItemBySlot(EquipmentSlot.HEAD) == stack
                || player.getItemBySlot(EquipmentSlot.OFFHAND) == stack)) {
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

}
