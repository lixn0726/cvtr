package io.vortex.cvtr.cache;

import java.util.ArrayList;
import java.util.List;

// todo:lithiumnzinc 2024/8/13 21:51 > 会有用的
public class ProcessedRefPair {

    private final int processId;

    private final List<String> processedRefs = new ArrayList<>();

    public ProcessedRefPair(int processId) {
        this.processId = processId;
    }

}
