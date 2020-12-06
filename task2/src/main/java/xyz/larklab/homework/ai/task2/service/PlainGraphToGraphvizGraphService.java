package xyz.larklab.homework.ai.task2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.larklab.homework.ai.task2.PlainNode;

@Service
public class PlainGraphToGraphvizGraphService extends GraphToGraphvizGraphService<PlainNode> {
    public PlainGraphToGraphvizGraphService() {
        super(PlainNode::getName);
    }
}
