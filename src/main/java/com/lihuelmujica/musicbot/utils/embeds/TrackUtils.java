package com.lihuelmujica.musicbot.utils.embeds;

import com.github.topislavalinkplugins.topissourcemanagers.ISRCAudioTrack;
import com.lihuelmujica.musicbot.discord.lavaplayer.TrackScheduler;
import com.lihuelmujica.musicbot.utils.SecurityUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TrackUtils {

    public static @NotNull String formatTrackLength(long trackLength) {
        long hours = TimeUnit.MILLISECONDS.toHours(trackLength);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(trackLength) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(trackLength));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(trackLength) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackLength));
        String time = "";
        if (hours > 0) time += hours + ":";
        if (minutes < 10 && hours > 0) time += "0";
        time += minutes + ":";
        if (seconds < 10) time += "0";
        time += seconds;
        return time;
    }

    public static MessageEmbed displayTrack(AudioTrack track, TrackScheduler scheduler, SlashCommandInteractionEvent event) {
        String duration = formatTrackLength(track.getInfo().length);
        String repeat = (scheduler.isRepeat()) ? "Activado" : "Desactivado";
        String userMention = track.getUserData(String.class);
        String lihuelMention = event.getGuild().getMemberById("353729891872669696").getEffectiveName();
        String lihuelIcon = event.getGuild().getMemberById("353729891872669696").getUser().getEffectiveAvatarUrl();
        return new EmbedBuilder()
                .setTitle("Reproduciendo")
                .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")")
                .addField("Duración", "`"+duration+"`", true)
                .addField("Tamaño de la cola", "`"+(scheduler.getQueue().size())+"`", true)
                .addField("Volumen", "`"+scheduler.getPlayer().getVolume()+"%`", true)
                .addField("Pedida por", userMention, true)
                .addField("Link", "[`enlace`]("+track.getInfo().uri+")", true)
                .addField("Bucle: ", "`"+repeat+"`", true)
                .setColor(Color.blue)
                .setThumbnail(getThumbnail(track))
                .setFooter("by " + lihuelMention + " md para informar de bugs y suggerencias :)", lihuelIcon)
                .build();
    }

    private static String getThumbnail(AudioTrack track) {
        String domain = SecurityUtils.getDomain(track.getInfo().uri);
        if (    domain.equalsIgnoreCase("spotify") || domain.equalsIgnoreCase("apple")) {
            return ((ISRCAudioTrack) track).getArtworkURL();
        }
        return String.format("https://img.youtube.com/vi/%s/0.jpg", track.getIdentifier());
    }

}
