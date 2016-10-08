package com.wehin.varnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */

@Target(ElementType.TYPE)
public @interface TaggedUnion {
    String value();
}
