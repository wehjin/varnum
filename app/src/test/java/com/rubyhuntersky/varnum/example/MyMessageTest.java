package com.rubyhuntersky.varnum.example;

import com.wehin.varnum.MatchAction0;
import com.wehin.varnum.MatchAction1;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */

public class MyMessageTest {

    @Test
    public void messageMatchesReset() throws Exception {

        final boolean didMatch[] = {false};
        final MyMessage message = MyMessage.Reset();
        MyMessage.match(message).isReset(new MatchAction0() {
            @Override
            public void call() {
                didMatch[0] = true;
            }
        }).isSetSize(new MatchAction1<Integer>() {
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
        final MyMessage message = MyMessage.SetSize(5);
        MyMessage.match(message).isReset(new MatchAction0() {
            @Override
            public void call() {
                Assert.fail("Matched Reset");
            }
        }).isSetSize(new MatchAction1<Integer>() {
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
        final MyMessage message = MyMessage.Reset();
        MyMessage.match(message).isSetSize(new MatchAction1<Integer>() {
            @Override
            public void call(Integer value) {
                Assert.fail("Matched SetSize");
            }
        }).remaining(new MatchAction0() {
            @Override
            public void call() {
                didMatch[0] = true;
            }
        });
        Assert.assertTrue(didMatch[0]);
    }
}