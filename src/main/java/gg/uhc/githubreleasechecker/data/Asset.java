package gg.uhc.githubreleasechecker.data;

import com.google.gson.annotations.SerializedName;

public class Asset {
    @SerializedName("browser_download_url") private String downloadUrl;
    private String name;

    private Asset() {}

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getName() {
        return name;
    }
}
