package atms.app.my_application_c.Tools;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import atms.app.my_application_c.ConfigBox.OrigConfig;
import atms.app.my_application_c.MyApplication;
import atms.app.my_application_c.aidl.workClient;

/**
 * Do background work in rootservice
 * 1 handle client num change and run all callbacks
 * 2 handle root msgs and run all callbacks
 * 3 handle clents in a FIFO list
 * 4 client scheduler
 */

public class Worker {
    ///////////////////////////////////////////////////////////////////////////
    //static functions
    /**
     * static functions
     */


    //
    public static ExecutorService executorService;

    public static void setExecutorService(ExecutorService _executorService) {
        executorService = _executorService;
    }

    public static void unsetExecutorService() {
        executorService = null;
    }


    /**
     * workclient list handle code
     */

    //Number of tasks
    private static final AtomicInteger clientNumber = new AtomicInteger(0);

    public static int getClientNumber() {
        return clientNumber.get();
    }

    private static void setClientNumberNumber(int clientNum) {
        Worker.clientNumber.set(clientNum);
    }

    private static int incrementClientNumber() {
        return clientNumber.incrementAndGet();
    }

    private static int decrementClientNumber() {
        return clientNumber.decrementAndGet();
    }

    private static final List<clientNumListener> clientNumListenerList = new ArrayList<>();

    /**
     * regist . listener will be called out of main thread!!!
     *
     * @param listener
     */
    synchronized public static void registClientNumListener(clientNumListener listener) {
        clientNumListenerList.add(listener);
    }

    synchronized public static void unregistClientNumListener(clientNumListener listener) {
        clientNumListenerList.remove(listener);
    }

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int tmp = 0;
                int current = 0;
                while (!MyApplication.isAppExited) {
                    current = getWorkClientNum();
                    if (tmp != current) {
                        tmp = current;
                        Log.d(TAG.WorkerTAG, "Client num update " + current);
                        for (clientNumListener listener : clientNumListenerList) {
                            Log.d(TAG.WorkerTAG, "Client call back called");
                            listener.onNumChanged(current);
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    ////////////////////////////////////

    /**
     * handle client in LinkedBlockingDeque
     * 1 you can close a waiting client in every time
     * 2 avoid concurrent! It's necessary ,I think you are also afraid of that
     * your device reboot while it's doing a disk clone action???????
     * or it reboot before your partition is setup correct.
     * 3 limited client num,no explaination
     */


    //default limited is 5
    private static final LinkedBlockingDeque<workClient> clientLinkedBlockingDeque = new LinkedBlockingDeque<>(getClientQueLimit());

    private static workClient scheduleAclient() {
        try {
            return clientLinkedBlockingDeque.remove();
        } catch (NoSuchElementException e) {
            return null;
        }

    }


    /**
     * get client num limit
     *
     * @return
     */
    public static int getClientQueLimit() {
        //TODO get client num limit
        return 5;
    }

    public static boolean addWorkClient(workClient client) {
        boolean result = clientLinkedBlockingDeque.offer(client);
        if (result) {
            incrementClientNumber();
            Log.d(TAG.WorkerTAG, "Add client : " + client.getClientUUID());
            return true;
        } else {
            Log.d(TAG.WorkerTAG, "Add client Failed :" + client.getClientUUID());
        }
        return false;
    }

    public static LinkedBlockingDeque<workClient> getWorkClientList() {
        return clientLinkedBlockingDeque;
    }

    public static int getWorkClientNum() {
        return getClientNumber();
    }

    /**
     * dangerous ! force stop all client(root service)
     * this may break your data
     * and lead into a unknown result
     */
    public static void clearWorkClientList() {
        // stop all task
        for (workClient client : clientLinkedBlockingDeque) {
            client.terminateAllTask();
            removeClient(client);
            Log.d(TAG.WorkerTAG, "Stop " + client.getClientUUID());
        }

    }

    /**
     * the method may be invoked concurrent
     * and this method is only useful in clearWorkClientList
     *
     * @param client
     */
    synchronized public static boolean removeClient(workClient client) {
        try {
            clientLinkedBlockingDeque.remove(client);
            decrementClientNumber();
            Log.d(TAG.WorkerTAG, "Remove client :" + client.getClientUUID());
            return true;
        } catch (NoSuchElementException e) {
            Log.e(TAG.WorkerTAG, e.getMessage(), e);
        }
        return false;

    }

    /**
     * client list scheduler
     */
    static Thread clientScheduler = null;

    static {
        setupScheduler();
    }

    /**
     * if scheduler died,call this
     */
    private static void setupScheduler() {
        if (clientScheduler == null) {
            clientScheduler = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!MyApplication.isAppExited) {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        workClient client = scheduleAclient();
                        if (client != null) {
                            Log.d(TAG.WorkerTAG, "Schedule : " + client.getClientUUID());
                            boolean result = client.submitWork();
                            //FInished
                            Log.d(TAG.WorkerTAG, "[FINISH CLIENT] " + client.getClientUUID());
                            decrementClientNumber();
                        }
                    }
                }
            });
            clientScheduler.start();
        } else {

        }
    }
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    /**
     * try put a client into list
     *
     * @param origConfig
     * @param context
     * @return falied return null,success return client obj
     */
    @Nullable
    public static workClient putTaskToRootService(OrigConfig origConfig
            , Context context) {
        Log.d(TAG.WorkerTAG, " Try submit a client " + origConfig.getAttributions().get("id"));
        workClient client = new workClient(context, origConfig.getXmlString());
        boolean result = addWorkClient(client);
        if (result) {
            //I guess you may do this, I'm also
            //But I have try it,it's too silly.......
            //just put a client
            //don't care when it gets submited
            //the submit work was thrown to client scheduler
            //This makes it easy to manage clients
            //also friendly to homeFragment and me

            //client.submitWork(origConfig.getXmlString());


            Log.d(TAG.WorkerTAG, "Submit " + origConfig.getAttributions().get("id") + " successfully");
            return client;
        }

        Log.d(TAG.WorkerTAG, "Submit " + origConfig.getAttributions().get("id") + " failed.");

        return null;
    }


}
