import gg.uhc.githubreleasechecker.data.Asset;
import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;
import org.junit.Test;

import java.io.IOException;

public class LatestReleaseQueryerTest {

    @Test
    public void test() throws IOException {
        LatestReleaseQueryer r = new LatestReleaseQueryer("Eluinhost", "UHC");
        Release[] rel = r.queryReleases();

        for (Release p : rel) {
            System.out.println(p.getUrl() + " " + p.getVersion() + " " + p.isPrerelease());

            for (Asset asset : p.getAssets()) {
                System.out.println(asset.getName() + " " + asset.getDownloadUrl());
            }
        }
    }
}
