package com.lihuelmujica.musicbot.discord.commands.music;

import com.lihuelmujica.musicbot.discord.commands.Command;
import com.lihuelmujica.musicbot.discord.lavaplayer.PlayerManager;
import com.lihuelmujica.musicbot.utils.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Play implements Command {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Reproduce música xd";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Name of the song to play", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            event.replyEmbeds(EmbedUtils.createError("Tienes que estar en un canal de voz que yo para usar este comando")).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else {
            if(selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                event.replyEmbeds(EmbedUtils.createError("Tienes que estar en un canal de voz que yo para usar este comando")).queue();
                return;
            }
        }

        String name = event.getOption("name").getAsString();
        try {
            new URI(name);
        } catch (URISyntaxException e) {
            name = "ytsearch:" + name;
        }

        PlayerManager playerManager = PlayerManager.get();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        event.deferReply().queue();
        playerManager.play(event, name);
    }

}
