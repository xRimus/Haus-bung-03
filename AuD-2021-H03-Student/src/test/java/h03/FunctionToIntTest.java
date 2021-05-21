package h03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static h03.Utils.getClassForName;
import static h03.Utils.DefinitionCheck;
import static org.junit.jupiter.api.Assertions.*;

@DefinitionCheck("checkInterface")
public class FunctionToIntTest {

    static Class<?> functionToIntIntf;
    static Method sizeOfAlphabet, apply;

    @BeforeAll
    public static void checkInterface() {
        functionToIntIntf = getClassForName("h03.FunctionToInt");

        // is interface
        assertTrue(functionToIntIntf.isInterface(), "FunctionToInt is not an interface");

        // is generic
        assertEquals(1, functionToIntIntf.getTypeParameters().length, "FunctionToInt is not generic");
        assertEquals("T", functionToIntIntf.getTypeParameters()[0].getName(), "Type parameter of FunctionToInt is not named 'T'");

        // methods
        try {
            sizeOfAlphabet = functionToIntIntf.getDeclaredMethod("sizeOfAlphabet");

            assertTrue(Modifier.isPublic(sizeOfAlphabet.getModifiers()), "Method sizeOfAlphabet() in FunctionToInt is not public (implicit or explicit)");
            assertFalse(Modifier.isStatic(sizeOfAlphabet.getModifiers()), "Method sizeOfAlphabet() in FunctionToInt is static");
            assertFalse(sizeOfAlphabet.isDefault(), "Method sizeOfAlphabet() in FunctionToInt is default");

            apply = functionToIntIntf.getDeclaredMethod("apply", Object.class);

            assertTrue(Modifier.isPublic(apply.getModifiers()), "Method apply(T) is not public (implicit or explicit)");
            assertFalse(Modifier.isStatic(apply.getModifiers()), "Method apply(T) is static");
            assertFalse(apply.isDefault(), "Method apply(T) is default");
            assertEquals(1, apply.getExceptionTypes().length, "Method apply(T) does not throw any exceptions");
            assertEquals(IllegalArgumentException.class, apply.getExceptionTypes()[0], "Method apply(T) does not throw IllegalArgumentException");
        } catch (NoSuchMethodException e) {
            fail("Interface FunctionToInt is missing a required method", e);
        }
    }

    @Test
    public void interfaceDefinitionCorrect() {}
}
