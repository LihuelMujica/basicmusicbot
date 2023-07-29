package com.lihuelmujica.musicbot.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface Command {

    String getName();

    String getDescription();

    List<OptionData> getOptions();

    @Async("asyncExecutor")
    void execute(SlashCommandInteractionEvent event);
}
