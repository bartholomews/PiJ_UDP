package main;

import java.util.UUID;

/**
 * Creates an unique-ID for a Client connected with the Server.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class IdGeneratorImpl implements IdGenerator {

    /**
     * {@inheritDoc}
     *
     * @return the ID generated.
     */
    @Override
    public UUID generateID() {
        return UUID.randomUUID();
    }

}
