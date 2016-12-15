package watcherModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SiGen on 2016/12/14.
 */
public class WatchedImpl implements IWatched {
    // 存放watcher对象
    private List<IWatcher> iWatcherList = new ArrayList<IWatcher>();

    // 增加一个watcher对象
    @Override
    public void addWatcher(IWatcher iWatcher) {
        iWatcherList.add(iWatcher);
    }

    // 移除一个watcher对象
    @Override
    public void removeWatcher(IWatcher iWatcher) {
        iWatcherList.remove(iWatcher);
    }

    // 通知watcher对象
    @Override
    public void notifyWatcher(String message) throws InterruptedException {
        // 遍历并通知watcher
        iWatcherList.forEach(
                singleWatcher -> singleWatcher.update(message)
        );
    }
}
