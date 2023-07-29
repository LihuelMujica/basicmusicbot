package com.lihuelmujica.musicbot.discord.lavaplayer;

import com.lihuelmujica.musicbot.utils.embeds.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();
    private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static PlayerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager, guild);

            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());

            return musicManager;
        });
    }

    public void play(SlashCommandInteractionEvent event, String trackURL) {
        Guild guild = event.getGuild();
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track, event);
                embedBuilder.setUrl(track.getInfo().uri);
                embedBuilder.setAuthor("Añadido a la playlist en posición " + guildMusicManager.getTrackScheduler().getQueue().size(), null, event.getMember().getEffectiveAvatarUrl());
                embedBuilder.setTitle(track.getInfo().title);
                embedBuilder.setUrl(track.getInfo().uri);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();
                if(tracks.isEmpty()) {
                    embedBuilder.setDescription("La playlist actual está vacía");
                }
                if (trackURL.contains("playlist")) {
                    embedBuilder.setAuthor("Añadiendo playlist");
                    for(int i = 0; i < tracks.size(); i++) {
                        AudioTrackInfo info = tracks.get(i).getInfo();
                        guildMusicManager.getTrackScheduler().queue(tracks.get(i), event);
                        embedBuilder.addField(i+1 + ") " +
                                        info.title
                                                .replace("(","")
                                                .replace(")", "")
                                , "Duración: " + info.length/1000/60 % 60 + ":" + info.length/1000 % 60, false);
                    }
                    event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                    return;
                }
                AudioTrack track = tracks.get(0);
                guildMusicManager.getTrackScheduler().queue(track, event);
                embedBuilder.setUrl(track.getInfo().uri);
                embedBuilder.setAuthor("Añadido a la playlist en posición " + guildMusicManager.getTrackScheduler().getQueue().size(), null, event.getMember().getEffectiveAvatarUrl());
                embedBuilder.setTitle(track.getInfo().title);
                embedBuilder.setUrl(track.getInfo().uri);
                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessageEmbeds(EmbedUtils.createError("No se ha encontrado ninguna canción")).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessageEmbeds(EmbedUtils.createError("No se ha podido reproducir la canción")).queue();
            }
        });
    }

}
