package com.raylabs.doggie.utils.tab;

import org.junit.Test;
import static org.junit.Assert.*;

public class TabDividerDelegateTest {

    static class FakeController implements DividerController {
        final boolean linear;
        boolean removed = false;
        FakeController(boolean linear) { this.linear = linear; }

        @Override public boolean isLinearLayout() { return linear; }
        @Override public void removeDividers() { removed = true; }
    }

    @Test
    public void apply_callsRemove_whenLinear() {
        FakeController c = new FakeController(true);
        TabDividerDelegate.apply(c);
        assertTrue(c.removed);
    }

    @Test
    public void apply_noop_whenNotLinear() {
        FakeController c = new FakeController(false);
        TabDividerDelegate.apply(c);
        assertFalse(c.removed);
    }

    @Test
    public void apply_noop_whenNullController() {
        // should not throw
        TabDividerDelegate.apply(null);
    }
}