package io.github.taciosd.rmi.promise.demo.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import io.github.taciosd.rmi.promise.ProgressEvent;
import io.github.taciosd.rmi.promise.demo.common.service.ExecutionFlow;
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

    private RmiPromise<Double, ExecutionFlow> promise;

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
                statusLabel.setText("Cancelling...");
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
        statusLabel.setText("Executing...");

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

                            statusLabel.setText("Success! (result = " + result + ")");
                        }
                        else if (phase.equals(Phase.CANCELLED)) {
                            statusLabel.setText("Cancelled!");
                        }
                        else if (phase.equals(Phase.FAILED)) {
                            Throwable throwable = null;
                            try {
                                throwable = promise.getThrowable();
                            }
                            catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            statusLabel.setText("Error! (" + throwable.getMessage() + ")");
                        }
                    });
                }

                @Override
                public void onProgressChanged(ProgressEvent event) throws RemoteException {
                    Platform.runLater(() -> {
                        progressIndicator.setProgress((double) event.getValue() / 100);
                        statusLabel.setText("Current state of the work: " + event.getState());
                    });
                }
            });
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
