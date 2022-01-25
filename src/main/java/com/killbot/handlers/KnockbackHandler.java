// package com.killbot.handlers;
// import com.killbot.KillBot;
// import com.github.retrooper.packetevents.event.PacketListener;
// import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
// import com.github.retrooper.packetevents.protocol.packettype.PacketType;
// import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;


// public class KnockbackHandler implements PacketListener {

    
//     @Override
//     public void onPacketSend(PacketSendEvent event) {
//         if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
//             WrapperPlayServerEntityVelocity interactEntity = new WrapperPlayServerEntityVelocity(event);
//             int entityId = interactEntity.getEntityId();
//             if (KillBot.getPlugin().getManager().getBots().stream().anyMatch(entity -> entity.getId() == entityId)) {
//                 event.setCancelled(true);
//             }
//         }
//     }
// };