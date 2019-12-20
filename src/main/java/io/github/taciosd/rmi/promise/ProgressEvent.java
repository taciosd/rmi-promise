package io.github.taciosd.rmi.promise;

import java.io.Serializable;

/**
 * Represents an event of progress in the task execution.
 * The S object can be used to provide information about the state of the task execution.
 *
 * Created by taciosd on 12/20/2019.
 */
public class ProgressEvent<S> implements Serializable {

    private int value;
    private S state;

    public static <S> ProgressEvent<S> create(int value, S state) {
        return new ProgressEvent<>(value, state);
    }

    private ProgressEvent(int value, S state) {
        this.value = value;
        this.state = state;
    }

    public ProgressEvent(ProgressEvent<S> event) {
        this(event.value, event.state);
    }

    public int getValue() {
        return value;
    }

    public S getState() {
        return state;
    }
}
