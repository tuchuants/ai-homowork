package xyz.larklab.homework.ai.task2.service;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import lombok.RequiredArgsConstructor;
import xyz.larklab.homework.ai.task2.algorithm.GraphSearchAlgorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static guru.nidi.graphviz.model.Factory.mutNode;

@RequiredArgsConstructor
public abstract class GraphToGraphvizGraphService<Actual> implements ToGraphvizGraphService<GraphSearchAlgorithm.Graph<Actual>> {
    private final HtmlProcessor<Actual> htmlProcessor;

    @Override
    public void process(MutableGraph graph, GraphSearchAlgorithm.Graph<Actual> actualGraph) {
        var current = actualGraph.getRootElement();
        process(graph, current, new HashMap<>());
    }

    private MutableNode process(MutableGraph graph,
                                GraphSearchAlgorithm.Element<Actual> current,
                                Map<GraphSearchAlgorithm.Element<Actual>, MutableNode> nodeMap) {
        var thisNode = nodeMap.get(current);
        if (thisNode == null) {
            thisNode = mutNode(htmlProcessor.process(current));
            nodeMap.put(current, thisNode);
            graph.add(thisNode);
        }
        var successors = current.getGeneratedSuccessors();
        for (var andArc : successors) {
            var t = thisNode;
            if (andArc.size() > 1) {
                var rand = new Random().toString();
                rand = rand.substring(rand.length() - 4);
                t = mutNode("AND-arc [" + rand + "]");
                thisNode.addLink(t);
            }
            for (var element : andArc) {
                var node = nodeMap.get(element);
                if (node == null) {
                    node = process(graph, element, nodeMap);
                }
                t.addLink(node);
            }
        }
        return thisNode;
    }

    public interface HtmlProcessor<Actual> {
        default Label process(GraphSearchAlgorithm.Element<Actual> element) {
            return Label.htmlLines("Cost:" + element.getCost(), process(element.getActual()));
        }

        String process(Actual actual);
    }
}
