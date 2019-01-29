# rmi-promise
Implementation of a Promise object using Java RMI.

# Example

**Client code** 
~~~~
RmiPromise<Double> promise = service.executeHeavyWork();
.
promise.registerCallback(new DefaultCallback() {
        @Override
        public void onPhaseChanged(Phase phase) throws RemoteException {
                Platform.runLater(() -> {
                        label.setText("Phase changed! " + phase));
                        if (phase.equals(Phase.SUCCESS)) {
                                resultLabel.setText("Success!!!");
                        }
                }
        }
.
        @Override
        public void onProgressChanged(int newProgress) throws RemoteException {
                Platform.runLater(() -> progressIndicator.setProgress(newProgress));
        }
});
~~~~

**Server code**
~~~~
@Override
public RmiPromise<Double> executeHeavyWork() throws RemoteException {

        RmiPromiseImpl<Double> promise = new RmiPromiseImpl<>();

        Thread thread = new Thread(() -> {
                try {
                        double result = 0.0;
            
                        for (int i = 0; i < 100; i++) {
                
                                if (promise.hasCancelOrder()) {
                                        // Rollback, update promise and return.
                                        promise.finish(Phase.CANCELLED);
                                        return;
                                }
                                    
                                result = Util.calculateDifficultNumber(result);
                
                                promise.setProgress(i+1);
                        }

                        promise.finish(result);
                }
                catch (Exception e) {
                        e.printStackTrace();
                        promise.finish(e);
                }
        });

        thread.start();

        return promise;
}
~~~~