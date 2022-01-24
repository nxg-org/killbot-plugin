package com.killbot.physics;

import com.killbot.physics.util.BubbleColumnDrag;

import org.bukkit.World;
import org.bukkit.util.Vector;

import org.bukkit.util.BoundingBox;

// const physics = {
//     gravity: 0.08, // blocks/tick^2 https://minecraft.gamepedia.com/Entity#Motion_of_entities
//     airdrag: Math.fround(1 - 0.02), // actually (1 - drag)
//     yawSpeed: 3.0,
//     pitchSpeed: 3.0,
//     playerSpeed: 0.1,
//     sprintSpeed: 0.3,
//     sneakSpeed: 0.3,
//     stepHeight: 0.6, // how much height can the bot step on without jump
//     negligeableVelocity: 0.003, // actually 0.005 for 1.8, but seems fine
//     soulsandSpeed: 0.4,
//     honeyblockSpeed: 0.4,
//     honeyblockJumpSpeed: 0.4,
//     ladderMaxSpeed: 0.15,
//     ladderClimbSpeed: 0.2,
//     playerHalfWidth: 0.3,
//     playerHeight: 1.8,
//     waterInertia: 0.8,
//     lavaInertia: 0.5,
//     liquidAcceleration: 0.02,
//     airborneInertia: 0.91,
//     airborneAcceleration: 0.02,
//     defaultSlipperiness: 0.6,
//     outOfLiquidImpulse: 0.3,
//     autojumpCooldown: 10, // ticks (0.5s)
//     bubbleColumnSurfaceDrag: {
//       down: 0.03,
//       maxDown: -0.9,
//       up: 0.1,
//       maxUp: 1.8
//     },
//     bubbleColumnDrag: {
//       down: 0.03,
//       maxDown: -0.3,
//       up: 0.06,
//       maxUp: 0.7
//     },
//     slowFalling: 0.125,
//     movementSpeedAttribute: mcData.attributesByName.movementSpeed.resource,
//     sprintingUUID: '662a6b8d-da3e-4c1c-8813-96ea6097278d' // SPEED_MODIFIER_SPRINTING_UUID is from LivingEntity.java
//   }

//   if (supportFeature('independentLiquidGravity')) {
//     physics.waterGravity = 0.02
//     physics.lavaGravity = 0.02
//   } else if (supportFeature('proportionalLiquidGravity')) {
//     physics.waterGravity = physics.gravity / 16
//     physics.lavaGravity = physics.gravity / 4
//   }




public class PlayerPhysics {

    private final float gravity = .08f;
    private final float airDrag = 0.98f;
    private final float yawSpeed = 3.0f;
    private final float pitchspeed = 3.0f;
    private final float playerSpeed = 0.1f;
    private final float sprintSpeed = 0.3f;
    private final float sneakSpeed = 0.3f;
    private final float stepHeight = 0.6f;
    private final float negligeableVelocity = 0.003f;
    private final float soulsandSpeed = 0.4f;
    private final float honeyblockSpeed = 0.4f;
    private final float honeyblockJumpSpeed = 0.4f;
    private final float ladderClimbSpeed = 0.15f;
    private final float ladderMaxSpeed = 0.2f;

    private final float playerHeight = 1.8f;
    private final float playerHalfWidth = 0.3f;
    private final float waterInertia = .8f;
    private final float lavaInertia = 0.5f;
    private final float liquidAcceleration = 0.02f;
    private final float airborneInertia = 0.91f;
    private final float airborneAcceleration = 0.02f;
    private final float defaultSlipperiness = 0.6f;
    private final float outOfLiquidImpulse = 0.3f;
    private final int autojumpCooldown = 10; // ticks (0.5s)

    private final BubbleColumnDrag bubbleColumnDrag;
    private final BubbleColumnDrag bubbleColumnSurfaceDrag;

    private final float slowFalling = 0.125f;
    private final float waterGravity;
    private final float lavaGravity;

    public PlayerPhysics(boolean independentGravity) {
        bubbleColumnDrag = new BubbleColumnDrag();
        bubbleColumnSurfaceDrag = new BubbleColumnDrag(true);
        if (independentGravity) {
            waterGravity = 0.02f;
            lavaGravity = 0.02f;
        } else {
            waterGravity = gravity / 16;
            lavaGravity = gravity / 4;
        }
    }


    public BoundingBox[] getWaterInBB() {
        return null;
    }


    public boolean isInWaterApplyCurrent(World world, BoundingBox aabb, Vector vel) {
        Vector accel = new Vector();

        return false;
        // function isInWaterApplyCurrent (world, bb, vel) {
        //     const acceleration = new Vec3(0, 0, 0)
        //     const waterBlocks = getWaterInBB(world, bb)
        //     const isInWater = waterBlocks.length > 0
        //     for (const block of waterBlocks) {
        //       const flow = getFlow(world, block)
        //       acceleration.add(flow)
        //     }
        
        //     const len = acceleration.norm()
        //     if (len > 0) {
        //       vel.x += acceleration.x / len * 0.014
        //       vel.y += acceleration.y / len * 0.014
        //       vel.z += acceleration.z / len * 0.014
        //     }
        //     return isInWater
        //   }
    }



}
