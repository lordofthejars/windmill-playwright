package org.windmill;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.Tracing;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;


/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium()
              .launch(new BrowserType.LaunchOptions()
              .setHeadless(false)
              .setSlowMo(200))
              ;
            BrowserContext context = browser.newContext();

            context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true));


            // Open new page
            Page page = context.newPage();
            
            // Go to https://quinoa-wind-turbine-demo.apps.skatt.rl97.p1.openshiftapps.com/
            page.navigate("https://quinoa-wind-turbine-demo.apps.skatt.rl97.p1.openshiftapps.com/");
            
            Locator power = page.locator("#power-generator");
            int initialPower = Integer.parseInt(power.textContent());

            Response response = page.waitForResponse("**/api/power", () -> {
              page.locator("#generator").click();
            });

            Assertions.assertThat(response.status()).isEqualTo(204);

            Locator username = page.locator("#user-name");
            assertThat(username).not().isEmpty();

            // Click text=1
            Locator userteam = page.locator("#user-team");
            assertThat(userteam).containsText(Pattern.compile("\\d"));
            
            int afterPower = Integer.parseInt(power.textContent());

            Assertions.assertThat(afterPower).isGreaterThan(initialPower);


            context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("trace_generate_.zip")));

          }
    }
}
