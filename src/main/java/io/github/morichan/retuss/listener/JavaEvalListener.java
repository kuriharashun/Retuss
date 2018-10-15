package io.github.morichan.retuss.listener;

import io.github.morichan.retuss.language.java.*;
import io.github.morichan.retuss.language.java.Class;
import io.github.morichan.retuss.parser.java.JavaParser;
import io.github.morichan.retuss.parser.java.JavaParserBaseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Javaソースコードのパーサを利用したコンテキストの抽出クラス
 *
 * ANTLRに依存する。
 */
public class JavaEvalListener extends JavaParserBaseListener {
    private JavaParser.PackageDeclarationContext packageDeclaration = null;
    private List<JavaParser.ImportDeclarationContext> importDeclarations = new ArrayList<>();
    private List<JavaParser.TypeDeclarationContext> typeDeclarations = new ArrayList<>();
    private Java java = new Java();
    private AccessModifier accessModifier = null;

    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof JavaParser.PackageDeclarationContext) {
                packageDeclaration = (JavaParser.PackageDeclarationContext) ctx.getChild(i);

            } else if (ctx.getChild(i) instanceof JavaParser.ImportDeclarationContext) {
                importDeclarations.add((JavaParser.ImportDeclarationContext) ctx.getChild(i));

            } else if (ctx.getChild(i) instanceof JavaParser.TypeDeclarationContext) {
                typeDeclarations.add((JavaParser.TypeDeclarationContext) ctx.getChild(i));
            }
        }
    }

    @Override
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        Class javaClass = new Class();

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof JavaParser.ClassDeclarationContext) {
                javaClass.setName(ctx.getChild(i).getChild(1).getText());
                javaClass.setExtendsClass(searchExtendsClass((JavaParser.ClassDeclarationContext) ctx.getChild(i)));
            }
        }

        java.addClass(javaClass);
    }

    @Override
    public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        accessModifier = null;
        boolean isAlreadySearchedAccessModifier = false;

        if (ctx.getChildCount() >= 2 && ctx.getChild(ctx.getChildCount() - 1) instanceof JavaParser.MemberDeclarationContext) {
            for (int i = 0; i < ctx.getChildCount() - 1; i++) {
                if (ctx.getChild(i).getChild(0) instanceof JavaParser.ClassOrInterfaceModifierContext) {
                    try {
                        accessModifier = AccessModifier.choose(ctx.getChild(i).getChild(0).getChild(0).getText());
                        isAlreadySearchedAccessModifier = true;
                        break;
                    } catch (IllegalArgumentException e) {
                        // static, abstract, final, strictfp or Annotation
                    }
                }
            }
            if (!isAlreadySearchedAccessModifier) {
                accessModifier = AccessModifier.Package;
            }
        } else if (ctx.getChild(0) instanceof JavaParser.MemberDeclarationContext) {
            accessModifier = AccessModifier.Package;
        }
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        Field field = new Field();

        if (accessModifier != null) {
            field.setAccessModifier(accessModifier);
            accessModifier = null;
        }

        if (ctx.getChild(0).getChild(0) instanceof JavaParser.AnnotationContext) {
            field.setType(new Type(ctx.getChild(0).getChild(1).getText()));
        } else {
            field.setType(new Type(ctx.getChild(0).getChild(0).getText()));
        }

        field.setName(ctx.getChild(1).getChild(0).getChild(0).getText());

        java.getClasses().get(java.getClasses().size() - 1).addField(field);
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        Method method = new Method();

        if (accessModifier != null) {
            method.setAccessModifier(accessModifier);
            accessModifier = null;
        }

        if (ctx.getChild(0).getChild(0) instanceof JavaParser.TypeTypeContext) {
            if (ctx.getChild(0).getChild(0).getChild(0) instanceof JavaParser.AnnotationContext) {
                method.setType(new Type(ctx.getChild(0).getChild(0).getChild(1).getText()));
            } else {
                method.setType(new Type(ctx.getChild(0).getChild(0).getChild(0).getText()));
            }
        } else {
            method.setType(new Type(ctx.getChild(0).getChild(0).getText()));
        }

        method.setName(ctx.getChild(1).getText());

        java.getClasses().get(java.getClasses().size() - 1).addMethod(method);
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        if (! (ctx.getParent().getParent().getParent() instanceof JavaParser.MethodDeclarationContext)) return;

        Argument argument = new Argument();

        if (ctx.getChild(0).getChild(0) instanceof JavaParser.AnnotationContext) {
            argument.setType(new Type(ctx.getChild(0).getChild(1).getText()));
        } else {
            argument.setType(new Type(ctx.getChild(0).getChild(0).getText()));
        }

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof JavaParser.VariableDeclaratorIdContext) {
                argument.setName(ctx.getChild(i).getChild(0).getText());
            }
        }

        java.getClasses().get(java.getClasses().size() - 1).getMethods()
                .get(java.getClasses().get(java.getClasses().size() - 1).getMethods().size() - 1).addArgument(argument);
    }

    private Class searchExtendsClass(JavaParser.ClassDeclarationContext ctx) {

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof JavaParser.TypeTypeContext) {
                return new Class(ctx.getChild(i).getText());
            }
        }

        return null;
    }

    public JavaParser.PackageDeclarationContext getPackageDeclaration() throws NullPointerException {
        return packageDeclaration;
    }

    public List<JavaParser.ImportDeclarationContext> getImportDeclarations() {
        return importDeclarations;
    }

    public List<JavaParser.TypeDeclarationContext> getTypeDeclarations() {
        return typeDeclarations;
    }

    public Java getJava() {
        return java;
    }
}
