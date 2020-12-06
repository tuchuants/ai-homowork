package xyz.larklab.homework.ai.task2;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import xyz.larklab.homework.ai.task2.algorithm.GraphSearchAlgorithm;
import xyz.larklab.homework.ai.task2.service.ToImageService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

@RequiredArgsConstructor
@SpringBootApplication
public class Main {
    private final ToImageService<GraphSearchAlgorithm.Graph<PlainNode>> toImageService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void invoke() throws IOException {
        // Prepare data.
        var elementMap = new HashMap<String, PlainNode>();
        for (char c = 'A'; c <= 'J'; ++c) {
            String name = String.valueOf(c);
            elementMap.put(name, new PlainNode(name));
        }
        elementMap.get("A").getSuccessors().add(Arrays.asList(elementMap.get("B"), elementMap.get("C")));
        elementMap.get("A").getSuccessors().add(Collections.singletonList(elementMap.get("D")));
        elementMap.get("A").setEstimatedCost(5);
        elementMap.get("B").getSuccessors().add(Arrays.asList(elementMap.get("G"), elementMap.get("H")));
        elementMap.get("B").setEstimatedCost(3);
        elementMap.get("C").getSuccessors().add(Collections.singletonList(elementMap.get("E")));
        elementMap.get("C").setEstimatedCost(2);
        elementMap.get("D").getSuccessors().add(Arrays.asList(elementMap.get("E"), elementMap.get("F")));
        elementMap.get("D").setEstimatedCost(6);
        elementMap.get("E").setEstimatedCost(0);
        elementMap.get("F").setEstimatedCost(11);
        elementMap.get("G").getSuccessors().add(Arrays.asList(elementMap.get("I"), elementMap.get("J")));
        elementMap.get("G").setEstimatedCost(3);
        elementMap.get("H").getSuccessors().add(Arrays.asList(elementMap.get("I"), elementMap.get("J")));
        elementMap.get("H").setEstimatedCost(2);
        elementMap.get("I").setEstimatedCost(0);
        elementMap.get("J").setEstimatedCost(0);

        var searchOptions = new GraphSearchAlgorithm.SearchAlgorithmOptions<PlainNode>();
        searchOptions.setCostEstimator(PlainNode::getEstimatedCost);
        searchOptions.setSuccessorGenerator(PlainNode::getSuccessors);

        var graph =
                GraphSearchAlgorithm.AOStarSearch(elementMap.get("A"), searchOptions);
        toImageService.toLocalImage(graph, "AO-star");
    }


}
