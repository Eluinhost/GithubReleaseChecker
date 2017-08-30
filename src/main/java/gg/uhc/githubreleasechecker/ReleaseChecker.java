/*
 * Project: githubreleasechecker
 * Class: ReleaseChecker
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.githubreleasechecker;

import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;

import com.github.zafarkhaja.semver.Version;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class ReleaseChecker {
    protected final Version installed;
    protected final boolean allowPrerelease;
    protected final Plugin plugin;
    protected final LatestReleaseQueryer queryer;

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

    public UpdateResponse checkForUpdate() throws IOException {
        Release[] releases = queryer.queryReleases();
        Release potentialUpdate = null;
        Version updateVersion = installed;

        for (Release release : releases) {
            if (release == null || (!allowPrerelease && release.isPrerelease()))
                continue;

            Version version = release.getVersion();

            if (version == null)
                continue;

            if (version.lessThanOrEqualTo(updateVersion))
                continue;

            potentialUpdate = release;
            updateVersion = version;
        }

        return new UpdateResponse(
            installed,
            potentialUpdate
        );
    }
}
