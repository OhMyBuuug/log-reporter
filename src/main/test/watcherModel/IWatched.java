package watcherModel;
/**
 * Created by SiGen on 2016/12/14.
 */
public interface IWatched {
    public void addWatcher(IWatcher iWatcher);
    public void removeWatcher(IWatcher iWatcher);
    public void notifyWatcher(String message) throws InterruptedException;
}
