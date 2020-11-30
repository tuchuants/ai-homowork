package xyz.larklab.homework.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import xyz.larklab.homework.ai.algorithm.BoxSearchAlgorithm;
import xyz.larklab.homework.ai.entity.Box;
import xyz.larklab.homework.ai.service.ToImageService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Main {
    private final ToImageService<BoxSearchAlgorithm.SearchResult> toImageService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    void invoke() throws IOException {
        var start = new Box(new byte[][]{
                {2, 8, 3},
                {1, 6, 4},
                {7, 0, 5}
        }, new int[]{2, 1});

        /*var findBox = new Box(new byte[][]{
                {1, 2, 3},
                {4, 0, 5},
                {8, 7, 6}
        }, new int[]{1, 1});*/

        var findBox = new Box(new byte[][]{
                {1, 2, 3},
                {8, 0, 4},
                {7, 6, 5}
        }, new int[]{1, 1});

        toImageService.toLocalImage(Objects.requireNonNull(BoxSearchAlgorithm.BFS(start, findBox)), "bfs");
        toImageService.toLocalImage(Objects.requireNonNull(BoxSearchAlgorithm.DFS(start, findBox, 8)), "dfs");
        toImageService.toLocalImage(Objects.requireNonNull(BoxSearchAlgorithm.bestFirstSearch(start, findBox)), "best-first-search");
        toImageService.toLocalImage(Objects.requireNonNull(BoxSearchAlgorithm.AAsteriskSearch(start, findBox)), "A-asterisk-search");
    }
}
