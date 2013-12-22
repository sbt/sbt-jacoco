import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.io.File;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.btr.proxy.selector.pac.PacProxySelector;

import play.Logger;
import play.mvc.Content;
import play.test.FakeApplication;
import play.test.Helpers;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    public static File relativeToBaseDir(String path) {
        System.out.println("Absolute current working directory path: " + new File(".").getAbsolutePath());
        return new File(new File("."), path);
    }

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }

    @Test
    public void workingDirectoryShouldBeConstant() {
        FakeApplication app = new FakeApplication(relativeToBaseDir("."),
                Helpers.class.getClassLoader(), new HashMap<String, String>(), new ArrayList<String>(), null);
        
        File testData = app.getWrappedApplication().getFile("./test/resources/test-data.txt");
        
        assertThat(testData).isFile();
    }

    @Test
    public void unmanagedDependenciesCanBeFoundOnClassPath() {
        FakeApplication app = new FakeApplication(relativeToBaseDir("."),
                Helpers.class.getClassLoader(), new HashMap<String, String>(), new ArrayList<String>(), null);
        
        ProxySelector ps = new PacProxySelector(null);
        
        assertThat(ps instanceof ProxySelector);
    }
    
}
