package EALiodufiowAMS2.helpers;

import javax.swing.*;
import java.util.function.Supplier;

public class AsyncSwingExecutor {

    public <T> void loadAsync(Supplier<T> supplier,
                              java.util.function.Consumer<T> onLoaded,
                              java.util.function.Consumer<Exception> onError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return supplier.get();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    onLoaded.accept(result);
                } catch (Exception e) {
                    onError.accept(e);
                }
            }
        }.execute();
    }
}
