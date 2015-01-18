package uk.ac.brighton.uni.ab607.mmorpg.test;

import javafx.scene.Parent;

import com.almasb.common.test.Test;

public abstract class OrionTestBase extends Test {

    public abstract Parent getResultsContent();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
