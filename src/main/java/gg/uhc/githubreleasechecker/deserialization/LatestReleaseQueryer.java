package gg.uhc.githubreleasechecker.deserialization;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.uhc.githubreleasechecker.data.Release;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LatestReleaseQueryer {
    protected static final String URL_FORMAT = "https://api.github.com/repos/%s/%s/releases";

    protected final URL url;
    protected final Gson gson;

    public LatestReleaseQueryer(String author, String repo) {
        Preconditions.checkNotNull(author);
        Preconditions.checkNotNull(repo);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Version.class, new VersionDeserializer());
        gson = builder.create();

        try {
            this.url = new URL(String.format(URL_FORMAT, author, repo));
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid author/repository names provided", ex);
        }
    }

    public Release[] queryReleases() throws IOException {
        String rawJson = Resources.toString(url, Charsets.UTF_8);

        return gson.fromJson(rawJson, Release[].class);
    }
}
