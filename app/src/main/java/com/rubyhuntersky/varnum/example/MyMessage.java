package com.rubyhuntersky.varnum.example;

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

    public static class Matcher {

        private final MyMessage message;
        private boolean didMatch;

        public Matcher(MyMessage message) {
            this.message = message;
        }

        public Matcher isReset(MatchAction0 matchAction) {
            if (!didMatch && Reset.isMatchFor(message)) {
                didMatch = true;
                matchAction.call();
            }
            return this;
        }

        public Matcher isSetSize(MatchAction1<Integer> matchAction) {
            if (!didMatch && SetSize.isMatchFor(message)) {
                didMatch = true;
                matchAction.call(SetSize.getValue(message));
            }
            return this;
        }

        public void orElse(MatchAction0 matchAction) {
            if (!didMatch) {
                didMatch = true;
                matchAction.call();
            }
        }
    }

    public interface MatchAction1<T> {
        void call(T value);
    }

    public interface MatchAction0 {
        void call();
    }
}
