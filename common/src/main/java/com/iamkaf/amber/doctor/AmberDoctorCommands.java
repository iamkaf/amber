//? if >=1.21.11 {
package com.iamkaf.amber.doctor;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.commands.v1.SimpleCommands;
import com.iamkaf.amber.api.doctor.v1.Doctor;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.doctor.DoctorReports;
import com.iamkaf.amber.doctor.ClientDoctorCommands;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;

public final class AmberDoctorCommands {
    private AmberDoctorCommands() {}

    public static void initialize() {
        Doctor.registerServer(DoctorReports.amberInfo(), (context, section) -> DoctorReports.amber(section));
        CommandEvents.EVENT.register((dispatcher, registryAccess, environment) -> {
            var server = Commands.literal("server")
                    .executes(context -> inspect(context.getSource(), Optional.ofNullable(context.getSource().getPlayer())))
                    .then(Commands.argument("player", EntityArgument.player())
                            //? if >=1.21.11
                            .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                            //? if <1.21.11
                            /*.requires(source -> source.hasPermission(2))*/
                            .executes(context -> inspect(context.getSource(),
                                    Optional.of(EntityArgument.getPlayer(context, "player")))));
            dispatcher.register(SimpleCommands.createBaseCommand(Constants.MOD_ID)
                    .then(Commands.literal("doctor")
                            .executes(context -> inspect(context.getSource(),
                                    Optional.ofNullable(context.getSource().getPlayer())))
                            .then(server)));
        });
        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientDoctorCommands::initialize);
    }

    private static int inspect(CommandSourceStack source, Optional<ServerPlayer> player) {
        DoctorReports.render(Doctor.inspectServer(new Doctor.ServerContext(source.getServer(), player)), message -> {
            //? if >=1.20
            source.sendSuccess(() -> message, false);
            //? if <1.20
            /*source.sendSuccess(message, false);*/
        });
        return 1;
    }
}
//?}
