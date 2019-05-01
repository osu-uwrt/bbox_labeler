import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;

/**
 * Model class.
 *
 * @author Derek Opdycke
 */
public final class SessionTypeModel1 implements SessionTypeModel {

    /**
     * Model variables.
     */
    private BoxAPIConnection api;

    /**
     * Default constructor.
     */
    public SessionTypeModel1(BoxAPIConnection api) {
        /*
         * Initialize model
         */
        this.api = api;
    }

    @Override
    public String username() {
        return BoxUser.getCurrentUser(this.api).getInfo().getName();
    }

    @Override
    public BoxAPIConnection api() {
        return this.api;
    }
}
