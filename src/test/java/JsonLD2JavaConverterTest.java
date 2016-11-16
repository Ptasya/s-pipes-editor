import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import cz.cvut.kbss.jsonld.deserialization.JsonLdDeserializer;
import cz.cvut.kbss.sempipes.model.TestNode;
import cz.cvut.kbss.sempipes.model.graph.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Miroslav Blasko on 10.11.16.
 */
public class JsonLD2JavaConverterTest {


    private JsonLdDeserializer deserializer;

    @Before
    public void setUp() {
        this.deserializer = JsonLdDeserializer.createExpandedDeserializer();
    }

    @Test
    public void testDeserializeNodeWithoutType() throws Exception {
        final Object input = readAndExpand("createNodeSample.json");
        final TestNode result = deserializer.deserialize(input, TestNode.class);
        TestNode control = new TestNode();
        control.setUri(new URI("/nodes/12034"));
        control.setLabel("create name");
        control.setX(1.0);
        control.setY(2.2);
        Set<String> types = new HashSet<>();
        types.add("a");
        types.add("b");
        Set<String> in = new HashSet<>();
        in.add("a");
        in.add("b");
        Set<String> out = new HashSet<>();
        out.add("c");
        out.add("d");
        control.setInParameters(in);
        control.setOutParameters(out);
        System.err.println("Test node " + result);
        System.err.println("Test node " + control);

        assertTrue(result.equals(control));
    }


    private Object readAndExpand(String fileName) throws Exception {
        final InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        final Object jsonObject = JsonUtils.fromInputStream(is);
        return JsonLdProcessor.expand(jsonObject);
    }

    @Test
    public void testDeserializeNode() throws Exception {
        final Object input = readAndExpand("createNodeSample.json");
        final Node result = deserializer.deserialize(input, Node.class);
        assertTrue(result.getNodeTypes() != null);
//        verifyUserAttributes(USERS.get(HALSEY_URI), result);
//        assertNotNull(result.getEmployer());
//        verifyOrganizationAttributes(result.getEmployer());
        System.out.println("Node " + result);
    }

}
