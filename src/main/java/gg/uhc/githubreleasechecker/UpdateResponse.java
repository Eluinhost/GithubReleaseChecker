package gg.uhc.githubreleasechecker;

import com.github.zafarkhaja.semver.Version;
import gg.uhc.githubreleasechecker.data.Release;

public class UpdateResponse {
    protected final Version installed;
    protected final Release updateDetails;
    protected final boolean hasUpdate;

    public UpdateResponse(Version installed) {
        this(installed, null);
    }

    UpdateResponse(Version installed, Release update) {
        this.installed = installed;
        this.updateDetails = update;
        this.hasUpdate = updateDetails != null;
    }

    public Version getInstalled() {
        return installed;
    }

    public Release getUpdateDetails() {
        return updateDetails;
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }
}
