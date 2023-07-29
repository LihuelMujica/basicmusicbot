package com.lihuelmujica.musicbot.discord.commands.music;


import com.lihuelmujica.musicbot.discord.commands.Command;
import com.lihuelmujica.musicbot.discord.lavaplayer.PlayerManager;
import com.lihuelmujica.musicbot.utils.embeds.TrackUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SongStatus implements Command {
    @Override
    public String getName() {
        return "estado-cancion";
    }

    @Override
    public String getDescription() {
        return "Te dice cuánto falta para que termine la canción";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        AudioTrack track = PlayerManager.get().getGuildMusicManager(event.getGuild()).getTrackScheduler().getCurrentTrack();
        if(track == null) {
            event.getHook().sendMessage("No hay canciones en la cola").queue();
            return;
        }
        long position = track.getPosition();
        long duration = track.getDuration();
        long remaining = duration - position;
        event.getHook().sendMessage("Quedan " + TrackUtils.formatTrackLength(remaining) + " minutos")
                .setEphemeral(true).queue();
    }
}
