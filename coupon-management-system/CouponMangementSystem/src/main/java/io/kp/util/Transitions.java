package io.kp.util;

import io.kp.model.BatchState;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transitions {

    public static Map<BatchState, List<BatchState>> transitionMap = new HashMap<>();

    static {
        transitionMap.put(BatchState.CREATED, List.of(BatchState.ACTIVE, BatchState.SUSPENDED,
                BatchState.TERMINATED));
        transitionMap.put(BatchState.ACTIVE, List.of(BatchState.SUSPENDED,
                BatchState.TERMINATED));
        transitionMap.put(BatchState.SUSPENDED, List.of(BatchState.ACTIVE,
                BatchState.TERMINATED));
        transitionMap.put(BatchState.TERMINATED, Collections.emptyList());
    }
}
