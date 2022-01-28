package com.killbot.bot;
import net.minecraft.world.phys.Vec2;



public class PlayerInputs {
    public float leftImpulse;
    public float forwardImpulse;
    public boolean forward;
    public boolean back;
    public boolean left;
    public boolean right;
    public boolean jumping;
    public boolean shiftKeyPressed;  //sneaking
    public boolean sprintKeyPressed; //sprinting

    private static final double MOVING_SLOW_FACTOR = 0.3;




    public PlayerInputs() {
        this.leftImpulse = 0.0f;
        this.forwardImpulse = 0.0f;
        this.forward = false;
        this.back = false;
        this.left = false;
        this.right = false;
        this.jumping = false;
        this.shiftKeyPressed = false;
        this.sprintKeyPressed = false;

    }

    public void tick(boolean bl) {
            float f = this.forward == this.back ? 0.0f : (this.forwardImpulse = this.forward ? 1.0f : -1.0f);
            this.leftImpulse = this.left == this.right ? 0.0f : (this.left ? 1.0f : -1.0f);

            if (bl) {
                this.leftImpulse = (float)((double)this.leftImpulse * 0.3);
                this.forwardImpulse = (float)((double)this.forwardImpulse * 0.3);
            }
    }

    public Vec2 getMoveVector() {
        return new Vec2(this.leftImpulse, this.forwardImpulse);
    }

    public boolean hasForwardImpulse() {
        return this.forwardImpulse > 1.0E-5f;
    }

    public static enum Controls {
        FORWARD,
        BACK,
        LEFT,
        RIGHT,
        SPRINT,
        SNEAK,
        JUMP
    }


    public void setControl(Controls control, boolean setting) {
        switch (control) {
            case FORWARD:
                this.forward = setting;
                break;
            case BACK:
                this.back = setting;
                break;
            case LEFT:
                this.left = setting;
                break;
            case RIGHT:
                this.right = setting;
                break;
            case SPRINT:
                this.sprintKeyPressed = setting;
                break;
            case SNEAK:
                this.shiftKeyPressed = setting;
                break;
            case JUMP:
                this.jumping = setting;
                break;

        }
    }
}
