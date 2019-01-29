package io.github.taciosd.rmi.promise;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by taciosd on 1/28/19.
 */
public interface RmiPromise<T extends Serializable> extends Remote, Serializable {

    int getProgress() throws RemoteException;

    boolean isFinished()  throws RemoteException;

    void cancel() throws RemoteException;

    T getResult() throws RemoteException;

    Throwable getThrowable() throws RemoteException;

    Phase getPhase() throws RemoteException;

    void registerCallback(Callback callback) throws RemoteException;
}
