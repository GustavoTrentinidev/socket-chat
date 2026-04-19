package com.udesc.domain.connection;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionRegistry {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String id, Connection connection) {
        this.connections.put(id, connection);
    }

    public Connection getConnection(String id) {
        return this.connections.get(id);
    }

    public void removeConnection(String id) {
        this.connections.remove(id);
    }

    public Collection<Connection> listConnections() {
        return this.connections.values();
    }

    public Optional<Connection> getConnectionById(String id) {
        return Optional.ofNullable(this.connections.get(id));
    }
}
