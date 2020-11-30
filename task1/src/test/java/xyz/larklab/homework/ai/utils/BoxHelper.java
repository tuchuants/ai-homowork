package xyz.larklab.homework.ai.utils;

import xyz.larklab.homework.ai.entity.Box;

public class BoxHelper {
    private static final Box root;

    private static final Box[] children;

    static {
        root = new Box(new byte[][]{
                {1, 2, 3},
                {4, 0, 5},
                {6, 7, 8}
        }, new int[]{1, 1});
        root.setSearchIndex(1);
        children = new Box[]{
                new Box(new byte[][]{
                        {1, 0, 3},
                        {4, 2, 5},
                        {6, 7, 8}
                }, new int[]{0, 1}, root),
                new Box(new byte[][]{
                        {1, 2, 3},
                        {4, 7, 5},
                        {6, 0, 8}
                }, new int[]{2, 1}, root),
                new Box(new byte[][]{
                        {1, 2, 3},
                        {0, 4, 5},
                        {6, 7, 8}
                }, new int[]{1, 0}, root),
                new Box(new byte[][]{
                        {1, 2, 3},
                        {4, 5, 0},
                        {6, 7, 8}
                }, new int[]{1, 2}, root)
        };
        children[0].setSearchIndex(2);
        children[1].setSearchIndex(3);
        root.addChildren(children);
    }

    public static Box getExampleRootBox() {
        return root;
    }

    public static Box[] getExampleBoxChildren() {
        return children;
    }
}
