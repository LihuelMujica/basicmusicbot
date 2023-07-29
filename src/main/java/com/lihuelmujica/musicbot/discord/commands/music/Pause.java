package com.lihuelmujica.musicbot.discord.commands.music;


import com.lihuelmujica.musicbot.discord.commands.Command;
import com.lihuelmujica.musicbot.discord.lavaplayer.GuildMusicManager;
import com.lihuelmujica.musicbot.discord.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Pause implements Command {

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pausa o reanuda la reproducción";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        AudioPlayer player = guildMusicManager.getTrackScheduler().getPlayer();
        EmbedBuilder musicEB = new EmbedBuilder();

        if(player.getPlayingTrack() == null) {
            musicEB.setDescription("¡No hay música sonando!");
            event.replyEmbeds(musicEB.build()).setEphemeral(true).queue();
            return;
        }

        player.setPaused(!player.isPaused());
        musicEB.addField("Track actual: ", player.getPlayingTrack().getInfo().title, false);
        if(player.isPaused()) {
            musicEB.setDescription("Se ha pausado el track actual.");
            musicEB.addField("Pausado por", event.getMember().getAsMention(), false);
        }else {
            musicEB.setDescription("El track ha sido reanudado.");
            musicEB.addField("Reanudado por", event.getMember().getAsMention(), false);
        }
        event.replyEmbeds(musicEB.build()).queue();
    }
}
