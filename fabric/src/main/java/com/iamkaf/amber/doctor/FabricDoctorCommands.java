package com.iamkaf.amber.doctor;

import com.iamkaf.amber.platform.services.IClientDoctorCommands;

public final class FabricDoctorCommands implements IClientDoctorCommands {
    @Override
    public void register() {
        //? if >=1.21.11 {

        //? if >=1.19 {
        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, context) -> dispatcher.register(ClientDoctorCommands.root()));
        //?} else {
        /*net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER
                .register(ClientDoctorCommands.root());*/
        //?}
        //?}
    }
}
