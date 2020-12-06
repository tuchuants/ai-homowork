package xyz.larklab.homework.ai.task2;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class PlainNode {
    private final String name;
    private final List<List<PlainNode>> successors;
    private int estimatedCost;

    public PlainNode(String name) {
        this.name = name;
        this.successors = new ArrayList<>();
    }
}
