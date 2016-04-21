package gg.uhc.githubreleasechecker;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;
import org.bukkit.plugin.Plugin;

public class ReleaseChecker {
    protected final Version installed;
    protected final boolean allowPrerelease;
    protected final Plugin plugin;
    protected final LatestReleaseQueryer queryer;

    protected final Predicate<Release> IS_NEWER = new Predicate<Release>() {
        @Override
        public boolean apply(Release input) {
            return input != null
                    && input.getVersion() != null
                    && (allowPrerelease || !input.isPrerelease())
                    && input.getVersion().greaterThan(installed);
        }
    };

    /**
     * Creates a new release checker for checking for updates
     *
     * @param plugin the plugin to check for
     * @param queryer use to query for releases to check against
     * @param allowPrerelease if set to true will also check for prerelease versions
     *
     * @throws IllegalArgumentException if provided plugin does not have a version string it it's plugin.yml
     * @throws com.github.zafarkhaja.semver.ParseException if provided version number is not valid semver
     */
    public ReleaseChecker(Plugin plugin, LatestReleaseQueryer queryer, boolean allowPrerelease) {
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(queryer);

        this.allowPrerelease = allowPrerelease;
        this.plugin = plugin;
        this.queryer = queryer;

        final String versionString = plugin.getDescription().getVersion();

        if (versionString == null) throw new IllegalArgumentException("Provided plugin does not have a version field");

        installed = Version.valueOf(versionString);
    }

    public Version getInstalledVersion() {
        return installed;
    }

    public void checkForUpdate(final FutureCallback<UpdateResponse> callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(new UpdateResponse(
                        installed,
                        Iterables.tryFind(
                            ImmutableList.copyOf(queryer.queryReleases()),
                            IS_NEWER
                        ).orNull()
                    ));
                } catch (Exception ex) {
                    callback.onFailure(ex);
                }
            }
        });
    }
}
