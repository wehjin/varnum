package com.wehjin.varnum.compiler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


class UnionClass {
    final String simpleName;
    final String packageName;
    final Set<UnionClassMember> members = new HashSet<>();

    UnionClass(String simpleName, String packageName) {
        this.simpleName = simpleName;
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "UnionClass{" +
              "simpleName='" + simpleName + '\'' +
              ", packageName='" + packageName + '\'' +
              ", members=" + members +
              '}';
    }
}
