package main;

import java.util.UUID;

/**
 * Creates an unique-ID for a Client connected with the Server.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface IdGenerator {

    /**
     * Generate an unique-ID.
     *
     * @return the ID generated.
     */
    public UUID generateID();

}
