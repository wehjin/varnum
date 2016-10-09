package com.wehin.varnum;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */
abstract public class BaseMatcher<T> {
    protected final T candidate;
    protected boolean didMatch;

    protected BaseMatcher(T candidate) {
        this.candidate = candidate;
    }

    public void remaining(MatchAction0 matchAction) {
        if (!didMatch) {
            didMatch = true;
            matchAction.call();
        }
    }
}
