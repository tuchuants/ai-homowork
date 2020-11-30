package xyz.larklab.homework.ai.service;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.springframework.stereotype.Service;
import xyz.larklab.homework.ai.algorithm.BoxSearchAlgorithm;
import xyz.larklab.homework.ai.entity.Box;

import static guru.nidi.graphviz.model.Factory.mutNode;

@Service
public class SearchResultToGraphvizGraphService implements ToGraphvizGraphService<BoxSearchAlgorithm.SearchResult> {
    @Override
    public void process(MutableGraph graph, BoxSearchAlgorithm.SearchResult searchResult) {
        process(graph, searchResult.getBoxTree().getRootElement(), searchResult.getFinalBox(), null);
    }

    private void process(MutableGraph graph, Box thisElement, Box finalElement,
                         MutableNode parentNode) {
        var thisGraphNode = mutNode(generateHtmlTable(thisElement));
        if (thisElement.equals(finalElement)) thisGraphNode.attrs().add(Color.RED4);
        graph.add(thisGraphNode);
        if (parentNode != null) {
            parentNode.addLink(thisGraphNode);
        }
        for (var childElement : thisElement.getChildren()) {
            process(graph, childElement, finalElement, thisGraphNode);
        }
    }

    private Label generateHtmlTable(Box boxElement) {
        var searchIndex = boxElement.getSearchIndex();
        var boxStatus = boxElement.getStatus();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table>");
        stringBuilder.append("<tr><td colspan=\"2\">IDX</td><td>").append(searchIndex).append("</td></tr>");
        for (var line : boxStatus) {
            stringBuilder.append("<tr>");
            {

            }
            for (var element : line) {
                stringBuilder.append("<td>");
                stringBuilder.append(element);
                stringBuilder.append("</td>");
            }
            stringBuilder.append("</tr>");
        }
        stringBuilder.append("</table>");
        return Label.html(stringBuilder.toString());
    }
}
