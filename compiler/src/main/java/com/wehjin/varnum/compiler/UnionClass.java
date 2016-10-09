package com.wehjin.varnum.compiler;

import com.squareup.javapoet.ClassName;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


class UnionClass {
    public static final String MATHER_SIMPLE_NAME = "Matcher";
    final String simpleName;
    final String packageName;
    final Set<UnionClassMember> members = new HashSet<>();

    UnionClass(String simpleName, String packageName) {
        this.simpleName = simpleName;
        this.packageName = packageName;
    }

    ClassName getClassName() {
        return ClassName.get(packageName, simpleName);
    }

    String getParameterName() {
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    ClassName getMatcherClassName() {
        return ClassName.get(packageName, simpleName, MATHER_SIMPLE_NAME);
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
