# MyRepo
# Complete Compiler for DECaf Language

This repository contains the compiler designed following the language specifications provided in _Language ProjcetDcsp.pdf_ , as part of the course Compiler Construction at BUT.

The compiler parses input files according to the language specifications, and if the input is syntactically and semantically correct, it is capable of generating corresponding assembly-level code.
 
<br/>



### To test the Scanner on a testcase
1. jflex Scanner.flex
2.  javac Main.java
3.  java Main.java testcase.txt

### Stages of compiling a testcase (Option 10)
                                              
 |    Stage    |     Details  |
| :------------- | :----------: |
|  Lexical Analysis | Categorizes the contents of the input file as tokens (as per the language specifications).|
| Syntax Analysis   | Creates a Parse Tree for the tokens being returned by the lexer. If an error is encountered in any of the above 2 stages, the compiler does not proceed to semantic analsysis.|
| Semantic Analysis |Creates an Abstract Syntax Tree for the coresponding Parse Tree and populates the Symbol Table. If an error is encountered during Semantic Analysis, the compiler does not proceed to code generation |
| Code generation | Generates corresponding assembly-level code. As per the project requirements, code generation is restricted to only those testcases which have a single function (main) and handle only integers.|

## Author

* [Mohammad Borzooei](https://github.com/MohammadBorzooei)