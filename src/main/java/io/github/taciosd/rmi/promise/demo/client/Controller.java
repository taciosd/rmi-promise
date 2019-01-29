package io.github.taciosd.rmi.promise.demo.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import io.github.taciosd.rmi.promise.demo.common.service.Service;
import io.github.taciosd.rmi.promise.DefaultCallback;
import io.github.taciosd.rmi.promise.Phase;
import io.github.taciosd.rmi.promise.RmiPromise;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;


public class Controller {

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button toggleBtn;

    @FXML
    private Label statusLabel;

    private RmiPromise<Double> promise;

    @FXML
    void initialize() {
        progressIndicator.setProgress(0.0);
        progressIndicator.setDisable(true);
    }

    public void onToggleBtnClick(ActionEvent actionEvent) {
        if (toggleBtn.getText().equals("CANCEL")) {
            cancelOperation();
        }
        else {
            executeOperation();
        }
    }

    private void cancelOperation() {
        if (promise != null) {
            try {
                promise.cancel();
                toggleBtn.setDisable(true);
                statusLabel.setText("Cancelando...");
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeOperation() {
        Service service = null;

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (Service) registry.lookup("//localhost/RmiServer");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            this.promise = service.executeHeavyWork();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        toggleBtn.setText("CANCEL");
        progressIndicator.setDisable(false);
        statusLabel.setText("Executando...");

        try {
            promise.registerCallback(new DefaultCallback() {
                @Override
                public void onPhaseChanged(Phase phase) throws RemoteException {
                    Platform.runLater(() -> {
                        if (phase.isTerminal()) {
                            toggleBtn.setText("START");
                            toggleBtn.setDisable(false);
                            progressIndicator.setDisable(true);
                        }

                        if (phase.equals(Phase.SUCCESS)) {
                            Double result = null;
                            try {
                                result = promise.getResult();
                            }
                            catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            statusLabel.setText("Sucesso! (result = " + result + ")");
                        }
                        else if (phase.equals(Phase.CANCELLED)) {
                            statusLabel.setText("Cancelado!");
                        }
                        else if (phase.equals(Phase.FAILED)) {
                            Throwable throwable = null;
                            try {
                                throwable = promise.getThrowable();
                            }
                            catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            statusLabel.setText("Falha! (" + throwable.getMessage() + ")");
                        }
                    });
                }

                @Override
                public void onProgressChanged(int newProgress) throws RemoteException {
                    Platform.runLater(() -> progressIndicator.setProgress((double) newProgress / 100));
                }
            });
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }


/*
        new Thread(() -> {
            try {
                while (!promise.isFinished()) {
                    Thread.sleep(200);
                    double progress = (double) promise.getProgress() / 100;

                    Platform.runLater(() -> progressIndicator.setProgress(progress));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            Platform.runLater(() -> {
                toggleBtn.setText("START");
                toggleBtn.setDisable(false);
                progressIndicator.setProgress(0.0);
                progressIndicator.setDisable(true);
            });
        }).start();
*/
    }
}
