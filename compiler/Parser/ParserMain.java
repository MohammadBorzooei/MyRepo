package Parser;
import java.io.IOException;
import java.io.FileOutputStream;
import Parser.LexicalAnalyzer;
import Semantic.CodeGenerator;
import Syntax.Parser;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ParserMain {

        public static void main(String[] args) throws IOException {
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(new FileReader("src/Code.txt"));
            CodeGenerator codeGenerator = new CodeGenerator(lexicalAnalyzer);
            Parser parser = new Parser(lexicalAnalyzer, codeGenerator, "src/Syntax/table.npt", false);
            AST result;
            try {
                parser.parse();
                result = codeGenerator.getResult();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "Main",
                    null, "java/lang/Object", null);
            MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC,
                    "main", "([Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            result.codegen(classWriter, methodVisitor);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
            try (FileOutputStream fos = new FileOutputStream("Main.class")) {
                fos.write(classWriter.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Code compiled successfully");

        }
    }

