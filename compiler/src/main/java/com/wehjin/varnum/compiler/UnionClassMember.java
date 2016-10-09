package com.wehjin.varnum.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


class UnionClassMember {
    final String simpleName;
    final List<String> valueTypes = new ArrayList<>();

    UnionClassMember(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return "UnionClassMember{" +
              "simpleName='" + simpleName + '\'' +
              ", valueTypes=" + valueTypes +
              '}';
    }
}
