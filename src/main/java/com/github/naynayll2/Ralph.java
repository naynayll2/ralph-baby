package com.github.naynayll2;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;

import java.io.File;
import java.util.Scanner;

public class Ralph {

    public static void main(String[] args) {
        // Log the bot in
        Scanner input = new Scanner("token.txt");
        String token = input.nextLine();
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login().join();
        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });
        // Add a listener which responds to "!ralph" with an img of ralph and adds 1 to the ralph counter
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ralph")){
                new MessageBuilder()
                        .addAttachment(new File("/home/naythun/Proj/img/ralph.png"))
                        .send(event.getChannel());
            }

        });
    }

}
