package com.udesc.domain;

public enum Command {
    LIST_USERS("/users"),
    SEND_MESSAGE("/send message "),
    SEND_FILE("/send file "),
    QUIT("/quit");

    public final String value;

    Command(String value) {
        this.value = value;
    }

    public static boolean isValidCommand(String message) {
        boolean valid = false;

        for (Command command : Command.values()) {
            if (message.startsWith(command.value)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static Command getCommand(String message) {
        for (Command command : Command.values()) {
            if (message.startsWith(command.value)) {
                return command;
            }
        }
        
        return null;
    }
}
