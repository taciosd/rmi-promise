package demo.server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Arrays;

import demo.common.service.Service;
import tsd.rmi.promise.Phase;
import tsd.rmi.promise.RmiPromise;
import tsd.rmi.promise.RmiPromiseImpl;

/**
 * Created by taciosd on 1/28/19.
 */
public class ServiceImpl extends UnicastRemoteObject implements Service {

    protected ServiceImpl() throws RemoteException {
        super(1099);
    }

    @Override
    public RmiPromise<Double> executeHeavyWork() throws RemoteException {

        RmiPromiseImpl<Double> promise = new RmiPromiseImpl<>();

        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(150);
                    promise.setProgress(i+1);

                    if (i == 20 && Math.random() > 0.7) {
                        throw new SQLException("ORA-0000");
                    }

                    if (i % 5 == 0 && promise.hasCancelOrder()) {
                        // Rollback, update promise and return.
                        Thread.sleep(1000);
                        promise.finish(Phase.CANCELLED);
                        return;
                    }
                }

                promise.finish(Math.random());
            }
            catch (Exception e) {
                e.printStackTrace();
                promise.finish(e);
            }
        });

        thread.start();

        return promise;
    }

    public static void main(String[] args) {

        try {
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
            Service service = new ServiceImpl();
            registry.rebind("//localhost/RmiServer", service);
            System.err.println(Arrays.toString(registry.list()));
            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
/*
        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
