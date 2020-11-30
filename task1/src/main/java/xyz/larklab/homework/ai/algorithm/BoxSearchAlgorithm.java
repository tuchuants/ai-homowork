package xyz.larklab.homework.ai.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import xyz.larklab.homework.ai.entity.Box;

import java.util.*;

public class BoxSearchAlgorithm {
    private static final int SEARCH_BEGIN_INDEX = 1;

    public static SearchResult BFS(Box start, Box findBox) {
        Queue<Box> openTable = new LinkedList<>();
        var rootElement = new Box(start.getStatus(), start.getSpaceLocation(), null, SEARCH_BEGIN_INDEX);
        openTable.add(rootElement);
        var finalBox = processBFS(openTable, findBox);
        return finalBox != null ? new SearchResult(new Box.BoxTree(rootElement), finalBox) : null;
    }

    /**
     * The actual process function of BFS search.
     *
     * @param findBox the box wanted to find. its parent would never be backfilled.
     * @return The found box with parent chain.
     */
    private static Box processBFS(Queue<Box> openTable, Box findBox) {
        var closeTable = new HashSet<Box>();
        var searchIndex = SEARCH_BEGIN_INDEX;
        while (!openTable.isEmpty()) {
            var currentBox = openTable.remove();
            closeTable.add(currentBox);
            for (var childBox : currentBox.getIterableChildren()) {
                if (!closeTable.contains(childBox) && !openTable.contains(childBox)) {
                    ++searchIndex;
                    childBox.setSearchIndex(searchIndex);
                    currentBox.addChild(childBox);
                    if (childBox.equals(findBox)) {
                        return childBox;
                    }
                    openTable.add(childBox);
                }
            }
        }
        return null;
    }

    public static SearchResult DFS(Box start, Box findBox, int maxDepth) {
        var rootElement = new Box(start.getStatus(), start.getSpaceLocation(), null, SEARCH_BEGIN_INDEX);
        var boxStack = new ArrayDeque<Box>();
        var iteratorStack = new ArrayDeque<Iterator<Box>>();
        //var closeTable = new HashSet<Box>();
        var searchIndex = SEARCH_BEGIN_INDEX;
        boxStack.push(rootElement);
        iteratorStack.push(rootElement.getIterableChildren().iterator());
        while (!boxStack.isEmpty()) {
            var current = boxStack.peek();
            var currentIterator = iteratorStack.peek();
            if (current.equals(findBox)) return new SearchResult(new Box.BoxTree(rootElement), current);
            assert currentIterator != null;
            if (currentIterator.hasNext()) {
                var currentChild = currentIterator.next();
                var currentChildIterator = currentChild.getIterableChildren().iterator();
                if (!boxStack.contains(currentChild) && boxStack.size() < maxDepth) {
                    boxStack.push(currentChild);
                    iteratorStack.push(currentChildIterator);
                    current.addChild(currentChild);
                    ++searchIndex;
                    currentChild.setSearchIndex(searchIndex);
                }
            } else {
                //closeTable.add(boxStack.pop());
                boxStack.pop();
                iteratorStack.pop();
            }
        }
        return null;
    }

    private static final LossFunction<Integer, Box> simpleLossFunction = (current, goal) -> {
        int line = goal.getStatus().length;
        int col = goal.getStatus()[0].length;
        var currentStatus = current.getStatus();
        var goalStatus = goal.getStatus();
        int result = 0;
        for (int lineIndex = 0; lineIndex < line; ++lineIndex) {
            for (int colIndex = 0; colIndex < col; ++colIndex) {
                if (currentStatus[lineIndex][colIndex] != goalStatus[lineIndex][colIndex]) {
                    ++result;
                }
            }
        }
        return result;
    };

    public static SearchResult bestFirstSearch(Box start, Box findBox) {
        LossFunction<Double, BoxLevelPair> wrappedLossFunction =
                (current, goal) -> (double) simpleLossFunction.invoke(current.getBox(), goal.getBox());
        return bestFirstSearch(start, findBox, wrappedLossFunction);
    }

    public static SearchResult bestFirstSearch(Box start, Box findBox, LossFunction<Double, BoxLevelPair> wrappedLossFunction) {

        var lossComparator = new BestFirstSearchComparator<Double, BoxLevelPair>(wrappedLossFunction, new BoxLevelPair(findBox, 0));
        var openTable = new PriorityQueue<>(lossComparator);
        var closeTable = new HashMap<Box, Integer>();
        var rootElement = new Box(start.getStatus(), start.getSpaceLocation(), null, SEARCH_BEGIN_INDEX);
        var searchIndex = SEARCH_BEGIN_INDEX;
        openTable.add(new BoxLevelPair(rootElement, 1));
        while (!openTable.isEmpty()) {
            var current = openTable.remove();
            var currentBox = current.getBox();
            var currentLevel = current.getLevel();
            closeTable.put(currentBox, currentLevel);
            currentBox.setSearchIndex(searchIndex);
            ++searchIndex;
            if (currentBox.equals(findBox)) return new SearchResult(new Box.BoxTree(rootElement), findBox);
            for (var child : currentBox.getIterableChildren()) {
                var childBoxLevelPair = new BoxLevelPair(child, currentLevel + 1);
                if (!closeTable.containsKey(child)) {
                    processBestFirstSearchAppend(openTable, current, childBoxLevelPair);
                } else {
                    int level = closeTable.get(child);
                    if (childBoxLevelPair.getLevel() < level)
                        processBestFirstSearchAppend(openTable, current, childBoxLevelPair);
                }
            }
        }
        return null;
    }

    public static SearchResult AAsteriskSearch(Box start, Box findBox) {
        LossFunction<Double, BoxLevelPair> wrappedLossFunction =
                (current, goal) -> simpleLossFunction.invoke(current.getBox(), goal.getBox()) + current.getLevel() * 0.1;
        return bestFirstSearch(start, findBox, wrappedLossFunction);
    }

    private static void processBestFirstSearchAppend(Collection<BoxLevelPair> openTable, BoxLevelPair current,
                                                     BoxLevelPair child) {
        current.getBox().addChild(child.getBox());
        openTable.add(child);
    }

    public interface LossFunction<Numeric, RelatedObject> {
        Numeric invoke(RelatedObject current, RelatedObject goal);
    }

    @Data
    public static class SearchResult {
        @NonNull
        private final Box.BoxTree boxTree;
        private final Box finalBox;
    }

    @AllArgsConstructor
    private static class BestFirstSearchComparator<Numeric extends Comparable<Numeric>, RelatedObject> implements Comparator<RelatedObject> {
        private final LossFunction<Numeric, RelatedObject> lossFunction;
        private final RelatedObject goal;

        @Override
        public int compare(RelatedObject o1, RelatedObject o2) {
            var loss1 = lossFunction.invoke(o1, goal);
            var loss2 = lossFunction.invoke(o2, goal);
            return loss1.compareTo(loss2);
        }
    }

    @Data
    private static class BoxLevelPair {
        private final Box box;
        @EqualsAndHashCode.Exclude
        private final int level;
    }
}
