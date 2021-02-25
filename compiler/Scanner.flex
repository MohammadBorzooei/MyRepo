import java.io.IOException;
import java_cup.runtime.*;
/*user codes */
%%
/*options and decleration */
%class Scanner
%line
%column
%unicode
%function next_token
%type MySymbol

%{
private boolean flag = false;
private HashSet<String> records = new HashSet<String>();
    private MySymbol symbol(String token)
    {
    System.err.println("Obtain token " + token + " \"" + yytext() + "\"" );
    return new MySymbol(token, yytext());
    }
    private MySymbol symbol(String token, Object val) {
    System.err.println( "Obtain token " + token + " \"" + yytext() + "\"" );
    return new MySymbol(token, val);
    }
    StringBuilder string = new StringBuilder();
%}

    /* VARIABLES */
    id = {letter}({letter}|{digit}|"_")*
    letter = [A-Za-z]

    /* INTEGER NUMBERS */
    IntLiteral  = ("-")?{Dec_numbers}
    Dec_numbers = (0 | {digitWithoutZero}{digit}*)("l" | "L")?
    HexLiteral  = ("-")?{Hex_numbers}
    Hex_numbers = 0x[\da-fA-F]{1, 4}

    /* WHITESPACE */
    LineTerminator = \r|\n|\r\n
    WhiteSpace = {LineTerminator} | [ \t\f]

    StringCharacter = [^\t\r\n\"\'\\]

    SpecialCharacter = \\ ([trn\"\'\\])

    SingleCommentCharacter = [^\n]

    MultilpleCommentCharacter = [^\t\r\n#]


    /* define states */
    %state CHARACTER
    %state STRING
    %state SINGLE_COMMENT
    %state MULT_COMMENT

    %%

    /*lexical rules*/

    <YYINITIAL> {

        /* CHARACTERS */
        "'"      { yybegin(CHARACTER); string.setLength(0); string.append("'");}

        /* STRINGS */
        "\"      { yybegin(STRING); string.setLength(0); string.append("\");}

        /* SYMBOLS */
                "=="    { return symbol("==");}
                "!="    { return symbol("!=");}
                "<="    { return symbol("<=");}
                "<"     { return symbol("<");}
                ">"     { return symbol(">");}
                ">="    { return symbol(">=");}
                "="     { return symbol("=");}
                "~"     { return symbol("tilda");}
                "&"     { return symbol("&");}
                "|"     { return symbol("|");}
                "^"     { return symbol("^");}
                "and"   { return symbol("and");}
                "or"    { return symbol("or");}
                "not"   { return symbol("not");}
                "*"     { return symbol("*");}
                "+"     { return symbol("+");}
                "+="    { return symbol("+=");}
                "%="    { return symbol("%=");}
                "-="    { return symbol("-=");}
                "*="    { return symbol("*=");}
                "/="    { return symbol("/=");}
                "/"     { return symbol("/");}
                "%"     { return symbol("%");}
                "begin" { return symbol("begin");}
                "end"   { return symbol("end");}
                "("     { return symbol("(");}
                ")"     { return symbol(")");}
                "."     { return symbol(".");}
                ","     { return symbol("comma");}
                ":"     { return symbol(":");}
                ";"     { return symbol(";");}
                "["     { return symbol("[");}
                "]"     { return symbol("]");}
                "]["     { return symbol("missused brackets");}
                "++"    { return symbol("++");}
                "--"    { return symbol("--");}
                "-"     { return symbol("-");}


        /* KEYWORDS */

        "record"      {flag = true; return symbol("record");}
        "class"       { return symbol("class");}
        "this"       { return symbol("const");}
        "extends"       { return symbol("extends");}
        "implements"       { return symbol("implements");}
        "protected"       { return symbol("protected");}
        "public"       { return symbol("public");}
        "private"       { return symbol("private");}
        "interface"       { return symbol("interface");}
        "function"    { return symbol("function");}
        "void"        { return symbol("void");}
        "null"       { return symbol("null");}
        "const"       { return symbol("const");}
        "auto"        { return symbol("auto");}
        "return"      { return symbol("return");}
        "break"       { return symbol("break");}
        "continue"    { return symbol("continue");}
        "len"         { return symbol("len");}
        "if"          { return symbol("if");}
        "else"        { return symbol("else");}
        "switch"      { return symbol("switch");}
        "of"          { return symbol("of");}
        "case"        { return symbol("case");}
        "default"     { return symbol("default");}
        "for"         { return symbol("for");}
        "in"          { return symbol("in");}
        "int"         { return symbol("base_type");}
        "long"        { return symbol("base_type");}
        "double"      { return symbol("base_type");}
        "float"       { return symbol("base_type");}
        "char"        { return symbol("base_type");}
        "string"      { return symbol("base_type");}
        "bool"        { return symbol("base_type");}
        "new"         { return symbol("new");}
        "println"     { return symbol("println");}
        "true"        { return symbol("true", Boolean.valueOf(yytext()));}
        "false"       { return symbol("false", Boolean.valueOf(yytext()));}

        /* COMMENTS */
                "##"    { yybegin(SINGLE_COMMENT); string.setLength(0); string.append("##");}

                "/#"    {yybegin(MULT_COMMENT); string.setLength(0); string.append("/#");}


        /* WHITESPACES */
        {WhiteSpace}        {/* skip */}


        /* VARIABLES */

        {id}      {   String temp = yytext(); if(records.contains(temp)){flag = false; return symbol("rec_id",temp);} if(flag){flag = false;records.add(temp);return symbol("rec_id",temp);} return symbol("id",temp);}

        /* NUMBERS */
        {IntLiteral}        {return symbol("int_const", Integer.valueOf(yytext()));}
        {HexLiteral}        {return symbol("int_const", yytext());}
        {RealLiteral}       {return symbol("real_const", Double.valueOf(yytext()));}
        {ScientificLiteral} {return symbol("real_const", yytext());}

       } //in male kojas


        <CHARACTER>{
            "'"                 {yybegin(YYINITIAL);}
            {StringCharacter}   {return symbol("char", yytext().charAt(0));}
            {SpecialCharacter}  {return symbol("char" ,yytext().charAt(0));}
            }

            <STRING>{
                \"                  {yybegin(YYINITIAL); string.append("\""); StringBuilder temp = string; string = new StringBuilder(); return symbol("string", temp.toString());}
                {StringCharacter}+  {string.append(yytext());}
                {SpecialCharacter}+ {string.append(yytext());}
                }

                <SINGLE_COMMENT>{
                    \n                              {yybegin(YYINITIAL);}
                    {SingleCommentCharacter}+       {}
                    }

                    <MULT_COMMENT>{
                        "#/"                            {yybegin(YYINITIAL);}
                        "#"                             {}
                        \n                              {}
                        \r\n                            {}
                        "	"                           {}
                        {MultilpleCommentCharacter}+    {}
                        }

                        [^]        { throw new RuntimeException("Illegal character \""+yytext()+
                        "\" at line "+yyline+", column "+yycolumn); }

                        //<<EOF>>    {return symbol("$");}

                            /*After creating the Scanner file with flex
                            Do the following:
                            **set the package name
                            **import HashSet<>
                            **make the class and it's constructor public
                            **implement it from Lexical
                            **add 'private MySymbol currentSymbol = null;' to the class
                            **add the @Override available at the end of this comment

                            @Override
                            public MySymbol currentToken() {
                            return currentSymbol;
                            }'
                            methods
                            */