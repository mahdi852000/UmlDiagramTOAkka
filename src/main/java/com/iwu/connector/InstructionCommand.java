package com.iwu.connector;

public record InstructionCommand(String command) implements ConnectorCommand{
    @Override
    public String toString() {
        return "InstructionCommand{" +
                "command='" + command + '\'' +
                '}';
    }
}
