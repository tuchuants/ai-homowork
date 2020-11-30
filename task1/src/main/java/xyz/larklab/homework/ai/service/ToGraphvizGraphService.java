package xyz.larklab.homework.ai.service;

import guru.nidi.graphviz.model.MutableGraph;

import static guru.nidi.graphviz.model.Factory.mutGraph;


public interface ToGraphvizGraphService<SourceObject> {
    /**
     * Product a mutable graph from the source object.
     *
     * @param graph the mutable graph which would bearer the source object data.
     */
    void process(MutableGraph graph, SourceObject sourceObject);

    /**
     * Product a new mutable graph from the source object.
     *
     * @param isDirected whether the graph is directed.
     * @return the mutable graph which would bearer the source object data.
     */
    default MutableGraph process(SourceObject sourceObject, boolean isDirected) {
        var graph = mutGraph().setDirected(isDirected);
        process(graph, sourceObject);
        return graph;
    }

    /**
     * Product a new directed mutable graph from the source object.
     */
    default MutableGraph process(SourceObject sourceObject) {
        return process(sourceObject, true);
    }
}
