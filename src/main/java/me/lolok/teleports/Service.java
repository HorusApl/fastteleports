package me.lolok.teleports;

/**
 * Service interface represents a service class.
 */
public interface Service {

    /**
     * Called to enable the service
     */
    void enable();

    /**
     * Called to disable the service
     */
    void disable();

    /**
     * Called to reload the service
     */
    default void reload() {
        disable();
        enable();
    }
}
