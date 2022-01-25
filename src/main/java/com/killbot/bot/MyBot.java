package com.killbot.bot;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.killbot.KillBot;
import com.killbot.util.BotUtils;
import com.killbot.util.Debugger;
import com.killbot.util.MojangAPI;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
// import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
// import net.minecraft.network.NetworkManager;
// import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.network.Connection;
// import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
// import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

//av = position, getPositionVector();
//ax = velocity, getMot()

public class MyBot extends ServerPlayer {

    //use tickCount for alive ticks.

    private int kills;

    // private byte fireTicks;
    // private byte groundTicks;
    // private byte jumpTicks;
    // private byte noFallTicks;
    private byte noJumpTicks;

    private MyBot(MinecraftServer minecraftserver, ServerLevel ServerLevel, GameProfile gameprofile) {
        super(minecraftserver, ServerLevel, gameprofile);
        this.maxUpStep = 1f;
        this.noPhysics = false;
        // TODO Auto-generated constructor stub
    }

    public static MyBot createBot(Location loc, String username) {
        return createBot(username, MojangAPI.getSkin(username), loc);
    }

    public static MyBot createBot(String username, String[] skin, Location loc) {

        return createBot(username, skin, loc.getWorld(), loc.toVector());

    }

    public static MyBot createBot(String username, String[] skin, World world, Vector loc) {
        return createBot(username, skin, world, loc.getX(), loc.getY(), loc.getZ());
    }

    public static MyBot createBot(String username, String[] skin, World world, double x, double y, double z) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) Objects.requireNonNull(world)).getHandle();

        UUID uuid = BotUtils.randomSteveUUID();
        MyBot npc = new MyBot(nmsServer, nmsWorld, new GameProfile(uuid, username));

        npc.setPos(x, y, z);
        npc.getBukkitEntity().setNoDamageTicks(0);
        npc.connection = new ServerGamePacketListenerImpl(nmsServer, new Connection(PacketFlow.CLIENTBOUND) {

            @Override
            public void send(Packet<?> packet,
                    @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
            }

        }, npc);
        KillBot.getRenderer().initRenderToAll(npc);
        nmsWorld.addEntity(npc, SpawnReason.CUSTOM);
        KillBot.getPlugin().getManager().getBots().add(npc); // may move to manager.
        KillBot.getRenderer().renderToAll(npc);

        return npc;
    }

    /**
     * Moves a delta of passed-in vector. AKA, use with velocity.
     * 
     * @param vec
     */
    public void move(Vector vec) {
        // super.move(MoverType.SELF, new Vec3(vec.getX(), vec.getY(), vec.getZ()));
        this.travel(new Vec3(vec.getX(), vec.getY(), vec.getZ()));
    }

    public void jump() {
        double d7 = 0.0D;
      
        if (this.isAffectedByFluids()) {
      
            if (this.isInLava()) {
               d7 = this.getFluidHeight(FluidTags.LAVA);
            } else {
               d7 = this.getFluidHeight(FluidTags.WATER);
            }
        }
   
            boolean flag = this.isInWater() && d7 > 0.0D;
            double d8 = this.getFluidJumpThreshold();
            if (!flag || this.onGround && d7 <= d8) {
               if (!this.isInLava() || this.onGround && d7 <= d8) {
                   //&& this.noJumpTicks == 0
                  if ((this.onGround || flag && d7 <= d8) && this.noJumpTicks == 0) {
                     super.jumpFromGround();
                     this.noJumpTicks = 2;
                  }
               } else {
                  super.jumpInLiquid(FluidTags.LAVA);
               }
            } else {
               super.jumpInLiquid(FluidTags.WATER);
            }
    }


    public void checkSwim(boolean enabled) {
        if (enabled) {
        
         
        } else {
            setSwimming(false);
        
        }
     
    }

    @Override
    public void tick() {
       
        if (!this.isAlive())
        return;

        super.tick();
        super.baseTick();

        if (this.noJumpTicks > 0) 
            --this.noJumpTicks;

        float health = getHealth();
        float maxHealth = getMaxHealth();
        float regenAmount = 0.025f;
        float amount;

        if (health < maxHealth - regenAmount) {
            amount = health + regenAmount;
        } else {
            amount = maxHealth;
        }

        this.setSprinting(true);
        if (!this.isInWater()) {
            setPose(Pose.STANDING);
            setSwimming(false);
            this.jump();
         
        } else {
            setSwimming(true);
            setPose(Pose.SWIMMING);
        }
        this.checkSwim(this.isInWater());

        Vec3 dir = new Vec3(0, 0, 1);
        dir.scale(this.getSpeed() / 0.1);
        Debugger.log(this.getSpeed(), this.isSprinting());
        Entity entity = ((CraftPlayer) Bukkit.getServer().getOnlinePlayers().stream().filter(player -> player.getName().equals("Generel_Schwerz")).toArray()[0]).getHandle();
        this.lookAt(Anchor.EYES, entity, Anchor.EYES);
        // if (this.isSprinting()) dir.scale(1.3);
        this.travel(dir);
  

        setHealth(amount);
        // oldVelocity = velocity.clone();
    }

    public void testAttack(org.bukkit.entity.Entity e) {
        testAttack((net.minecraft.world.entity.Entity) e);
    }

    public void testAttack(Entity e) {
        this.attack(e);
    }

    // Inefficient.
    public boolean checkGround() {
        if (getVelocity().getY() > 0) {
            return false;
        }

        World world = getBukkitEntity().getWorld();
        BoundingBox box = getBukkitEntity().getBoundingBox();

        double[] xVals = {
                box.getMinX(),
                box.getMaxX()
        };

        double[] zVals = {
                box.getMinZ(),
                box.getMaxZ()
        };

        for (double x : xVals) {
            for (double z : zVals) {

                Vector loc = new Vector(x, getPosition().getY() - 0.01, z);
                Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                if (block.getType().isSolid() && BotUtils.solidAt(world, loc.getX(), loc.getY(), loc.getZ())) {
                    return true;
                }

            }
        }
        return false;
    }

    //May optimize these later.
    public Vector getVelocity() {
        return getBukkitEntity().getVelocity();
    }

    public Vector getPosition() {
        return getBukkitEntity().getLocation().toVector();
    }

}
