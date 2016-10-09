package com.wehjin.varnum.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.wehin.varnum.TaggedUnion;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class TaggedUnionProcessor extends AbstractProcessor {

    private Types typeUtil;
    private Elements elementUtils;
    private Filer filer;

    private Set<TypeElement> unprocessedTypes = new LinkedHashSet<>();
    private Map<ClassName, ClassName> wrapperMap = new LinkedHashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(TaggedUnion.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        typeUtil = env.getTypeUtils();
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        try {
            findTaggedUnionElements(roundEnvironment);
            final UnionClassParser unionClassParser = new UnionClassParser(processingEnv, wrapperMap);
            final Set<UnionClass> unionClasses = unionClassParser.parseUnionClasses(unprocessedTypes);
            final UnionClassGenerator unionClassGenerator = new UnionClassGenerator(filer);
            unionClassGenerator.generateUnionClasses(unionClasses);
        } catch (Throwable t) {
            // TODO Pass element in throwable.
            error("Error: " + t.getLocalizedMessage());
        }
        return true;
    }

    private void findTaggedUnionElements(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(TaggedUnion.class)) {

            // Ensure we are dealing with a TypeElement
            if (!(element instanceof TypeElement)) {
                error(TaggedUnion.class.getCanonicalName() + " applies to a type, " + element.getSimpleName() + " is a "
                            + element.getKind());
                continue;
            }

            TypeElement typeElement = (TypeElement) element;

            // Ensure the element isn't parameterized
            if (hasTypeArguments(typeElement)) {
                error(TaggedUnion.class.getCanonicalName() + " cannot be used directly on classes with type parameters");
                continue;
            }

            unprocessedTypes.add(typeElement);

            final ClassName className = ClassName.get(typeElement);
            final String taggedUnionName = typeElement.getAnnotation(TaggedUnion.class).value();
            final ClassName wrapperName = ClassName.get(className.packageName(), taggedUnionName);
            wrapperMap.put(className, wrapperName);
        }
    }

    private void error(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void error(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    private static boolean hasTypeArguments(TypeElement typeElement) {
        TypeMirror type = typeElement.asType();
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (typeArguments.size() > 0) {
                return true;
            }
        }
        return false;
    }
}
