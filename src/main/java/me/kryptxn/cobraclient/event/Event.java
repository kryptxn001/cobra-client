package me.kryptxn.cobraclient.event;

import me.kryptxn.cobraclient.CobraClient;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;



public abstract class Event {

    private boolean cancelled;

    public Event() {
        this.cancelled = false;
    }

    public void call() {
        cancelled = false;

        CopyOnWriteArrayList<Data> dataList = CobraClient.Instance.eventManager.get(this.getClass());

        if (dataList == null) {
            return;
        }

        dataList.forEach(data -> {
            
            try {
                data.getTarget().invoke(data.getSource(), this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        });


    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


}