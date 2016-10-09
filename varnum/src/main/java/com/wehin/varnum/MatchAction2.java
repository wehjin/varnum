package com.wehin.varnum;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */
public interface MatchAction2<T1, T2> {
    void call(T1 value1, T2 value2);
}
