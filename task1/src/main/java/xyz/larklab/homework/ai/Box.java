package xyz.larklab.homework.ai;

import lombok.Data;

import java.util.Iterator;
import java.util.function.BiFunction;

@Data
public class Box {
    private static final byte SPACE_NUM = 0;
    private byte[][] status;
    private int[] spaceLocation;
    private Box parent;

    @Data
    public static class ChildrenIterator implements Iterable<Box>, Iterator<Box> {
        private static interface AtomicOperation {
            int[] invoke(byte[][] status, int x, int y);
        }

        private static interface Operation {
            Box invoke(Box box);
        }

        static final Operation[] OPERATION_MAP;
        static final int OPERATION_MAP_LENGTH;

        static  {
            OPERATION_MAP = new Operation[4];
            // Use a function wrapper to implement a simple AOP to simplify code writing process.
            BiFunction<Box, AtomicOperation, Box> operation = (box, atomicOperation) -> {
                var spaceLocation = box.getSpaceLocation();
                var x = spaceLocation[0];
                var y = spaceLocation[1];
                var status = box.getStatus().clone();
                var newSpaceLocation = atomicOperation.invoke(status, x, y);
                var result = new Box();
                result.setStatus(status);
                result.setSpaceLocation(newSpaceLocation);
                result.setParent(box);
                return result;
            };
            // Space move up operation.
            AtomicOperation moveUp = (status1, x, y) -> {
                status1[x][y] = status1[x-1][y];
                status1[x-1][y] = SPACE_NUM;
                return new int[]{x-1, y};
            };
            // Space move down operation.
            AtomicOperation moveDown = (status1, x, y) -> {
                status1[x][y] = status1[x+1][y];
                status1[x+1][y] = SPACE_NUM;
                return new int[]{x+1, y};
            };
            // Space move left operation.
            AtomicOperation moveLeft = (status1, x, y) -> {
                status1[x][y] = status1[x][y-1];
                status1[x][y-1] = SPACE_NUM;
                return new int[]{x, y-1};
            };
            // Space move right operation.
            AtomicOperation moveRight = (status1, x, y) -> {
                status1[x][y] = status1[x][y+1];
                status1[x][y+1] = SPACE_NUM;
                return new int[]{x, y+1};
            };
            AtomicOperation[] atomicOperations = {moveUp, moveDown, moveLeft, moveRight};
            for (int i = 0; i < atomicOperations.length; ++i) {
                AtomicOperation atomicOperation = atomicOperations[i];
                OPERATION_MAP[i] = box -> operation.apply(box, atomicOperation);
            }
            OPERATION_MAP_LENGTH = OPERATION_MAP.length;
        }

        private final Box box;
        private int currentOperationIndex = 0;

        @Override
        public Iterator<Box> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return currentOperationIndex < OPERATION_MAP_LENGTH;
        }

        @Override
        public Box next() {
            var result = OPERATION_MAP[currentOperationIndex].invoke(box);
            ++currentOperationIndex;
            return result;
        }
    }

    public Iterable<Box> getIterableChildren() {
        return new ChildrenIterator(this);
    }
}
