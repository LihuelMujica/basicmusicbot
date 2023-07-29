package com.lihuelmujica.musicbot.discord.commands.music;

import com.lihuelmujica.musicbot.discord.commands.Command;
import com.lihuelmujica.musicbot.discord.lavaplayer.GuildMusicManager;
import com.lihuelmujica.musicbot.discord.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Queue implements Command {
    @Override
    public String getName() {
        return "ver-playlist";
    }

    @Override
    public String getDescription() {
        return "Muestra la playlist actual";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("I am not in an audio channel").queue();
            return;
        }

        if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("You are not in the same channel as me").queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        List<AudioTrack> queue = new ArrayList<>(guildMusicManager.getTrackScheduler().getQueue());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Playlist actual");
        if(queue.isEmpty()) {
            embedBuilder.setDescription("La playlist actual está vacía");
        }
        for(int i = 0; i < queue.size(); i++) {
            AudioTrackInfo info = queue.get(i).getInfo();
            embedBuilder.addField(i+1 + ") " +
                    info.title
                            .replace("(","")
                            .replace(")", "")
                    , "Duración: " + info.length/1000/60 % 60 + ":" + info.length/1000 % 60, false);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
