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

import com.github.zafarkhaja.semver.Version;
import gg.uhc.githubreleasechecker.ReleaseChecker;
import gg.uhc.githubreleasechecker.UpdateResponse;
import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ExamplePlugin extends JavaPlugin {
    public void onEnable() {
        final LatestReleaseQueryer queryer = new LatestReleaseQueryer("Eluinhost", "UHC");
        final ReleaseChecker checker = new ReleaseChecker(this, queryer, true);

        getServer()
            .getScheduler()
            .runTaskTimerAsynchronously(this, new UpdateLogger(checker, this), 0, 60 * 60 * 20);
    }
}

class UpdateLogger implements Runnable, Listener {
    protected static final String UPDATE_FORMAT = "An update is available (%s -> %s).";

    protected final ReleaseChecker checker;
    protected final Plugin plugin;

    protected Version latestUpdate;
    protected BaseComponent chatMessage;

    UpdateLogger(ReleaseChecker checker, Plugin plugin) {
        this.checker = checker;
        this.plugin = plugin;
    }

    @EventHandler public void on(PlayerJoinEvent event) {
        if (event.getPlayer().isOp() && chatMessage != null) {
            event.getPlayer().spigot().sendMessage(chatMessage);
        }
    }

    @Override
    public void run() {
        try {
            final UpdateResponse response = checker.checkForUpdate();

            if (response.hasUpdate()) {
                final Release update = response.getUpdateDetails();

                if (!update.getVersion().equals(latestUpdate)) {
                    latestUpdate = update.getVersion();

                    final String base = String.format(
                        UPDATE_FORMAT,
                        response.getInstalled().toString(),
                        update.getVersion().toString()
                    );

                    plugin.getLogger().info(base + " More info: " + update.getUrl());

                    chatMessage = new TextComponent(base);
                    chatMessage.setColor(ChatColor.AQUA);

                    TextComponent info = new TextComponent(" Click me for info");
                    info.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.getUrl()));
                    info.setUnderlined(true);
                    chatMessage.addExtra(info);

                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        if (p.isOp()) {
                            p.spigot().sendMessage(chatMessage);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}