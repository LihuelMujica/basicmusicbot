package com.lihuelmujica.musicbot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Discord {

    @Value("${discord.token}")
    private String BOT_TOKEN;

    @Bean
    public JDA jda() {
        JDA jda = JDABuilder.createDefault(BOT_TOKEN).build();
        Presence presence = jda.getPresence();
        presence.setStatus(OnlineStatus.ONLINE);
        presence.setActivity(Activity.of(Activity.ActivityType.LISTENING, "a tu corazón"));
        return jda;
    }


}
