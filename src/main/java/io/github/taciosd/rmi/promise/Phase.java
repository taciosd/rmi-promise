package io.github.taciosd.rmi.promise;

/**
 * Created by taciosd on 1/28/19.
 */
public enum Phase {
    READY,
    RUNNING,
    CANCELLED,
    FAILED,
    SUCCESS;

    public boolean isTerminal() {
        return  this.equals(CANCELLED) ||
                this.equals(FAILED) ||
                this.equals(SUCCESS);
    }
}
