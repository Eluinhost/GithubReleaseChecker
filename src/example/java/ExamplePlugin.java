/*
 * Project: githubreleasechecker
 * Class: ExamplePlugin
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
