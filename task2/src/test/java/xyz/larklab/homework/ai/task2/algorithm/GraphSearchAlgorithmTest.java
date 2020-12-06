package xyz.larklab.homework.ai.task2.algorithm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphSearchAlgorithmTest {
    @Data
    @RequiredArgsConstructor
    private static class TestElement {
        final String name;
        final List<List<TestElement>> successors;
        int estimatedCost;

        public TestElement(String name) {
            this.name = name;
            this.successors = new ArrayList<>();
        }
    }
    @Test
    void simple() {
        // Prepare data.
        var elementMap = new HashMap<String, TestElement>();
        for (char c = 'A'; c <= 'J'; ++c) {
            String name = String.valueOf(c);
            elementMap.put(name, new TestElement(name));
        }
        elementMap.get("A").successors.add(Arrays.asList(elementMap.get("B"), elementMap.get("C")));
        elementMap.get("A").successors.add(Collections.singletonList(elementMap.get("D")));
        elementMap.get("A").estimatedCost = 5;
        elementMap.get("B").successors.add(Arrays.asList(elementMap.get("G"), elementMap.get("H")));
        elementMap.get("B").estimatedCost = 3;
        elementMap.get("C").successors.add(Collections.singletonList(elementMap.get("E")));
        elementMap.get("C").estimatedCost = 2;
        elementMap.get("D").successors.add(Arrays.asList(elementMap.get("E"), elementMap.get("F")));
        elementMap.get("D").estimatedCost = 6;
        elementMap.get("E").estimatedCost = 0;
        elementMap.get("F").estimatedCost = 11;
        elementMap.get("G").successors.add(Arrays.asList(elementMap.get("I"), elementMap.get("J")));
        elementMap.get("G").estimatedCost = 3;
        elementMap.get("H").successors.add(Arrays.asList(elementMap.get("I"), elementMap.get("J")));
        elementMap.get("H").estimatedCost = 2;
        elementMap.get("I").estimatedCost = 0;
        elementMap.get("J").estimatedCost = 0;
        var searchOptions = new GraphSearchAlgorithm.SearchAlgorithmOptions<TestElement>();
        searchOptions.setCostEstimator(testElement -> testElement.estimatedCost);
        searchOptions.setSuccessorGenerator(testElement -> testElement.successors);
        var graph =
                GraphSearchAlgorithm.AOStarSearch(elementMap.get("A"), searchOptions);
        System.out.println(graph);
    }
}