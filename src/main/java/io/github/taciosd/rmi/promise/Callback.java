package io.github.taciosd.rmi.promise;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by taciosd on 1/28/19.
 */
public interface Callback extends Remote {

    void onPhaseChanged(Phase phase) throws RemoteException;

    void onProgressChanged(ProgressEvent event) throws RemoteException;
}
