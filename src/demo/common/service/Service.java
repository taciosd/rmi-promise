package demo.common.service;

import tsd.rmi.promise.RmiPromise;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by taciosd on 1/28/19.
 */
public interface Service extends Remote, Serializable {

    RmiPromise<Double> executeHeavyWork() throws RemoteException;
}
