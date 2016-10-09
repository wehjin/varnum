package com.rubyhuntersky.varnum.example;

import com.wehin.varnum.BaseMatcher;
import com.wehin.varnum.MatchAction0;
import com.wehin.varnum.MatchAction1;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


@SuppressWarnings("WeakerAccess")
public abstract class MyMessage {

    public static MyMessage Reset() {
        return new Reset();
    }

    public static MyMessage SetSize(Integer value) {
        return new SetSize(value);
    }

    public static Matcher match(MyMessage message) {
        return new Matcher(message);
    }

    private static class Reset extends MyMessage {
        static boolean isMatchFor(MyMessage message) {
            return message instanceof Reset;
        }
    }

    private static class SetSize extends MyMessage {
        private final Integer value;

        SetSize(Integer value) {
            this.value = value;
        }

        static Integer getValue(MyMessage message) {
            return ((SetSize) message).value;
        }

        static boolean isMatchFor(MyMessage message) {
            return message instanceof SetSize;
        }
    }

    public static class Matcher extends BaseMatcher<MyMessage> {

        private Matcher(MyMessage candidate) {
            super(candidate);
        }

        public Matcher isReset(MatchAction0 matchAction) {
            if (!didMatch && Reset.isMatchFor(candidate)) {
                didMatch = true;
                matchAction.call();
            }
            return this;
        }

        public Matcher isSetSize(MatchAction1<Integer> matchAction) {
            if (!didMatch && SetSize.isMatchFor(candidate)) {
                didMatch = true;
                matchAction.call(SetSize.getValue(candidate));
            }
            return this;
        }
    }

}
