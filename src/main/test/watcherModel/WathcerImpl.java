package watcherModel;
/**
 * Created by SiGen on 2016/12/14.
 */
public class WathcerImpl implements IWatcher{

    @Override
    public void update(String message) {
        System.out.println(message);
    }
}
