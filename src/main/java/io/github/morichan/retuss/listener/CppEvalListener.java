package io.github.morichan.retuss.listener;

import io.github.morichan.retuss.language.cpp.*;
import io.github.morichan.retuss.language.cpp.Class;
import io.github.morichan.retuss.parser.cpp.CPP14Parser;
import io.github.morichan.retuss.parser.cpp.CPP14BaseListener;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.List;


public class CppEvalListener extends CPP14BaseListener {
    private Cpp cpp = new Cpp();
    //  private String classname; //
//    private CPP14Parser.ClassnameContext classnameContext = null;
  //  private List<CPP14Parser.ClassnameContext> classnames = new ArrayList<>();

    //最初に探索をはじめた時の初期の構文で必要なものを準備
  //  private MemberVariable memberVariable=new MemberVariable();
    private AccessSpecifier accessSpecifier=null;
    boolean isAlreadySearchedAccessSpecifier = false;
    boolean functiondefinitionFlag=false;

    @Override
    public void enterTranslationunit(CPP14Parser.TranslationunitContext ctx) {
    }

    @Override
    public void enterTypespecifier(CPP14Parser.TypespecifierContext ctx) {
    }


    @Override
    public void enterMemberspecification(CPP14Parser.MemberspecificationContext ctx) {
     //   accessSpecifier = null;


        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof CPP14Parser.AccessspecifierContext) {
                accessSpecifier=accessSpecifier.choose(ctx.getChild(i).getChild(0).getText());
            }
        }

//        cpp.addClass(cppClass);
    }

    @Override public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        MemberVariable memberVariable = new MemberVariable();


        if (accessSpecifier != null) {
            memberVariable.setAccessSpecifier(accessSpecifier);
           // accessSpecifier = null;
        }

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof CPP14Parser.DeclspecifierseqContext) {
                memberVariable.setType(new Type(ctx.getChild(i).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText()));
            }
            if (ctx.getChild(i) instanceof CPP14Parser.MemberdeclaratorlistContext) {
                memberVariable.setName(ctx.getChild(i).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText());
            }
        }
        cpp.getClasses().get(cpp.getClasses().size()-1).addMemberVariable(memberVariable);
    }

    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        functiondefinitionFlag=true;
        MemberFunction memberFunction = new MemberFunction();

        if (accessSpecifier != null) {
            memberFunction.setAccessSpecifier(accessSpecifier);
            // accessSpecifier = null;
        }

        if (ctx.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0) instanceof CPP14Parser.SimpletypespecifierContext) {
        memberFunction.setType(new Type(ctx.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText()));
        }

        if (ctx.getChild(1).getChild(0).getChild(0) instanceof CPP14Parser.NoptrdeclaratorContext) {
            memberFunction.setName(ctx.getChild(1).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText());
        }


        cpp.getClasses().get(cpp.getClasses().size()-1).addMemberFunction(memberFunction);

    }

    @Override public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) { functiondefinitionFlag=true; }

    @Override
    public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
//        if (! (ctx.getParent().getParent().getParent().getParent().getParent().getParent().getParent() instanceof CPP14Parser.FunctiondefinitionContext)) return;
        if (functiondefinitionFlag == true) {
            Argument argument = new Argument();

            if (ctx.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0) instanceof CPP14Parser.SimpletypespecifierContext) {
                argument.setType(new Type(ctx.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText()));
            }

            for (int i = 0; i < ctx.getChildCount(); i++) {
                if (ctx.getChild(i) instanceof CPP14Parser.DeclaratorContext) {
                    argument.setName(ctx.getChild(i).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getChild(0).getText());
                }
            }

            cpp.getClasses().get(cpp.getClasses().size() - 1).getMemberFunctions()
                    .get(cpp.getClasses().get(cpp.getClasses().size() - 1).getMemberFunctions().size() - 1).addArgument(argument);

        }

    }
//    @Override
//    public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
//        if (! (ctx.getParent().getParent().getParent().getParent() instanceof CPP14Parser.FunctiondefinitionContext)) return;
//
//        Argument argument = new Argument();
//
//    }

    private Class searchExtendsClass(CPP14Parser.ClassheadContext ctx) {

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof CPP14Parser.BaseclauseContext) {
//                return new Class(ctx.getChild(i).getChild(1).getChild(0).getChild(1).getChild(0).getChild(0).getText());
                for (int j = 0; j < ctx.getChild(i).getChildCount(); j++) {
                    if (ctx.getChild(i).getChild(j) instanceof CPP14Parser.BasespecifierlistContext) {
                        for (int k = 0; k < ctx.getChild(i).getChild(j).getChildCount(); k++) {
                            if (ctx.getChild(i).getChild(j).getChild(k) instanceof CPP14Parser.BasespecifierContext) {
                                for (int l = 0; l < ctx.getChild(i).getChild(j).getChild(k).getChildCount(); l++) {
                                    if (ctx.getChild(i).getChild(j).getChild(k).getChild(l) instanceof CPP14Parser.BasetypespecifierContext) {
                                        return new Class(ctx.getChild(i).getChild(j).getChild(k).getChild(l).getChild(0).getChild(0).getChild(0).getText());
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }

        }


        return null;
    }


    @Override
    public void enterClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
        Class cppClass = new Class();

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof CPP14Parser.ClassheadContext) {
                cppClass.setName(ctx.getChild(i).getChild(1).getChild(0).getChild(0).getText());
                cppClass.setExtendsClass(searchExtendsClass((CPP14Parser.ClassheadContext) ctx.getChild(i)));
            }
        }

        cpp.addClass(cppClass);
    }





    /**
     * 継承クラスの構文解析中に実行されるメソッド
     */
    @Override
    public void enterClassordecltype(CPP14Parser.ClassordecltypeContext ctx) {
//        Class cppClass = new Class();
//        for (int i = 0; i < ctx.getChildCount(); i++) {
//            if (ctx.getChild(i) instanceof CPP14Parser.ClassnameContext) {
//                    cppClass.setExtendsClass(searchExtendsClass((CPP14Parser.ClassnameContext) ctx.getChild(i)));
//            }
//        }
//        cpp.addClass(cppClass);
    }


    //enterClassname で処理を実装すると、継承のクラスの構文解析中にも発生するので推奨しない。
//    @Override public void enterClassname(CPP14Parser.ClassnameContext ctx) {
//        Class cppClass = new Class();
//
//        for (int i = 0; i < ctx.getChildCount(); i++) {
//            if (ctx.getChild(i) instanceof TerminalNodeImpl) {
//                cppClass.setName(ctx.getChild(i).getText());
//           //     cppClass.setExtendsClass(searchExtendsClass((JavaParser.ClassDeclarationContext) ctx.getChild(i)));
//            }
//        }
//        cpp.addClass(cppClass);
//    }


    public Cpp getCpp() {
        return cpp;
    }


//    public String getText() {
//        return className;
//    }
}
