package com.rubyhuntersky.varnum.example;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


@SuppressWarnings("WeakerAccess")
public abstract class ManualMessage {

    public static ManualMessage Reset() {
        return new Reset();
    }

    public static ManualMessage SetSize(Integer value) {
        return new SetSize(value);
    }

    public static Matcher match(ManualMessage message) {
        return new Matcher(message);
    }

    private static class Reset extends ManualMessage {
        static boolean isMatchFor(ManualMessage message) {
            return message instanceof Reset;
        }
    }

    private static class SetSize extends ManualMessage {
        private final Integer value;

        SetSize(Integer value) {
            this.value = value;
        }

        static Integer getValue(ManualMessage message) {
            return ((SetSize) message).value;
        }

        static boolean isMatchFor(ManualMessage message) {
            return message instanceof SetSize;
        }
    }

    public static class Matcher {

        private final ManualMessage message;
        private boolean didMatch;

        public Matcher(ManualMessage message) {
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
