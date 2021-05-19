package h03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import h03.Utils;
import h03.Utils.DefinitionCheck;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static h03.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
class PartialMatchLengthUpdateValuesTest {

    static Class<?> partialMatchLengthUpdateValuesClass;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(FunctionToIntTest.class),
                "PartialMatchLengthUpdateValuesTest requires that interface FunctionToInt is implemented correctly");

        partialMatchLengthUpdateValuesClass = getClassForName("h03.PartialMatchLengthUpdateValues");

        // is generic
        assertEquals(1, partialMatchLengthUpdateValuesClass.getTypeParameters().length,
                "PartialMatchLengthUpdateValues must be generic");
        assertEquals("T", partialMatchLengthUpdateValuesClass.getTypeParameters()[0].getName(),
                "Type parameter of PartialMatchLengthUpdateValues is not named 'T'");

        // is abstract
        assertTrue(isAbstract(partialMatchLengthUpdateValuesClass.getModifiers()), "PartialMatchLengthUpdateValues must be abstract");

        // constructor
        try {
            Constructor<?> constructor = partialMatchLengthUpdateValuesClass.getDeclaredConstructor(FunctionToIntTest.functionToIntIntf);

            assertEquals(
                    "h03.FunctionToInt<T>",
                    constructor.getGenericParameterTypes()[0].getTypeName(),
                    "Parameter of Constructor in PartialMatchLengthUpdateValues has incorrect type"
            );
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValues is missing a required constructor", e);
        }

        // fields
        boolean fieldFound = false;
        Field[] fields = partialMatchLengthUpdateValuesClass.getDeclaredFields();

        for (int i = 0; i < fields.length && !fieldFound; i++) {
            Field field = fields[i];

            fieldFound = field.getGenericType().getTypeName().equals("h03.FunctionToInt<T>") &&
                         isProtected(field.getModifiers()) &&
                         !isStatic(field.getModifiers()); // implied in assignment
        }

        assertTrue(fieldFound, "No field fulfilling the criteria specified in the assignment found in PartialMatchLengthUpdateValues");

        // methods
        try {
            Method getPartialMatchLengthUpdate =
                    partialMatchLengthUpdateValuesClass.getDeclaredMethod("getPartialMatchLengthUpdate", int.class, Object.class);

            assertTrue(isPublic(getPartialMatchLengthUpdate.getModifiers()),
                    "getPartialMatchLengthUpdate(T) in PartialMatchLengthUpdateValues must be public");
            assertTrue(isAbstract(getPartialMatchLengthUpdate.getModifiers()),
                    "getPartialMatchLengthUpdate(T) in PartialMatchLengthUpdateValues must be abstract");
            assertEquals(int.class, getPartialMatchLengthUpdate.getReturnType(),
                    "Return type of getPartialMatchLengthUpdate(T) in PartialMatchLengthUpdateValues must be int");

            Method computePartialMatchLengthUpdateValues =
                    partialMatchLengthUpdateValuesClass.getDeclaredMethod("computePartialMatchLengthUpdateValues", Object[].class);

            assertTrue(isProtected(computePartialMatchLengthUpdateValues.getModifiers()),
                    "computePartialMatchLengthUpdateValues(Enum) in PartialMatchLengthUpdateValues must be protected");
            assertFalse(isAbstract(computePartialMatchLengthUpdateValues.getModifiers()),
                    "computePartialMatchLengthUpdateValues(Enum) in PartialMatchLengthUpdateValues must not be abstract");
            assertEquals(int.class, computePartialMatchLengthUpdateValues.getReturnType(),
                    "Return type of computePartialMatchLengthUpdateValues(Enum) in PartialMatchLengthUpdateValues must be int");
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValues is missing a required method", e);
        }
    }

    @Test
    public void classDefinitionCorrect() {}
}
