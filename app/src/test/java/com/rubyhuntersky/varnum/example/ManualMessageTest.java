package com.rubyhuntersky.varnum.example;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */

public class ManualMessageTest {

    @Test
    public void messageMatchesReset() throws Exception {

        final boolean didMatch[] = {false};
        final ManualMessage message = ManualMessage.Reset();
        ManualMessage.match(message).isReset(new ManualMessage.MatchAction0() {
            @Override
            public void call() {
                didMatch[0] = true;
            }
        }).isSetSize(new ManualMessage.MatchAction1<Integer>() {
            @Override
            public void call(Integer value) {
                Assert.fail("Matched SetSize");
            }
        });
        Assert.assertTrue(didMatch[0]);
    }

    @Test
    public void messageMatchesSetSize() throws Exception {
        final boolean didMatch[] = {false};
        final ManualMessage message = ManualMessage.SetSize(5);
        ManualMessage.match(message).isReset(new ManualMessage.MatchAction0() {
            @Override
            public void call() {
                Assert.fail("Matched Reset");
            }
        }).isSetSize(new ManualMessage.MatchAction1<Integer>() {
            @Override
            public void call(Integer value) {
                Assert.assertEquals((Integer) 5, value);
                didMatch[0] = true;
            }
        });
        Assert.assertTrue(didMatch[0]);
    }

    @Test
    public void messageMatchesOrElse() throws Exception {
        final boolean didMatch[] = {false};
        final ManualMessage message = ManualMessage.Reset();
        ManualMessage.match(message).isSetSize(new ManualMessage.MatchAction1<Integer>() {
            @Override
            public void call(Integer value) {
                Assert.fail("Matched SetSize");
            }
        }).orElse(new ManualMessage.MatchAction0() {
            @Override
            public void call() {
                didMatch[0] = true;
            }
        });
        Assert.assertTrue(didMatch[0]);
    }
}