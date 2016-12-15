package watcherModel;
/**
 * Created by SiGen on 2016/12/14.
 */
public class test {
    public static void main(String[] args){
        IWatched girl = new WatchedImpl();

        IWatcher boy1 = new WathcerImpl();
        IWatcher boy2 = new WathcerImpl();
        IWatcher boy3 = new WathcerImpl();

        girl.addWatcher(boy1);
        girl.addWatcher(boy2);
        girl.addWatcher(boy3);

        try {
            girl.notifyWatcher("Hello world!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
