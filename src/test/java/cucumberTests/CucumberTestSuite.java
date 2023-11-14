package cucumberTests;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "message:target/cucumber-report.ndjson", features = "src/test/resources/cucumber")
public class CucumberTestSuite {
}
