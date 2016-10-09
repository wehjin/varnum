package com.wehjin.varnum.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wehin.varnum.BaseMatcher;
import com.wehin.varnum.MatchAction0;
import com.wehin.varnum.MatchAction1;
import com.wehin.varnum.MatchAction2;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author Jeffrey Yu
 * @since 10/8/16.
 */


class UnionClassGenerator {
    private final Filer filer;

    UnionClassGenerator(Filer filer) {
        this.filer = filer;
    }

    void generateUnionClasses(Set<UnionClass> unionClasses) {
        for (UnionClass unionClass : unionClasses) {
            try {
                generateUnionClass(unionClass).writeTo(filer);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while writing to filer." + e.getMessage(), e);
            }
        }
    }

    private MethodSpec generateMemberCreatorMethod(UnionClass unionClass, UnionClassMember member) {

        final MethodSpec.Builder builder = MethodSpec.methodBuilder(member.simpleName)
              .addModifiers(PUBLIC, STATIC)
              .returns(unionClass.getClassName());
        int index = 0;
        boolean appendIndex = member.valueTypes.size() > 1;
        String argumentsClause = "";
        for (TypeName valueType : member.valueTypes) {
            final String parameterName = "value" + (appendIndex ? (index + 1) : "");
            builder.addParameter(valueType, parameterName);
            if (argumentsClause.length() == 0) {
                argumentsClause = parameterName;
            } else {
                argumentsClause += ", " + parameterName;
            }
            index++;
        }
        final String statement = String.format("return new %s(%s)", member.simpleName, argumentsClause);
        return builder.addStatement(statement)
              .build();
    }

    private JavaFile generateUnionClass(UnionClass unionClass) throws IOException {
        TypeSpec.Builder unionClassBuilder = TypeSpec.classBuilder(unionClass.simpleName)
              .addModifiers(PUBLIC, ABSTRACT);
        for (UnionClassMember member : unionClass.members) {
            unionClassBuilder.addMethod(generateMemberCreatorMethod(unionClass, member));
        }

        for (UnionClassMember member : unionClass.members) {
            unionClassBuilder.addType(generateMemberClass(unionClass, member));
        }
        unionClassBuilder.addMethod(MethodSpec.methodBuilder("match")
                                          .addModifiers(PUBLIC, STATIC)
                                          .returns(unionClass.getMatcherClassName())
                                          .addParameter(unionClass.getClassName(), unionClass.getParameterName())
                                          .addStatement(String.format("return new %s(%s)",
                                                                      UnionClass.MATHER_SIMPLE_NAME,
                                                                      unionClass.getParameterName()))
                                          .build());

        unionClassBuilder.addType(generateMatcherClass(unionClass));

        return JavaFile.builder(unionClass.packageName, unionClassBuilder.build()).build();
    }

    private TypeSpec generateMatcherClass(UnionClass unionClass) {


        final ClassName baseMatcherName = ClassName.get(BaseMatcher.class);
        final TypeSpec.Builder builder = TypeSpec.classBuilder("Matcher");
        builder.addModifiers(PUBLIC, STATIC);
        builder.superclass(ParameterizedTypeName.get(baseMatcherName, unionClass.getClassName()));
        builder.addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(PRIVATE)
                                .addParameter(unionClass.getClassName(), unionClass.getParameterName())
                                .addStatement(String.format("super(%s)", unionClass.getParameterName()))
                                .build());
        for (UnionClassMember member : unionClass.members) {
            builder.addMethod(generateMemberMatcherMethod(unionClass, member));
        }
        return builder.build();
    }

    private MethodSpec generateMemberMatcherMethod(UnionClass unionClass, UnionClassMember member) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("is" + member.simpleName);
        builder.addModifiers(PUBLIC, FINAL);
        builder.returns(unionClass.getMatcherClassName());
        builder.addParameter(getMatchActionClass(member), "matchAction");
        String matchActionArguments = "";
        for (int index = 0; index < member.valueTypes.size(); index++) {
            final String format = "%s.getValue%d(candidate)";
            String argument = String.format(format, member.simpleName, index + 1);
            if (matchActionArguments.length() == 0) {
                matchActionArguments = argument;
            } else {
                matchActionArguments += ", " + argument;
            }
        }
        final CodeBlock ifBlock = CodeBlock.builder()
              .beginControlFlow(String.format("if (!didMatch && %s.isMatchFor(candidate))", member.simpleName))
              .addStatement("didMatch = true")
              .addStatement(String.format("matchAction.call(%s)", matchActionArguments))
              .endControlFlow()
              .build();
        builder.addCode(ifBlock);
        builder.addStatement("return this");
        return builder.build();
    }

    private Class<?> getMatchActionClass(UnionClassMember member) {
        Class<?> matchActionClass;
        switch (member.valueTypes.size()) {
            case 0:
                matchActionClass = MatchAction0.class;
                break;
            case 1:
                matchActionClass = MatchAction1.class;
                break;
            case 2:
                matchActionClass = MatchAction2.class;
                break;
            default:
                throw new RuntimeException("Too many parameters");
        }
        return matchActionClass;
    }

    private TypeSpec generateMemberClass(UnionClass unionClass, UnionClassMember member) {
        final String parameterName = unionClass.getParameterName();
        final MethodSpec isMatchForMethod = MethodSpec.methodBuilder("isMatchFor")
              .addParameter(ParameterSpec.builder(unionClass.getClassName(), parameterName).build())
              .addModifiers(STATIC)
              .returns(TypeName.BOOLEAN)
              .addStatement(String.format("return %s instanceof %s", parameterName, member.simpleName))
              .build();

        final TypeSpec.Builder builder = TypeSpec.classBuilder(member.simpleName)
              .addModifiers(PRIVATE, STATIC)
              .superclass(unionClass.getClassName());

        if (member.valueTypes.size() > 0) {
            int index = 0;
            for (TypeName typeName : member.valueTypes) {
                String fieldName = "value" + (index + 1);
                builder.addField(typeName, fieldName, PRIVATE, FINAL);
                index++;
            }
            final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
            index = 0;
            for (TypeName typeName : member.valueTypes) {
                String fieldName = "value" + (index + 1);
                constructorBuilder.addParameter(typeName, fieldName);
                constructorBuilder.addStatement(String.format("this.%s = %s", fieldName, fieldName));
                index++;
            }
            builder.addMethod(constructorBuilder.build());

            index = 0;
            for (TypeName typeName : member.valueTypes) {
                String fieldName = "value" + (index + 1);
                final MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(getValueGetterName(fieldName))
                      .returns(typeName)
                      .addModifiers(STATIC)
                      .addParameter(unionClass.getClassName(), unionClass.getParameterName())
                      .addStatement(String.format("return ((%s) %s).%s",
                                                  member.simpleName,
                                                  unionClass.getParameterName(),
                                                  fieldName));
                builder.addMethod(getterBuilder.build());
                index++;
            }
        }
        builder.addMethod(isMatchForMethod);
        return builder.build();
    }

    private String getValueGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
