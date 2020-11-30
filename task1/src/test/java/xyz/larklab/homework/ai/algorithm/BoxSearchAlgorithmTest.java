package xyz.larklab.homework.ai.algorithm;

import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;
import xyz.larklab.homework.ai.entity.Box;

class BoxSearchAlgorithmTest {
    @Test
    void BFS() throws ExecutionControl.NotImplementedException {
        var start = new Box(new byte[][]{
                {2, 8, 3},
                {1, 6, 4},
                {7, 0, 5}
        }, new int[]{2, 1}, null);

        var findBox = new Box(new byte[][]{
                {1, 2, 3},
                {8, 0, 4},
                {7, 6, 5}
        }, new int[]{1, 1}, null);

        var searchResult = BoxSearchAlgorithm.BFS(start, findBox);

        throw new ExecutionControl.NotImplementedException("");
    }
}