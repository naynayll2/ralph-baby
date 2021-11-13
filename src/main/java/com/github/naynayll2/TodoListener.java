package com.github.naynayll2;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
//import org.w3c.dom.Text;

//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.newBufferedWriter;

//import java.util.Optional;
public class TodoListener implements MessageCreateListener {
    private static final String resources = "/home/naythun/CSCI/ralph-baby/src/main/resources/";
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String[] keywords = event.getMessageContent().split(" ", 3);
        Path channelPath = Paths.get(resources + "channel_ids/" + event.getChannel().getIdAsString() + ".txt");
        List<String> current_ids = null;
        try {
            current_ids = Files.readAllLines(Paths.get(resources + "channel_ids.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (keywords[0].equals("$todo")) {
            System.out.println(keywords);
            String channel_id;
            //creates a new to-do list if one does not exist for that channel already
            if (keywords[1].equalsIgnoreCase("new")){
                try {
                    channel_id = event.getChannel().getIdAsString();
                    if (!current_ids.contains(channel_id)){
                        String thisId = event.getChannel().getIdAsString();
                        Files.writeString(Paths.get(resources + "channel_ids.txt"),thisId,StandardOpenOption.APPEND);
                        File thisTodo = new File(resources + "channel_ids/" + channel_id + ".txt");
                        if (thisTodo.createNewFile()){
                            BufferedWriter writeTodo = newBufferedWriter(thisTodo.toPath());
                            writeTodo.write("#this is how something looks like when its marked done\n");
                            writeTodo.write("1 - add a new task then mark this done with $todo 1 done!\n");
                            writeTodo.close();
                            event.getChannel().sendMessage("Created todo list for "+ event.getChannel());
                            printTodo(thisTodo.toPath(), event.getChannel());
                        }
                        else event.getChannel().sendMessage("Something went wrong when creating your todo :(");
                        event.getChannel().sendMessage("");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //checks if user wanted to add then calls the appropriate function to add the entry
            else if (keywords[1].equalsIgnoreCase("add")){
                if (addEntry(keywords[2], channelPath)){
                    try {
                        printTodo(channelPath, event.getChannel());
                    } catch (IOException e) {
                        event.getChannel().sendMessage("This channel does not have a todo list.");
                    }
                }
            }
            //deletes a to-do entry, mostly for accidental inputs
            else if (keywords[1].equalsIgnoreCase("delete")){
                try {
                    if (deleteEntry(Integer.parseInt(keywords[2].split(" ", 2)[0]) ,channelPath)){
                        printTodo(channelPath, event.getChannel());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //marks an entry as done
            else if(keywords[1].equalsIgnoreCase("done")){
                try {
                    if (markDone(Integer.parseInt(keywords[2].split(" ", 2)[0]) ,channelPath)){
                        printTodo(channelPath, event.getChannel());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends the to-do list to the channel provided. The channel id should match the id of the filepath
     * @param filePath a path object that specifies which txt holds the to-do information
     * @param channel the channel to which to sned the message
     * @throws IOException should not happen since this is only called in the case where a channel exists
     */
    private static void printTodo(Path filePath, TextChannel channel) throws IOException{

        List<String> todoLines = Files.readAllLines(filePath);
        MessageBuilder toSendTodo = new MessageBuilder();
        String builtTodo = String.join("\n", todoLines);
        toSendTodo.appendCode("python", builtTodo);
        toSendTodo.send(channel);
    }

    /**
     * adds an entry to the to-do list for the provide filePath. The to-do list Path should match up with the channel
     * id
     * @param toAdd the entry to be added to the to-do list
     * @param filePath the path of the to-do list
     * @return returns true if the entry was added succesfuly, false otherwise
     */
    public static boolean addEntry(String toAdd, Path filePath){ //TODO will break if everything in list is done. Fix that case. See todo for delete
        try {
            List<String> todoLines = Files.readAllLines(filePath);
            int index = Integer.parseInt(todoLines.get(todoLines.size() - 1).split(" ")[0]) + 1;
            BufferedWriter todo = newBufferedWriter(filePath, StandardOpenOption.APPEND);
            todo.append(index + " - " + toAdd);
            todo.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    /**
     * deletes an entry from the to-do list
     * @param entryNumber the entry which should be deleted
     * @param filePath the path of the to-do list where the deletion should take place
     * @return returns true if succesful, false otherwise (should never not be succesful in most cases)
     * @throws IOException if the file does not exist
     */
    public static boolean deleteEntry(int entryNumber, Path filePath) throws IOException{ //TODO make a function that returns line num of none done entries
        List<String> todoLines = Files.readAllLines(filePath);
        todoLines.remove(entryNumber - 1);
        for (int i = 0; i < todoLines.size(); i++ ){
            String[] line = todoLines.get(i).split(" ");
            if (Integer.parseInt(line[0]) != i + 1){
                line[0] = String.valueOf(i + 1);
            }
            String newLine = String.join(" ", line);
            todoLines.set(i + 1, newLine);
        }
        Files.write(filePath, todoLines);
        return true;
    }

    /**
     * marks an entry as done
     * @param entryNumber the non-done entry to be marked done
     * @param filePath the path to the file which contains the to-do list
     * @return true if marked done succesfully, false otherwise (should not happen that it returns false generally)
     * @throws IOException if file does not exist of course
     */
    public static boolean markDone(int entryNumber, Path filePath) throws IOException{ //TODO fix the case of when function is called when there is nothing to mark done
        List<String> todoLines = Files.readAllLines(filePath);
        int indexToSkip = 0;
        for (int i = 0; i < todoLines.size(); i++){
            if (todoLines.get(i).split(" ")[0].equals("#")){
                indexToSkip++;
            }
            else if (i == entryNumber - 1 + indexToSkip){
                todoLines.set(i, "# " + todoLines.get(entryNumber - 1 + indexToSkip));
                break;
            }
        }
        Files.write(filePath, todoLines);
        return true;
    }
//    private static boolean hasTodo(){
//
//    }
//    private static boolean addTodo(String messageContent){
//
//    }
}
