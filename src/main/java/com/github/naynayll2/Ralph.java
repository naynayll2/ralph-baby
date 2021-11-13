package com.github.naynayll2;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Ralph {
    private static String resources = "/home/naythun/CSCI/ralph-baby/src/main/resources/";
    public static void main(String[] args) throws IOException {
        // Log the bot in

        Scanner input = new Scanner(new File(resources + "token.txt"));
        String token = input.nextLine();
//        System.out.println(token);
        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .setAllNonPrivilegedIntents()
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


        api.addMessageCreateListener(new TodoListener());
    }

}
