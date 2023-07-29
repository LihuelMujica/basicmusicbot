package com.lihuelmujica.musicbot.discord;

import com.lihuelmujica.musicbot.discord.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandManager extends ListenerAdapter {

    private final List<Command> commands;
    private final JDA jda;

    @Autowired
    public CommandManager(List<Command> commands, JDA jda) {
        this.commands = commands;
        this.jda = jda;
        System.out.println(jda);
        jda.addEventListener(this);
        registerCommands();
    }


    public void registerCommands() {
        System.out.println("Registering commands");
        commands.forEach(command -> {
            System.out.println(command.getName());
            if (command.getOptions() != null) {
                jda.upsertCommand(command.getName(), command.getDescription())
                        .addOptions(command.getOptions())
                        .queue();
                return;
            }
            jda.upsertCommand(command.getName(), command.getDescription()).queue();
        });
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (Command command : commands) {
            if (event.getName().equals(command.getName())) {
                command.execute(event);
                return;
            }
        }
    }


}
