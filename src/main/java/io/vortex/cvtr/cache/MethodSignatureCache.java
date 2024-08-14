package io.vortex.cvtr.cache;

import io.vortex.cvtr.process.data.MethodSignature;

import java.util.HashMap;
import java.util.Map;

public class MethodSignatureCache {

    private static final Map<MethodSignature, String> codeCache = new HashMap<>();

    public static boolean checkCacheExist(MethodSignature sign) {
        return codeCache.containsKey(sign);
    }

    public static void addCache(MethodSignature sign, String code) {
        codeCache.put(sign, code);
    }

    public static void refresh(MethodSignature sign) {
        codeCache.remove(sign);
    }

}
