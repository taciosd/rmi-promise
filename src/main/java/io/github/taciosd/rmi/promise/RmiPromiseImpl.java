package io.github.taciosd.rmi.promise;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by taciosd on 1/28/19.
 */
public class RmiPromiseImpl<T extends Serializable, S extends Serializable> extends UnicastRemoteObject implements RmiPromise<T, S>, Serializable {

    private Phase currentPhase = Phase.READY;
    private ProgressEvent<S> progress = ProgressEvent.create(0, null);
    private boolean hasCancelOrder = false;
    private Callback callback;

    private T result;
    private Throwable throwable;

    public RmiPromiseImpl() throws RemoteException {
        super();
    }

    @Override
    public ProgressEvent<S> getProgress() throws RemoteException {
        return progress;
    }

    public void updateProgress(int value, S state) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Progress value is out of bounds [0,100].");
        }

        this.progress = ProgressEvent.create(value, state);
        publishProgress();
    }

    public void updateProgressValue(int value) {
        updateProgress(value, progress.getState());
    }

    public void updateProgressState(S state) {
        updateProgress(progress.getValue(), state);
    }

    @Override
    public boolean isFinished() throws RemoteException {
        return  currentPhase.equals(Phase.CANCELLED) ||
                currentPhase.equals(Phase.FAILED) ||
                currentPhase.equals(Phase.SUCCESS);
    }

    @Override
    public void cancel() throws RemoteException {
        hasCancelOrder = true;
    }

    @Override
    public T getResult() throws RemoteException {
        return result;
    }

    @Override
    public Throwable getThrowable() throws RemoteException {
        return throwable;
    }

    @Override
    public Phase getPhase() throws RemoteException {
        return currentPhase;
    }

    @Override
    public void registerCallback(Callback callback) throws RemoteException {
        this.callback = callback;
        if (currentPhase.isTerminal()) {
            publishPhase();
        }
    }

    public boolean hasCancelOrder() {
        return hasCancelOrder;
    }

    private void publishProgress() {
        if (callback != null) {
            try {
                callback.onProgressChanged(progress);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishPhase() {
        if (callback != null) {
            try {
                callback.onPhaseChanged(currentPhase);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish(Phase newPhase) {
        if (newPhase == null || newPhase.equals(Phase.READY) || newPhase.equals(Phase.RUNNING)) {
            throw new IllegalStateException("It's not possible to finish with this phase.");
        }

        currentPhase = newPhase;

        publishPhase();
    }

    public void finish(T result) {
        this.result = result;
        finish(Phase.SUCCESS);
    }

    public void finish(Throwable throwable) {
        this.throwable = throwable;
        finish(Phase.FAILED);
    }
}
