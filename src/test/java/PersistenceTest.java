import cz.cvut.kbss.sempipes.config.PersistenceConfig;
import cz.cvut.kbss.sempipes.config.RestConfig;
import cz.cvut.kbss.sempipes.persistence.dao.EdgeDao;
import cz.cvut.kbss.sempipes.persistence.dao.GraphDao;
import cz.cvut.kbss.sempipes.persistence.dao.NodeDao;
import cz.cvut.kbss.sempipes.model.graph.Edge;
import cz.cvut.kbss.sempipes.model.graph.Graph;
import cz.cvut.kbss.sempipes.model.graph.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import scala.Some;

import java.net.URI;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 03.12.16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestConfig.class, PersistenceConfig.class})
@WebAppConfiguration
public class PersistenceTest {

    private static byte nodeCount = 0;

    @Autowired
    private GraphDao graphDao;

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private EdgeDao edgeDao;

    @Test
    public void edgePersistenceTest() throws Exception {
        URI uri = new URI("https://uri");
        edgeDao.delete(uri);
        assertEquals(scala.None$.MODULE$, edgeDao.get(uri));
        final HashSet<String> types = new HashSet<>();
        types.add("https://type/1");
        types.add("https://type/2");
        types.add("https://type/3");
        types.add("https://type/4");
        final Node n = new Node(new URI("https://uri" + nodeCount++), "Label", 1, 2, types, new java.util.HashSet<String>(), new java.util.HashSet<String>());
        final Node n1 = new Node(new URI("https://uri" + nodeCount++), "Label", 1, 2, types, new java.util.HashSet<String>(), new java.util.HashSet<String>());
        Edge e = new Edge(uri, n, n1);
        assertEquals(new Some<>(e), edgeDao.add(e));
        assertEquals(new Some<>(e), edgeDao.get(new URI("https://uri")));
        assertEquals(new Some<>(uri), edgeDao.delete(uri));
        assertEquals(scala.None$.MODULE$, edgeDao.get(uri));
    }

    @Test
    public void nodePersistenceTest() throws Exception {
        URI uri = new URI("https://uri" + nodeCount);
        nodeDao.delete(uri);
        assertEquals(scala.None$.MODULE$, nodeDao.get(uri));
        Node n = persistNode();
        assertNotNull(nodeDao.get(uri));
        assertEquals(new Some<>(n), nodeDao.get(uri));
        assertEquals(new Some<>(uri), nodeDao.delete(uri));
        assertEquals(scala.None$.MODULE$, nodeDao.get(uri));
    }

    @Test
    public void graphPersistenceTest() throws Exception {
        final URI uri = new URI("https://uri");
        graphDao.delete(uri);
        assertEquals(scala.None$.MODULE$, graphDao.get(uri));
        final HashSet<String> types = new HashSet<>();
        types.add("https://type/1");
        types.add("https://type/2");
        types.add("https://type/3");
        final Node n = new Node(new URI("https://uri" + nodeCount), "Label", 1, 2, types, new java.util.HashSet<String>(), new java.util.HashSet<String>());
        final Edge e = new Edge(new URI("https://edge"), n, n);
        final HashSet<Node> nodes = new HashSet<>();
        nodes.add(n);
        final HashSet<Edge> edges = new HashSet<>();
        edges.add(e);
        final Graph g = new Graph(uri, "Graph", nodes, edges);
        assertEquals(new Some<>(g), graphDao.add(g));
        assertNotNull(graphDao.get(uri));
        assertEquals(new Some<>(g), graphDao.get(uri));
        assertEquals(new Some<>(uri), graphDao.delete(uri));
        assertEquals(scala.None$.MODULE$, graphDao.get(uri));
    }

    @Test
    public void getAllGraphsTest() throws Exception {
        final URI uri1 = new URI("https://uri3");
        final URI uri2 = new URI("https://uri4");
        graphDao.delete(uri1);
        graphDao.delete(uri2);
        assertEquals(scala.None$.MODULE$, graphDao.get(uri1));
        assertEquals(scala.None$.MODULE$, graphDao.get(uri2));
        final HashSet<String> types = new HashSet<>();
        types.add("https://type/1");
        types.add("https://type/2");
        types.add("https://type/3");
        final Node n = new Node(new URI("https://uri" + nodeCount), "Label", 1, 2, types, new java.util.HashSet<String>(), new java.util.HashSet<String>());
        final Edge e = new Edge(new URI("https://edge"), n, n);
        final HashSet<Node> nodes = new HashSet<>();
        nodes.add(n);
        final HashSet<Edge> edges = new HashSet<>();
        edges.add(e);
        final Graph g1 = new Graph(uri1, "Graph", nodes, edges);
        final Graph g2 = new Graph(uri2, "Graph", nodes, edges);
        assertEquals(new Some<>(g1), graphDao.add(g1));
        assertNotNull(graphDao.get(uri1));
        assertEquals(new Some<>(g2), graphDao.add(g2));
        assertNotNull(graphDao.get(uri2));
        assertEquals(2, graphDao.getAll().get().size());
        assertEquals(g1, graphDao.getAll().get().head());
        assertEquals(g1, graphDao.getAll().get().drop(1).head());
        assertEquals(new Some<>(uri1), graphDao.delete(uri1));
        assertEquals(scala.None$.MODULE$, graphDao.get(uri1));
        assertEquals(1, graphDao.getAll().get().size());
        assertEquals(g2, graphDao.getAll().get().head());
        assertEquals(new Some<>(uri2), graphDao.delete(uri2));
        assertEquals(scala.None$.MODULE$, graphDao.get(uri2));
    }

    private Node persistNode() throws Exception {
        final HashSet<String> types = new HashSet<>();
        types.add("https://type/1");
        types.add("https://type/2");
        types.add("https://type/3");
        types.add("https://type/4");
        final Node n = new Node(new URI("https://uri" + nodeCount++), "Label", 1, 2, types, new java.util.HashSet<String>(), new java.util.HashSet<String>());
        nodeDao.add(n);
        return n;
    }
}
