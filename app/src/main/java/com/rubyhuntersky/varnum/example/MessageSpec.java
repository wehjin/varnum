package com.rubyhuntersky.varnum.example;

import com.wehin.varnum.TaggedUnion;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */

@TaggedUnion("Message")
interface MessageSpec {
    void Reset();
    void SetSize(Integer size);
    void Multi(Integer first, String second);
}
