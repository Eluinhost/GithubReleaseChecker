import com.google.common.util.concurrent.FutureCallback;
import gg.uhc.githubreleasechecker.ReleaseChecker;
import gg.uhc.githubreleasechecker.UpdateResponse;
import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    protected static final String UPDATE_FORMAT = "An update is available (%s -> %s). Download here: %s";

    public void onEnable() {
        LatestReleaseQueryer queryer = new LatestReleaseQueryer("Eluinhost", "UHC");
        final ReleaseChecker checker = new ReleaseChecker(this, queryer, true);

        final UpdateCallback callback = new UpdateCallback();

        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                checker.checkForUpdate(callback);
            }
        }, 0, 60 * 60 * 20);
    }

    protected class UpdateCallback implements FutureCallback<UpdateResponse> {
        @Override
        public void onSuccess(UpdateResponse response) {
            if (response.hasUpdate()) {
                Release update = response.getUpdateDetails();
                getLogger().info(String.format(
                    UPDATE_FORMAT,
                    response.getInstalled(),
                    update.getVersion(),
                    update.getUrl())
                );
            }
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
        }
    }
}
