package com.wehjin.varnum.compiler;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


class UnionClassParser {
    private final ProcessingEnvironment processingEnv;
    private final Map<ClassName, ClassName> wrapperMap;

    UnionClassParser(ProcessingEnvironment processingEnv, Map<ClassName, ClassName> wrapperMap) {
        this.processingEnv = processingEnv;
        this.wrapperMap = wrapperMap;
    }

    Set<UnionClass> parseUnionClasses(Set<TypeElement> unprocessedTypes) {
        Set<UnionClass> unionClasses = new HashSet<>();
        for (Iterator<TypeElement> iterator = unprocessedTypes.iterator(); iterator.hasNext(); ) {
            TypeElement element = iterator.next();
            UnionClass unionClass = createUnionClass(element);
            unionClasses.add(unionClass);
            iterator.remove();
        }
        return unionClasses;
    }

    private UnionClass createUnionClass(TypeElement typeElement) {
        ClassName className = ClassName.get(typeElement);
        ClassName wrappedClassName = wrapperMap.get(className);

        final UnionClass unionClass = new UnionClass(wrappedClassName.simpleName(), wrappedClassName.packageName());
        final List<ExecutableElement> methods = findMethods(typeElement);
        for (ExecutableElement methodElement : methods) {
            final UnionClassMember unionClassMember = new UnionClassMember(methodElement.getSimpleName().toString());
            final List<? extends VariableElement> parameterElements = methodElement.getParameters();
            if (parameterElements.size() > 2) {
                throw new IllegalArgumentException("Too many parameters in tagged-union member type");
            }
            for (VariableElement parameter : parameterElements) {
                unionClassMember.valueTypes.add(ClassName.get(parameter.asType()));
            }
            unionClass.members.add(unionClassMember);
        }
        return unionClass;
    }

    private List<ExecutableElement> findMethods(TypeElement typeElement) {
        final List<ExecutableElement> methods = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) element);
            }
        }
        return methods;
    }
}
