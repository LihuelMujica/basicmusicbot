package com.lihuelmujica.musicbot.discord.lavaplayer;

import com.lihuelmujica.musicbot.utils.embeds.TrackUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private AudioTrack currentTrack;
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private boolean isRepeat = false;

    private SlashCommandInteractionEvent event;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(isRepeat) {
            player.startTrack(track.makeClone(), false);
        } else {
            player.startTrack(queue.poll(), false);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        event.getChannel().sendMessageEmbeds(TrackUtils.displayTrack(track, this, event)).queue();
        currentTrack = track;
        super.onTrackStart(player, track);
    }

    public void queue(AudioTrack track, SlashCommandInteractionEvent event) {
        this.event = event;
        track.setUserData(event.getMember().getAsMention());
        System.out.println("Track added to queue");
        System.out.println(track.getUserData().toString());
        if(!audioPlayer.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public AudioPlayer getPlayer() {
        return audioPlayer;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public AudioTrack getCurrentTrack() {
        return currentTrack;
    }


}
