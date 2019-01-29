package tsd.rmi.promise;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by taciosd on 1/28/19.
 */
public abstract class DefaultCallback extends UnicastRemoteObject implements Callback {

    protected DefaultCallback() throws RemoteException {
        super();
    }

    @Override
    public abstract void onPhaseChanged(Phase phase) throws RemoteException;

    @Override
    public abstract void onProgressChanged(int newProgress) throws RemoteException;
}
