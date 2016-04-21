package gg.uhc.githubreleasechecker.data;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.annotations.SerializedName;

public class Release {
    @SerializedName("html_url") private String url;
    @SerializedName("tag_name") private Version version;
    private boolean prerelease;
    private Asset[] assets;

    public Release() {}

    public String getUrl() {
        return url;
    }

    public Version getVersion() {
        return version;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public Asset[] getAssets() {
        return assets;
    }
}
