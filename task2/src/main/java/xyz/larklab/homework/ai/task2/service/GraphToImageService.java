package xyz.larklab.homework.ai.task2.service;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.larklab.homework.ai.task2.algorithm.GraphSearchAlgorithm;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class GraphToImageService<Actual> implements ToImageService<GraphSearchAlgorithm.Graph<Actual>> {
    private final ToGraphvizGraphService<GraphSearchAlgorithm.Graph<Actual>> toGraphvizGraphService;
    @Setter(onMethod_ = {@Value("${render.dir}")})
    private String rootPath;

    @Override
    public void toLocalImage(GraphSearchAlgorithm.Graph<Actual> graph, String name) throws IOException {
        var thisGraph = toGraphvizGraphService.process(graph);
        Graphviz.fromGraph(thisGraph).render(Format.PNG).toFile(Paths.get(rootPath, name + ".png").toFile());
    }
}
