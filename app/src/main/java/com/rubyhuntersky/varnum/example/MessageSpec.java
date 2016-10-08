package com.rubyhuntersky.varnum.example;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */

@TaggedUnion("Message")
interface MessageSpec<T> {
    MessageSpec Reset();
    MessageSpec SetSize(Integer size);
    MessageSpec Multi(Integer first, String second);
    MessageSpec Generic(T genericValue);
}
