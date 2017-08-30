/*
 * Project: githubreleasechecker
 * Class: LatestReleaseQueryer
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

package gg.uhc.githubreleasechecker.deserialization;

import gg.uhc.githubreleasechecker.data.Release;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class LatestReleaseQueryer {
    protected static final String URL_FORMAT = "https://api.github.com/repos/%s/%s/releases";

    protected final URL url;
    protected final Gson gson;

    public LatestReleaseQueryer(String author, String repo) {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Version.class, new VersionDeserializer());
        gson = builder.create();

        try {
            this.url = new URL(String.format(URL_FORMAT, author, repo));
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid author/repository names provided", ex);
        }
    }

    public Release[] queryReleases() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder sb = new StringBuilder();

        String temp;
        while ((temp = in.readLine()) != null) {
            sb.append(temp);
        }

        in.close();

        return gson.fromJson(sb.toString(), Release[].class);
    }
}
