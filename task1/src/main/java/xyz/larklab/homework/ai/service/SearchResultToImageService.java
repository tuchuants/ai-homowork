package xyz.larklab.homework.ai.service;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.larklab.homework.ai.algorithm.BoxSearchAlgorithm;

import java.io.IOException;
import java.nio.file.Paths;

import static guru.nidi.graphviz.model.Factory.mutGraph;

@Service
@RequiredArgsConstructor
public class SearchResultToImageService implements ToImageService<BoxSearchAlgorithm.SearchResult> {
    private final ToGraphvizGraphService<BoxSearchAlgorithm.SearchResult> toGraphvizGraphService;
    @Setter(onMethod_ = {@Value("${render.dir}")})
    private String rootPath;

    @Override
    public void toLocalImage(BoxSearchAlgorithm.SearchResult searchResult, String name) throws IOException {
        var graph = mutGraph().setDirected(true).use((graph1, creationContext) -> graph1.nodeAttrs().add(Shape.PLAIN_TEXT));
        toGraphvizGraphService.process(graph, searchResult);
        Graphviz.fromGraph(graph).render(Format.PNG).toFile(Paths.get(rootPath, name + ".png").toFile());
    }
}
