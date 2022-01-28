package com.killbot.bot;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.killbot.KillBot;
import com.killbot.bot.PlayerInputs;
import com.killbot.util.BotUtils;
import com.killbot.util.Debugger;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PlayerRideableJumping;

//av = position, getPositionVector();
//ax = velocity, getMot()

public class ControllablePlayer extends ServerPlayer {

    // use tickCount for alive ticks.

    public PlayerInputs input;
    private boolean crouching;
    private boolean wasFallFlying;
    private boolean handsBusy;

    private boolean wasJumping;

    private byte sprintTriggerTime;
    private byte noJumpTicks;
    private byte jumpRidingTicks;
    private float jumpRidingScale;

    protected ControllablePlayer(MinecraftServer minecraftserver, ServerLevel ServerLevel, GameProfile gameprofile) {
        super(minecraftserver, ServerLevel, gameprofile);
        this.maxUpStep = 0.6f;

        this.spawnInvulnerableTime = 0;
        this.collides = true;
        this.noPhysics = false;
        this.crouching = false;
        this.input = new PlayerInputs();

        // TODO Auto-generated constructor stub
    }

    public static ControllablePlayer createBot(Location loc, String username) {
        return createBot(username, MojangAPI.getSkin(username), loc);
    }

    public static ControllablePlayer createBot(String username, String[] skin, Location loc) {

        return createBot(username, skin, loc.getWorld(), loc.toVector());

    }

    public static ControllablePlayer createBot(String username, String[] skin, World world, Vector loc) {
        return createBot(username, skin, world, loc.getX(), loc.getY(), loc.getZ());
    }

    public static ControllablePlayer createBot(String username, String[] skin, World world, double x, double y,
            double z) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) Objects.requireNonNull(world)).getHandle();

        UUID uuid = BotUtils.randomSteveUUID();
        ControllablePlayer npc = new ControllablePlayer(nmsServer, nmsWorld, new GameProfile(uuid, username));

        // npc.connection = new ServerGamePacketListenerImpl(nmsServer, new
        // Connection(PacketFlow.CLIENTBOUND) {

        // @Override
        // public void send(Packet<?> packet,
        // @Nullable GenericFutureListener<? extends Future<? super Void>>
        // genericfuturelistener) {
        // }

        // }, npc);
        npc.getBukkitEntity().setNoDamageTicks(0);
        npc.setPos(x, y, z);
        // BotUtils.safeTeleport(npc, x, y, z);
        return npc;
    }

    public float getSpeedRatio() {
        return this.getSpeed() / 0.1f;
    }

    public float getAttackStrengthTicker() {
        return this.attackStrengthTicker;
    }

    public int getLastHurtByPlayerTime() {
        return this.lastHurtByPlayerTime;
    }

    public boolean isMoving() {
        Vec2 vec2 = this.input.getMoveVector();
        return vec2.x != 0.0f || vec2.y != 0.0f;
    }

    public boolean hasEnoughImpulseToStartSprinting() {
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double) this.input.forwardImpulse >= 0.8;
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    public boolean isRidingJumpable() {
        Entity entity = this.getVehicle();
        return this.isPassenger() && entity instanceof PlayerRideableJumping
                && ((PlayerRideableJumping) ((Object) entity)).canJump();
    }

    public boolean isAlwaysFlying() {
        return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
    }

    public boolean getJumping() {
        return jumping;
    }

    public float getJumpRidingScale() {
        return jumpRidingScale;
    }

    private boolean suffocatesAt(BlockPos blockPos2) {
        AABB aABB = this.getBoundingBox();
        AABB aABB2 = new AABB(blockPos2.getX(), aABB.minY, blockPos2.getZ(), (double) blockPos2.getX() + 1.0, aABB.maxY,
                (double) blockPos2.getZ() + 1.0).deflate(1.0E-7);
        return this.level.hasBlockCollision(this, aABB2,
                (blockState, blockPos) -> blockState.isSuffocating(this.level, (BlockPos) blockPos));
    }

    private void moveTowardsClosestSpace(double d, double d2) {
        Direction[] arrdirection;
        BlockPos blockPos = new BlockPos(d, this.getY(), d2);
        if (!this.suffocatesAt(blockPos)) {
            return;
        }
        double d3 = d - (double) blockPos.getX();
        double d4 = d2 - (double) blockPos.getZ();
        Direction direction = null;
        double d5 = Double.MAX_VALUE;
        for (Direction direction2 : arrdirection = new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH,
                Direction.SOUTH }) {
            double d6;
            double d7 = direction2.getAxis().choose(d3, 0.0, d4);
            double d8 = d6 = direction2.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d7 : d7;
            if (!(d6 < d5) || this.suffocatesAt((BlockPos) blockPos.relative(direction2)))
                continue;
            d5 = d6;
            direction = direction2;
        }
        if (direction != null) {
            Vec3 vec3 = this.getDeltaMovement();
            if (direction.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(0.1 * (double) direction.getStepX(), vec3.y, vec3.z);
            } else {
                this.setDeltaMovement(vec3.x, vec3.y, 0.1 * (double) direction.getStepZ());
            }
        }
    }

    public void controlsAndCollisions() {
        int n;
        boolean bl;
        boolean bl2;
        ItemStack itemStack;
        // ++this.sprintTime;
        if (this.sprintTriggerTime > 0) {
            --this.sprintTriggerTime;
        }
        boolean bl3 = this.wasJumping;
        boolean bl4 = this.input.shiftKeyPressed;
        boolean bl5 = this.hasEnoughImpulseToStartSprinting();
        this.crouching = !this.getAbilities().flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING)
                && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
        this.input.tick(this.isMovingSlowly());
        if (this.isUsingItem() && !this.isPassenger()) {
            this.input.leftImpulse *= 0.2f;
            this.input.forwardImpulse *= 0.2f;
            this.sprintTriggerTime = 0;
        }
        boolean bl6 = false;
        if (!this.noPhysics) {
            this.moveTowardsClosestSpace(this.getX() - (double) this.getBbWidth() * 0.35,
                    this.getZ() + (double) this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() - (double) this.getBbWidth() * 0.35,
                    this.getZ() - (double) this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double) this.getBbWidth() * 0.35,
                    this.getZ() - (double) this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double) this.getBbWidth() * 0.35,
                    this.getZ() + (double) this.getBbWidth() * 0.35);
        }
        if (bl4) {
            this.sprintTriggerTime = 0;
        }
        boolean bl7 = bl = (float) this.getFoodData().getFoodLevel() > 6.0f || this.getAbilities().mayfly;
        if (!(!this.onGround && !this.isUnderWater() || bl4 || bl5 || !this.hasEnoughImpulseToStartSprinting()
                || this.isSprinting() || !bl || this.isUsingItem() || this.hasEffect(MobEffects.BLINDNESS))) {
            if (this.sprintTriggerTime > 0 || this.input.sprintKeyPressed) {
                this.setSprinting(true);
            } else {
                this.sprintTriggerTime = 7;
            }
        }
        if (!this.isSprinting() && (!this.isInWater() || this.isUnderWater()) && this.hasEnoughImpulseToStartSprinting()
                && bl && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && this.input.sprintKeyPressed) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            bl2 = !this.input.hasForwardImpulse() || !bl;
            int n2 = n = bl2 || this.horizontalCollision || this.isInWater() && !this.isUnderWater() ? 1 : 0;
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.shiftKeyPressed && bl2 || !this.isInWater()) {
                    this.setSprinting(false);
                }
            } else if (n != 0) {
                this.setSprinting(false);
            }
        }
        bl2 = false;
        if (this.getAbilities().mayfly) {
            if (this.isAlwaysFlying()) {
                if (!this.getAbilities().flying) {
                    this.getAbilities().flying = true;
                    bl2 = true;
                    this.onUpdateAbilities();
                }
            } else if (!bl3 && this.input.jumping && !bl6) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else if (!this.isSwimming()) {
                    this.getAbilities().flying = !this.getAbilities().flying;
                    bl2 = true;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }
        if (this.input.jumping && !bl2 && !bl3 && !this.getAbilities().flying &&
                !this.isPassenger() && !this.onClimbable()
                && (itemStack = this.getItemBySlot(EquipmentSlot.CHEST)).is(Items.ELYTRA) &&
                ElytraItem.isFlyEnabled(itemStack) && this.tryToStartFallFlying()) {
            // this.connection.send(new ServerboundPlayerCommandPacket(this,
            // ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            // client only.
        }
        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.shiftKeyPressed && this.isAffectedByFluids()) {
            this.goDownInWater();
        }
        if (this.getAbilities().flying) { // assume not spectating someone.
            n = 0;
            if (this.input.shiftKeyPressed) {
                --n;
            }
            if (this.input.jumping) {
                ++n;
            }
            if (n != 0) {
                this.setDeltaMovement(
                        this.getDeltaMovement().add(0.0, (float) n * this.getAbilities().getFlyingSpeed() * 3.0f, 0.0));
            }
        }
        if (this.isRidingJumpable()) {
            PlayerRideableJumping playerRideableJumping = (PlayerRideableJumping) ((Object) this.getVehicle());
            if (this.jumpRidingTicks < 0) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0f;
                }
            }
            
            if (bl3 && !this.input.jumping) {
                this.jumpRidingTicks = -10;
                playerRideableJumping.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0f));
   
                // this.sendRidingJump(); // client only

            } else if (!bl3 && this.input.jumping) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0f;
            } else if (bl3) {
                ++this.jumpRidingTicks;
                this.jumpRidingScale = this.jumpRidingTicks < 10 ? (float) this.jumpRidingTicks * 0.1f
                        : 0.8f + 2.0f /
                                (float) (this.jumpRidingTicks - 9) * 0.1f;
            }
        } else {
            this.jumpRidingScale = 0.0f;
        }
        if (this.onGround && this.getAbilities().flying && !this.isAlwaysFlying()) {
            this.getAbilities().flying = false;
            this.onUpdateAbilities();
        }

    }

    @Override
    public boolean isCrouching() {
        return !this.getAbilities().flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING)
                && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.handsBusy = false;
        if (this.getVehicle() instanceof Boat) {
            Boat boat = (Boat)this.getVehicle();
            boat.setInput(this.input.left, this.input.right, this.input.forward, this.input.back);
            this.handsBusy |= this.input.left || this.input.right || this.input.forward || this.input.back;
        }
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }


    @Override
    public void tick() {

        // if (!this.isAlive())
        //     return;

        this.xxa = this.input.leftImpulse;
        this.zza = this.input.forwardImpulse;
        this.setSprinting(this.input.sprintKeyPressed);
        this.setShiftKeyDown(this.input.shiftKeyPressed);
        this.setJumping(this.input.jumping);

        if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround));
            this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyPressed));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isControlledByLocalInstance()) {
                this.connection.send(new ServerboundMoveVehiclePacket(entity));
            }
        }

        super.doTick();
        this.gameMode.tick();
        this.controlsAndCollisions();

        // Debugger.log(this.fallDistance, this.isFallFlying());

        if (this.noJumpTicks > 0)
            --this.noJumpTicks;
        if (this.invulnerableTime > 0)
            --this.invulnerableTime;


        this.wasJumping = this.input.jumping;
    }

    // May optimize these later.
    public Vector getVelocity() {
        return getBukkitEntity().getVelocity();
    }

    public Vector getPosition() {
        return getBukkitEntity().getLocation().toVector();
    }

    public void lookAt(org.bukkit.entity.Entity e) {
        this.lookAt(Anchor.EYES, ((CraftPlayer) e).getHandle(), Anchor.EYES);
    };

    public double distanceTo(org.bukkit.entity.Entity e) {
        return distanceTo(((CraftPlayer) e).getHandle());
    }

}
