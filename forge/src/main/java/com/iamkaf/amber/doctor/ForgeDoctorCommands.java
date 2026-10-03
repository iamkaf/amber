package com.iamkaf.amber.doctor;

import com.iamkaf.amber.platform.services.IClientDoctorCommands;

public final class ForgeDoctorCommands implements IClientDoctorCommands {
    @Override
    public void register() {
        com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents.EVENT
                .register((dispatcher, context) -> dispatcher.register(ClientDoctorCommands.root()));
    }
}
