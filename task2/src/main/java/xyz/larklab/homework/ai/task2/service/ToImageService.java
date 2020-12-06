package xyz.larklab.homework.ai.task2.service;

import java.io.IOException;

public interface ToImageService<SourceObject> {
    /**
     * Product a image (or diagram) from source object to specified path.
     *
     * @param name the file name of image without suffix.
     */
    void toLocalImage(SourceObject sourceObject, String name) throws IOException;
}
