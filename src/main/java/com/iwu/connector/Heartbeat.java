package com.iwu.connector;

public record Heartbeat(String source) implements ConnectorCommand {}

