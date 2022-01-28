package com.killbot.logic;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.killbot.KillBot;
import com.killbot.bot.BaseBot;
import com.killbot.bot.PlayerInputs.Controls;
import com.killbot.util.BotUtils;
import com.killbot.util.Debugger;
import com.killbot.util.MathUtils;
import com.killbot.util.MovementUtils;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.http.util.EntityUtils;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
// import org.bukkit.entity.Horse;
import net.minecraft.world.entity.animal.horse.Horse;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.netty.util.internal.MathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class TestLogic extends Logic {

    private float reach;
    private float viewDistance;
    private int strafeCounter;
    private Random random;
    private Controls currentStrafeDir;

    public TestLogic(BaseBot player) {
        super(player);
        this.strafeCounter = 0;
        this.random = new Random();
    }

    public static TestLogic apply(BaseBot player) {
        TestLogic log = new TestLogic(player);
        log.init();
        return log;
    }

    @Override
    public void init() {
        super.init();
        Set<ServerPlayer> ignored = KillBot.getPlugin().getManager().getIgnored();
        Set<BaseBot> bots = KillBot.getPlugin().getManager().getBots();

        List<net.minecraft.world.entity.player.Player> targets = bot.level.getEntities(
                net.minecraft.world.entity.EntityType.PLAYER,
                AABB.unitCubeFromLowerCorner(bot.position()).inflate(this.viewDistance),
                e -> !ignored.contains(e) && !bots.contains(e));
        
        if (targets.size() > 0) {
            setTarget(targets.get(0).getBukkitEntity());
            bot.getBukkitEntity().getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        }
   
    }

    @Override
    public void release() {
        super.release();

    }


    public void realisticAttack(org.bukkit.entity.Entity e) {
        realisticAttack(((CraftPlayer) e).getHandle());
    }

    public void realisticAttack(Entity e) {
        if (MathUtils.trueDistance(bot, e) < this.reach) {
            bot.lookAt(Anchor.EYES, e, Anchor.EYES);
            bot.attack(e);
            bot.swing(InteractionHand.MAIN_HAND);
        }

    }

    @Override
    public void moveToPosition(Location loc) {
        Vector test = loc.toVector();
        bot.lookAt(Anchor.EYES, new Vec3(test.getX(), test.getY(), test.getZ()));
        bot.input.setControl(Controls.FORWARD, true);
    }

    private void mountHorse(net.minecraft.world.entity.animal.horse.Horse horse) {
        bot.lookAt(Anchor.EYES, horse, Anchor.EYES);
        bot.swing(InteractionHand.MAIN_HAND);
        bot.startRiding(horse, true);
    };

    private void checkForHorse(boolean shouldDismount) {
        if (shouldDismount) {
            bot.stopRiding();
        } else if (!bot.isPassenger()) {
            List<net.minecraft.world.entity.animal.horse.Horse> horses = bot.level.getEntities(
                    net.minecraft.world.entity.EntityType.HORSE,
                    AABB.unitCubeFromLowerCorner(bot.position()).inflate(this.reach),
                    e -> ((net.minecraft.world.entity.animal.horse.Horse) e).getPassengers().size() == 0);
            horses = horses.stream().sorted((a, b) -> Math.round(
                    bot.distanceTo(a)
                            - bot.distanceTo(b)))
                    .toList();
            if (horses.size() > 0) {
                mountHorse(horses.get(0));
            }

            // (net.minecraft.world.entity.animal.horse.Horse) e).isSaddled() &&

        } else {
            net.minecraft.world.entity.Entity entity = bot.getRootVehicle();
            if (entity != bot && entity instanceof net.minecraft.world.entity.animal.horse.Horse) {
                net.minecraft.world.entity.animal.horse.Horse horse = (net.minecraft.world.entity.animal.horse.Horse) entity;
                horse.setTamed(true);
                horse.setSharedFlag(4, true);
                // horse.setYRot(bot.yHeadRot);
                // ((net.minecraft.world.entity.animal.horse.Horse) entity).travel(new
                // Vec3(bot.xxa, bot.yya, bot.zza));
            }

        }

        // public void handleMoveVehicle(ServerboundMoveVehiclePacket
        // serverboundMoveVehiclePacket) {
        // PacketUtils.ensureRunningOnSameThread(serverboundMoveVehiclePacket, this,
        // bot.getLevel());
        // if
        // (ServerGamePacketListenerImpl.containsInvalidValues(serverboundMoveVehiclePacket.getX(),
        // serverboundMoveVehiclePacket.getY(), serverboundMoveVehiclePacket.getZ(),
        // serverboundMoveVehiclePacket.getYRot(),
        // serverboundMoveVehiclePacket.getXRot())) {
        // this.disconnect(new
        // TranslatableComponent("multiplayer.disconnect.invalid_vehicle_movement"));
        // return;
        // }

        // net.minecraft.world.entity.Entity entity = this.bot.getRootVehicle();
        // if (entity != this.bot && entity.getControllingPassenger() == bot) {
        // ServerLevel serverLevel = bot.getLevel();

        // double d = entity.getX();
        // double d2 = entity.getY();
        // double d3 = entity.getZ();
        // double d4 = entity.getX(); //
        // ServerGamePacketListenerImpl.clampHorizontal(serverboundMoveVehiclePacket.getX());
        // double d5 = entity.getY(); //
        // ServerGamePacketListenerImpl.clampVertical(serverboundMoveVehiclePacket.getY());
        // double d6 = entity.getZ(); //
        // ServerGamePacketListenerImpl.clampHorizontal(serverboundMoveVehiclePacket.getZ());
        // float f = Mth.wrapDegrees(bot.getYRot());
        // //Mth.wrapDegrees(serverboundMoveVehiclePacket.getYRot());
        // float f2 = Mth.wrapDegrees(bot.getXRot());
        // //Mth.wrapDegrees(serverboundMoveVehiclePacket.getXRot());
        // double d7 = d4 - this.vehicleFirstGoodX;
        // double d8 = d5 - this.vehicleFirstGoodY;
        // double d9 = d6 - this.vehicleFirstGoodZ;
        // double d10 = d7 * d7 + d8 * d8 + d9 * d9;
        // double d11 = entity.getDeltaMovement().lengthSqr();
        // boolean bl = serverLevel.noCollision(entity,
        // entity.getBoundingBox().deflate(0.0625));
        // d7 = d4 - this.vehicleLastGoodX;
        // d8 = d5 - this.vehicleLastGoodY - 1.0E-6;
        // d9 = d6 - this.vehicleLastGoodZ;
        // entity.move(MoverType.PLAYER, new Vec3(d7, d8, d9));
        // double d12 = d8;
        // d7 = d4 - entity.getX();
        // d8 = d5 - entity.getY();
        // if (d8 > -0.5 || d8 < 0.5) {
        // d8 = 0.0;
        // }
        // d9 = d6 - entity.getZ();
        // d10 = d7 * d7 + d8 * d8 + d9 * d9;
        // boolean bl2 = false;
        // if (d10 > 0.0625) {
        // bl2 = true;
        // }

        // entity.absMoveTo(d4, d5, d6, f, f2);
        // boolean bl3 = serverLevel.noCollision(entity,
        // entity.getBoundingBox().deflate(0.0625));
        // // if (bl && (bl2 || !bl3)) {
        // // entity.absMoveTo(d, d2, d3, f, f2);
        // // this.connection.send(new ClientboundMoveVehiclePacket(entity));
        // // return;
        // // }
        // // bot.getLevel().getChunkSource().move(bot);
        // bot.checkMovementStatistics(bot.getX() - d, bot.getY() - d2, bot.getZ() -
        // d3);
        // this.clientVehicleIsFloating = d12 >= -0.03125 &&
        // !bot.server.isFlightAllowed() && this.noBlocksAround(entity);
        // this.vehicleLastGoodX = entity.getX();
        // this.vehicleLastGoodY = entity.getY();
        // this.vehicleLastGoodZ = entity.getZ();
        // }
        // }
        // bot.rideTick();

    }

    // private boolean noBlocksAround(Entity entity) {
    // return
    // entity.level.getBlockStates(entity.getBoundingBox().inflate(0.0625).expandTowards(0.0,
    // -0.55, 0.0)).allMatch(BlockBehaviour.BlockStateBase::isAir);
    // }

    private void doStrafeNow() {
        if (bot.getLastHurtByPlayerTime() < 60) {
            bot.input.setControl(Controls.LEFT, false);
            bot.input.setControl(Controls.RIGHT, false);
            this.currentStrafeDir = null;
        } else if (this.strafeCounter < 0) {
            Long temp = (Math.round(Math.floor(random.nextDouble() * 20) + 5));
            this.strafeCounter = temp.intValue();
            float intelliRand = random.nextFloat();
            Controls smartDir = intelliRand < 0.5 ? Controls.LEFT : Controls.RIGHT;
            Controls oppositeSmartDir = intelliRand >= 0.5 ? Controls.LEFT : Controls.RIGHT;
            if (bot.distanceTo(target) <= this.reach + 3) {
                this.bot.input.setControl(smartDir, true);
                this.bot.input.setControl(oppositeSmartDir, false);
                this.currentStrafeDir = smartDir;
            } else {
                if (this.currentStrafeDir != null)
                    this.bot.input.setControl(this.currentStrafeDir, false);
                this.currentStrafeDir = null;
            }
        }
        this.strafeCounter--;
    }

    @Override
    public void perTick() {
        float scale = bot.getAttackStrengthScale(0.5f);
        double dist = MathUtils.trueDistance(bot, target);
        boolean shouldSprint = bot.isOnGround() || bot.isUnderWater() || bot.getRootVehicle() != bot;
        boolean twoToFourBlocks = Math.abs(dist - this.reach) < 1;
        boolean targetUnderWater = ((CraftPlayer) target).getHandle().isUnderWater();
        boolean shouldSink = bot.isInWater() && !bot.isUnderWater() && targetUnderWater
                && !MovementUtils.anyBlocksSolidUnderneath(bot);
        boolean shouldJump = (!twoToFourBlocks && (bot.isOnGround() || !targetUnderWater))
                && (bot.getRootVehicle() == bot);
        boolean shouldDismount = targetUnderWater;

        // Debugger.log(bot.getJumpRidingScale(), shouldJump);
        bot.input.setControl(Controls.JUMP, shouldJump);
        bot.input.setControl(Controls.SNEAK, shouldSink);
        if ((scale < 0.1 || dist < this.reach - 1) && (bot.isOnGround() || bot.isInWater())) {
            bot.input.setControl(Controls.SPRINT, false);
            bot.input.setControl(Controls.FORWARD, false);
            bot.input.setControl(Controls.BACK, true);
        } else {
            if (shouldSprint)
                bot.input.setControl(Controls.SPRINT, true);
            bot.input.setControl(Controls.FORWARD, true);
            bot.input.setControl(Controls.BACK, false);
        }

        if (scale > 0.9) {
            if (bot.getDeltaMovement().y < -0.3f && dist < this.reach - 1) {
                attackTarget(true);
            } else if (dist < this.reach && (bot.isOnGround() || bot.isInWater() || bot.isPassenger())) {
                attackTarget(false);
            }

        }

        bot.lookAt(target);
        doStrafeNow();
        movementLogic();
        checkForHorse(shouldDismount);

    }

    @Override
    public void attackTarget() {
        attackTarget(false);
    }

    public void attackTarget(boolean wantCrit) {
        if (wantCrit) {
            bot.fallDistance = 1.0f;
            bot.input.setControl(Controls.SPRINT, false);
            bot.setSprinting(false);
        }
        this.realisticAttack(target);

    }

    private void movementLogic() {

        if (bot.isInWater() && !bot.isUnderWater()) {
            bot.setPose(Pose.STANDING);
            bot.setSwimming(false);
            bot.input.setControl(Controls.SPRINT, false);
            bot.setSprinting(false);

        } else if (bot.isInWater()) {
            bot.input.setControl(Controls.SPRINT, true);
            bot.setSwimming(true);
            bot.setPose(Pose.SWIMMING);
        }

    }

    @Override
    public void attackNearestNonBotPlayer() {
        // TODO Auto-generated method stub

    }

    @Override
    public void attackNearestEntity() {
        // TODO Auto-generated method stub

    }
}
