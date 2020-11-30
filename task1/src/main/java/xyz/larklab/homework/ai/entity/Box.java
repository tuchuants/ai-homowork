package xyz.larklab.homework.ai.entity;

import lombok.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Box {
    private static final byte SPACE_NUM = 0;
    @NonNull
    @EqualsAndHashCode.Include
    private byte[][] status;
    @NonNull
    private int[] spaceLocation;
    private Box parent;
    private int searchIndex;
    @NonNull
    @ToString.Exclude
    private final List<Box> children;

    public static class ChildrenIterator implements Iterable<Box>, Iterator<Box> {
        private interface AtomicOperation {
            int[] invoke(byte[][] status, int x, int y);
        }

        private interface Operation {
            Box invoke(Box box);
        }

        static final Operation[] OPERATION_MAP;
        static final int OPERATION_MAP_LENGTH;

        static {
            OPERATION_MAP = new Operation[4];
            // Use a function wrapper to implement a simple AOP to simplify code writing process.
            BiFunction<Box, AtomicOperation, Box> operation = (box, atomicOperation) -> {
                var spaceLocation = box.getSpaceLocation();
                var x = spaceLocation[0];
                var y = spaceLocation[1];
                var status = box.getStatus().clone();
                for (int i = 0; i < status.length; ++i) {
                    status[i] = status[i].clone();
                }
                var newSpaceLocationOrNull = atomicOperation.invoke(status, x, y);
                if (newSpaceLocationOrNull == null) return null;
                return new Box(status, newSpaceLocationOrNull, box);
            };
            // Space move up operation.
            AtomicOperation moveUp = (status1, x, y) -> {
                if (x - 1 < 0) return null;
                status1[x][y] = status1[x - 1][y];
                status1[x - 1][y] = SPACE_NUM;
                return new int[]{x - 1, y};
            };
            // Space move down operation.
            AtomicOperation moveDown = (status1, x, y) -> {
                if (x + 1 >= status1.length) return null;
                status1[x][y] = status1[x + 1][y];
                status1[x + 1][y] = SPACE_NUM;
                return new int[]{x + 1, y};
            };
            // Space move left operation.
            AtomicOperation moveLeft = (status1, x, y) -> {
                if (y - 1 < 0) return null;
                status1[x][y] = status1[x][y - 1];
                status1[x][y - 1] = SPACE_NUM;
                return new int[]{x, y - 1};
            };
            // Space move right operation.
            AtomicOperation moveRight = (status1, x, y) -> {
                if (y + 1 >= status1[x].length) return null;
                status1[x][y] = status1[x][y + 1];
                status1[x][y + 1] = SPACE_NUM;
                return new int[]{x, y + 1};
            };
            AtomicOperation[] atomicOperations = {moveUp, moveDown, moveLeft, moveRight};
            for (int i = 0; i < atomicOperations.length; ++i) {
                AtomicOperation atomicOperation = atomicOperations[i];
                OPERATION_MAP[i] = box -> operation.apply(box, atomicOperation);
            }
            OPERATION_MAP_LENGTH = OPERATION_MAP.length;
        }

        private final Iterator<Box> iterator;

        public ChildrenIterator(Box box) {
            var currentOperationIndex = 0;
            var children = new ArrayList<Box>();
            while (currentOperationIndex < OPERATION_MAP_LENGTH) {
                var resultOrNull = OPERATION_MAP[currentOperationIndex].invoke(box);
                if (resultOrNull != null) children.add(resultOrNull);
                ++currentOperationIndex;
            }
            iterator = children.iterator();
        }

        @Override
        public @Nonnull
        Iterator<Box> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Box next() {
            return iterator.next();
        }
    }

    @Data
    public static class BoxTree {
        private int length = 0;
        @NonNull
        private final Box rootElement;


        public BoxTree(Box rootElement) {
            this.rootElement = rootElement;
            ++length;
        }
    }

    public Box(byte[][] status, int[] spaceLocation, Box parent, int searchIndex) {
        this(status, spaceLocation, parent, searchIndex, new ArrayList<>());
    }

    public Box(byte[][] status, int[] spaceLocation, Box parent) {
        this(status, spaceLocation, parent, 0);
    }

    public Box(byte[][] status, int[] spaceLocation) {
        this(status, spaceLocation, null);
    }

    public void addChild(Box box) {
        children.add(box);
    }

    public void addChildren(Box... boxes) {
        children.addAll(Arrays.asList(boxes));
    }

    public Iterable<Box> getIterableChildren() {
        return new ChildrenIterator(this);
    }
}
