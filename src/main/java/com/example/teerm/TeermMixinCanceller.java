package com.example.teerm;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import java.util.List;

public class TeermMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.startsWith("io.github.jason13official.immortalize.mixin");
    }
}