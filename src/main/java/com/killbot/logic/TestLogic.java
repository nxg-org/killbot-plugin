package com.killbot.logic;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.level.ServerPlayer;

public class TestLogic extends Logic {

    public TestLogic(ServerPlayer player) {
        super(player);
        // TODO Auto-generated constructor stub
    }

    public static TestLogic apply(ServerPlayer player) {

        return new TestLogic(player);

    }

    @Override
    public void setTarget(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearTarget() {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveToPosition(Location loc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void attackTarget() {
        // TODO Auto-generated method stub

    }

    @Override
    public void attackNearestNonBotPlayer() {
        // TODO Auto-generated method stub

    }

    @Override
    public void attackNearestEntity() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        
    }
}
