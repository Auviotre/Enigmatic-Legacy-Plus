package auviotre.enigmatic.legacy.contents.command;

import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GetCurseTimeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("getcursetime")
                .requires(source -> source.hasPermission(2))
                .executes(context -> getTime(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> getTime(context.getSource(), EntityArgument.getPlayer(context, "player")))
                )
        );
        dispatcher.register(Commands.literal("getnocursetime")
                .requires(source -> source.hasPermission(2))
                .executes(context -> getNotTime(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> getNotTime(context.getSource(), EntityArgument.getPlayer(context, "player")))
                )
        );
    }

    private static int getTime(CommandSourceStack source, ServerPlayer player) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.getcursetime",
                player.getDisplayName(),
                data.getTimeWithCurses(),
                EnigmaticHandler.getSufferingTime(player)
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int getNotTime(CommandSourceStack source, ServerPlayer player) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.getnocursetime",
                player.getDisplayName(),
                data.getTimeWithoutCurses(),
                EnigmaticHandler.getNoSufferingTime(player)
        ), true);
        return Command.SINGLE_SUCCESS;
    }

}
