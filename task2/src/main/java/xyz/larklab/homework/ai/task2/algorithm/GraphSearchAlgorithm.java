package xyz.larklab.homework.ai.task2.algorithm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.*;

public class GraphSearchAlgorithm {
    public static <T> Graph<T> AOStarSearch(T start, SearchAlgorithmOptions<T> options) {
        final var FUTILITY = SimpleElement.GREATEST_NUM;
        var successorGenerator = Objects.requireNonNull(options.successorGenerator);
        var costEstimator = Objects.requireNonNull(options.costEstimator);
        var startElement = new SimpleElement<>(start, costEstimator.calculate(start));
        while (!startElement.isSolved() && startElement.getCost() < FUTILITY) {
            // Traverse the graph, starting at the initial node and following the current best path.
            Queue<Element<T>> helperQueue = new ArrayDeque<>();
            helperQueue.add(startElement);
            SimpleElement<T> current = (SimpleElement<T>) helperQueue.remove();
            while (current != null){
                if (!current.isExpanded()) break;
                else if (!current.isSolved() && current.getMinimumCostElement() != null) {
                    helperQueue.addAll(current.getMinimumCostElement());
                }
                current = (SimpleElement<T>) helperQueue.poll();
            }

            if (current == null) break;

            // Pick one of these unexpanded nodes and expand it.
            current.expand(successorGenerator, costEstimator);

            // Backward.
            current.backward();
        }

        if (startElement.isSolved()) return new Graph<>(startElement);
        else return null;
    }

    public interface SuccessorGenerator<T> {
        List<List<T>> generate(T t);

        default Iterable<List<T>> getIterableResult(T t) {
            return generate(t);
        }
    }

    public interface CostEstimator<T> {
        int calculate(T t);
    }

    public interface Element<Actual> {
        Actual getActual();

        List<Element<Actual>> getParents();

        List<Element<Actual>> getMinimumCostElement();

        List<List<Element<Actual>>> getGeneratedSuccessors();

        int getCost();
    }

    private static class AOStarSearch {
    }

    @Data
    public static class SearchAlgorithmOptions<T> {
        private SuccessorGenerator<T> successorGenerator;
        private CostEstimator<T> costEstimator;
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class SimpleElement<Actual> implements Element<Actual> {
        private static final int GREATEST_NUM = 1024 * 1024 * 1024;
        @EqualsAndHashCode.Include
        private final Actual actual;
        @NonNull
        @ToString.Exclude
        private final List<Element<Actual>> parents;
        @NonNull
        private final List<List<Element<Actual>>> generatedSuccessors;
        @NonNull
        @ToString.Exclude
        private final Map<Actual, Element<Actual>> visitedMap;
        private final Set<Actual> ancestorSet;
        private List<Element<Actual>> minimumCostElement;
        private boolean expanded;
        private boolean solved;
        private int cost;

        public SimpleElement(Actual actual, int estimatedCost) {
            this(actual, new HashMap<>(), estimatedCost);
        }

        public SimpleElement(Actual actual, @NonNull Map<Actual, @NonNull Element<Actual>> visitedMap,
                             int estimatedCost) {
            this(actual, new ArrayList<>(), new ArrayList<>(), visitedMap, estimatedCost);
        }

        public SimpleElement(Actual actual, @NonNull List<Element<Actual>> parents,
                             @NonNull List<List<Element<Actual>>> generatedSuccessors,
                             @NonNull Map<Actual, Element<Actual>> visitedMap, int estimatedCost) {
            this.actual = actual;
            this.parents = parents;
            this.generatedSuccessors = generatedSuccessors;
            this.visitedMap = visitedMap;
            this.cost = estimatedCost;
            ancestorSet = new HashSet<>();
            for (var parent : parents) {
                ancestorSet.add(parent.getActual());
                ancestorSet.addAll(((SimpleElement<Actual>) parent).getAncestorSet());
            }
            if (estimatedCost == 0) solved = true;
        }

        public void addParent(SimpleElement<Actual> parent) {
            parents.add(parent);
            ancestorSet.add(parent.getActual());
            ancestorSet.addAll(parent.getAncestorSet());
        }

        public void expand(SuccessorGenerator<Actual> successorGenerator, CostEstimator<Actual> costEstimator) {
            for (var andArcOfActual : successorGenerator.getIterableResult(actual)) {
                var newArc = new ArrayList<Element<Actual>>();
                var failure = false;

                // Generate current AND-arc and it's cost.
                for (var thisActual : andArcOfActual) {
                    // If the element is already in the graph, just get it instead of creating a new instance.
                    var newElement = visitedMap.get(thisActual);
                    if (newElement == null) {
                        newElement = new SimpleElement<>(
                                thisActual, this.visitedMap, costEstimator.calculate(thisActual));
                        visitedMap.put(thisActual, newElement);
                    } else if (this.ancestorSet.contains(thisActual)) {
                        failure = true;
                        break;
                    }

                    var newSimpleElement = ((SimpleElement<Actual>) newElement);
                    newSimpleElement.addParent(this);
                    newArc.add(newSimpleElement);
                }
                // If failure flag is set the arc is impossible to completed, so this arc should be aborted.
                if (failure) continue;

                // Add this arc to the successors' list.
                generatedSuccessors.add(newArc);
            }
            // Update cost information.
            calculateCostAndSet(this);
            expanded = true;
        }

        public void backward() {
            Set<Element<Actual>> visited = new HashSet<>();
            Queue<Element<Actual>> parentsQueue = new ArrayDeque<>(this.getParents());
            while (!parentsQueue.isEmpty()) {
                var current = (SimpleElement<Actual>) parentsQueue.remove();
                var currentParents = current.getParents();
                for (var parent : currentParents) {
                    if (!visited.contains(parent)) {
                        parentsQueue.add(parent);
                        visited.add(parent);
                    }
                }
                calculateCostAndSet(current);
                calculateSolvedFlagAndSet(current);
            }
        }

        private void calculateCostAndSet(SimpleElement<Actual> current) {
            if (current.solved) return;
            var minCost = GREATEST_NUM;
            for (var andArc : current.getGeneratedSuccessors()) {
                var arcCost = andArc.size();
                for (var element : andArc) {
                    arcCost += ((SimpleElement<Actual>) element).getCost();
                }
                if (arcCost < minCost) {
                    minCost = arcCost;
                    current.setMinimumCostElement(andArc);
                }
            }
            current.setCost(minCost);
        }

        private void calculateSolvedFlagAndSet(SimpleElement<Actual> current) {
            var andArc = current.getMinimumCostElement();
            var solvedArc = true;
            for (var element : andArc) {
                if (!((SimpleElement<Actual>) element).isSolved()) {
                    solvedArc = false;
                    break;
                }
            }
            if (solvedArc) {
                current.solved = true;
            }
        }
    }

    @Data
    public static class Graph<Actual> {
        private final Element<Actual> rootElement;
    }
}
