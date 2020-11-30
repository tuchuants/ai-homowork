package xyz.larklab.homework.ai.entity;

import org.junit.jupiter.api.Test;
import xyz.larklab.homework.ai.utils.BoxHelper;

import static org.assertj.core.api.Assertions.assertThat;

class BoxTest {
    static class ChildrenIteratorTest {
        @Test
        void controlGroup() {
            assertThat(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}
            }).isDeepEqualTo(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}
            });

            assertThat(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}
            }).isEqualTo(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}
            });

            char[][] array = {{'1', '2'}, {'3', '4'}};
            assertThat(array).isEqualTo(new char[][]{{'1', '2'}, {'3', '4'}});

            assertThat(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}
            }).isNotEqualTo(new Byte[][]{
                    {1, 2, 3}, {4, 5, 6}, {7, 8, 10}
            });

        }

        @Test
        void simple() {
            var root = BoxHelper.getExampleRootBox();
            var children = BoxHelper.getExampleBoxChildren();
            int index = 0;
            for (var boxIterator = root.getIterableChildren().iterator(); boxIterator.hasNext(); ++index) {
                var child = boxIterator.next();
                assertThat(child).isEqualTo(children[index]);
            }
            assertThat(index).isEqualTo(4);
        }

        @Test
        void boundaryLeftDown() {
            var root = new Box(new byte[][]{
                    {1, 2, 3},
                    {4, 5, 6},
                    {0, 7, 8}
            }, new int[]{2, 0}, null);
            var children = new Box[]{
                    new Box(new byte[][]{
                            {1, 2, 3},
                            {0, 5, 6},
                            {4, 7, 8}
                    }, new int[]{1, 0}, root),
                    new Box(new byte[][]{
                            {1, 2, 3},
                            {4, 5, 6},
                            {7, 0, 8}
                    }, new int[]{2, 1}, root),
            };
            int index = 0;
            for (var boxIterator = root.getIterableChildren().iterator(); boxIterator.hasNext(); ++index) {
                var child = boxIterator.next();
                assertThat(child.getStatus()).isEqualTo(children[index].getStatus());
            }
            assertThat(index).isEqualTo(2);
        }

        @Test
        void boundaryRightUp() {
            var root = new Box(new byte[][]{
                    {1, 2, 0},
                    {3, 4, 5},
                    {6, 7, 8}
            }, new int[]{0, 2}, null);
            var children = new Box[]{
                    new Box(new byte[][]{
                            {1, 2, 5},
                            {3, 4, 0},
                            {6, 7, 8}
                    }, new int[]{1, 2}, root),
                    new Box(new byte[][]{
                            {1, 0, 2},
                            {3, 4, 5},
                            {6, 7, 8}
                    }, new int[]{0, 1}, root),
            };
            int index = 0;
            for (var boxIterator = root.getIterableChildren().iterator(); boxIterator.hasNext(); ++index) {
                var child = boxIterator.next();
                assertThat(child.getStatus()).isEqualTo(children[index].getStatus());
            }
            assertThat(index).isEqualTo(2);
        }
    }
}