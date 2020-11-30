package xyz.larklab.homework.ai.service;

import guru.nidi.graphviz.model.MutableNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.larklab.homework.ai.algorithm.BoxSearchAlgorithm;
import xyz.larklab.homework.ai.entity.Box;
import xyz.larklab.homework.ai.utils.BoxHelper;

import static org.assertj.core.api.Assertions.assertThat;

class GraphTest {
    //private static final String TEST_ROOT_PATH = "test-output";
    private SearchResultToGraphvizGraphService searchResultToGraphvizGraphService;
    //private BoxTreeToImageService boxTreeToImageService;

    @BeforeEach
    void setUp() {
        searchResultToGraphvizGraphService = new SearchResultToGraphvizGraphService();
        //boxTreeToImageService = new BoxTreeToImageService(boxTreeToGraphvizGraphService);
        //boxTreeToImageService.setRootPath(TEST_ROOT_PATH);
    }

    @Test
    void simpleGraphviz() {
        // Prepare some data.
        var root = BoxHelper.getExampleRootBox();
        var children = BoxHelper.getExampleBoxChildren();
        // Only need two children boxes for simplicity.
        children = new Box[]{children[0], children[1]};
        root.getChildren().clear();
        root.addChildren(children);
        // The index here is fake search index, which has not side affect.
        var boxTree = new Box.BoxTree(root);

        // Prepare result set.
        var rootResultName = "<table>" +
                "<tr><td colspan=\"2\">IDX</td><td>1</td></tr>" +
                "<tr><td>1</td><td>2</td><td>3</td></tr>" +
                "<tr><td>4</td><td>0</td><td>5</td></tr>" +
                "<tr><td>6</td><td>7</td><td>8</td></tr></table>";
        var childrenResultNames = new String[]{
                "<table>" +
                        "<tr><td colspan=\"2\">IDX</td><td>2</td></tr>" +
                        "<tr><td>1</td><td>0</td><td>3</td></tr>" +
                        "<tr><td>4</td><td>2</td><td>5</td></tr>" +
                        "<tr><td>6</td><td>7</td><td>8</td></tr></table>",
                "<table>" +
                        "<tr><td colspan=\"2\">IDX</td><td>3</td></tr>" +
                        "<tr><td>1</td><td>2</td><td>3</td></tr>" +
                        "<tr><td>4</td><td>7</td><td>5</td></tr>" +
                        "<tr><td>6</td><td>0</td><td>8</td></tr></table>"
        };
        var resultNames = new String[]{rootResultName, childrenResultNames[0], childrenResultNames[1]};

        // Graph label assertion.
        var graph = searchResultToGraphvizGraphService.process(new BoxSearchAlgorithm.SearchResult(boxTree, null));
        var nodes = graph.rootNodes().toArray();
        for (int i = 0; i < nodes.length; ++i) {
            var currentNode = (MutableNode) nodes[i];
            assertThat(currentNode.name().toString()).isEqualTo(resultNames[i]);
        }

        // Graph links assertion.
        var rootNode = (MutableNode) nodes[0];
        var rootNodeLinks = rootNode.links();
        assertThat(rootNodeLinks.size()).isEqualTo(2);
        for (var link : rootNodeLinks) {
            assertThat(link.from()).isEqualTo(rootNode);
        }
    }
}