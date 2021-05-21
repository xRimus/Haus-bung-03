package h03;

import h03.Utils.DefinitionCheck;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.*;
import java.util.List;
import java.util.stream.Collectors;

import static h03.Utils.definitionCorrect;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
class UnicodeNumberOfCharIndexTest {

    private static Class<?> unicodeNumberOfCharIndexClass;
    private static Method sizeOfAlphabet, apply;
    private static Object instance;

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        try {
            assumeTrue(definitionCorrect(FunctionToIntTest.class),
                    "PartialMatchLengthUpdateValuesTest requires that interface FunctionToInt is implemented correctly");

            unicodeNumberOfCharIndexClass = Class.forName("h03.UnicodeNumberOfCharIndex");

            // is not generic
            assertEquals(0, unicodeNumberOfCharIndexClass.getTypeParameters().length, "UnicodeNumberOfCharIndex is generic");

            // implements FunctionToInt
            assertTrue(() -> {
                for (Type intf : unicodeNumberOfCharIndexClass.getGenericInterfaces())
                    if (intf.getTypeName().equals("h03.FunctionToInt<java.lang.Character>"))
                        return true;

                return false;
            }, "UnicodeNumberOfCharIndex does not implement interface FunctionToInt correctly");

            // is not abstract
            assertFalse(isAbstract(unicodeNumberOfCharIndexClass.getModifiers()), "UnicodeNumberOfCharIndex must not be abstract");

            // get instance
            try {
                instance = unicodeNumberOfCharIndexClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                assumeTrue(false, "UnicodeNumberOfCharIndex needs to have an empty constructor for these tests to work");
            }

            // methods
            sizeOfAlphabet = unicodeNumberOfCharIndexClass.getDeclaredMethod("sizeOfAlphabet");
            apply = unicodeNumberOfCharIndexClass.getDeclaredMethod("apply", Character.class);
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        }
    }

    @Test
    public void testSizeOfAlphabet() throws ReflectiveOperationException {
        assertEquals(65536, sizeOfAlphabet.invoke(instance), "sizeOfAlphabet() in UnicodeNumberOfCharIndex did not return expected value");
    }

    @ParameterizedTest
    @ArgumentsSource(RandomCharProvider.class)
    public void testApply(int i, char c) throws ReflectiveOperationException {
        assertEquals(i, apply.invoke(instance, c), "apply(Character) in UnicodeNumberOfCharIndex did not return expected value");
    }
}

@DefinitionCheck("checkClass")
class SelectionOfCharsIndexTest {

    private static Class<?> selectionOfCharsIndexClass;
    private static Method sizeOfAlphabet, apply;
    private static Field theChars;
    private static Object instance;

    private static final List<Character> charList = new RandomCharProvider()
            .provideArguments(null)
            .map(arguments -> (Character) arguments.get()[1])
            .collect(Collectors.toList());

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        try {
            assumeTrue(definitionCorrect(FunctionToIntTest.class),
                    "PartialMatchLengthUpdateValuesTest requires that interface FunctionToInt is implemented correctly");

            selectionOfCharsIndexClass = Class.forName("h03.SelectionOfCharsIndex");

            // is not generic
            assertEquals(0, selectionOfCharsIndexClass.getTypeParameters().length, "SelectionOfCharsIndex is generic");

            // implements FunctionToInt
            assertTrue(() -> {
                for (Type intf : selectionOfCharsIndexClass.getGenericInterfaces())
                    if (intf.getTypeName().equals("h03.FunctionToInt<java.lang.Character>"))
                        return true;

                return false;
            }, "SelectionOfCharsIndex does not implement interface FunctionToInt correctly");

            // is not abstract
            assertFalse(isAbstract(selectionOfCharsIndexClass.getModifiers()), "SelectionOfCharsIndex must not be abstract");

            // fields
            try {
                theChars = selectionOfCharsIndexClass.getDeclaredField("theChars");
            } catch (NoSuchFieldException e) {
                fail("SelectionOfCharsIndex does not have field \"theChars\"", e);
            }

            assertTrue(isPrivate(theChars.getModifiers()), "Field \"theChars\" in SelectionOfCharsIndex is not private");
            assertFalse(isStatic(theChars.getModifiers()), "Field \"theChars\" in SelectionOfCharsIndex is static"); // implied in assignment
            assertEquals(char[].class, theChars.getType(), "Field \"theChars\" in SelectionOfCharsIndex has incorrect type");

            theChars.setAccessible(true);

            // get instance
            try {
                Constructor<?> constructor = selectionOfCharsIndexClass.getDeclaredConstructor(List.class);

                assertEquals(
                        "java.util.List<java.lang.Character>",
                        constructor.getGenericParameterTypes()[0].getTypeName(),
                        "Parameter of Constructor has incorrect type"
                );

                instance = constructor.newInstance(charList);
            } catch (NoSuchMethodException e) {
                assumeTrue(false, "SelectionOfCharsIndex is missing a required constructor");
            }

            // fields after instantiation
            char[] chars = (char[]) theChars.get(instance);

            for (int i = 0; i < chars.length; i++)
                assertEquals(charList.get(i), chars[i],
                        "Field \"theChars\" in SelectionOfCharsIndex does not have expected value '" + charList.get(i) + "' at index " + i);

            // methods
            sizeOfAlphabet = selectionOfCharsIndexClass.getDeclaredMethod("sizeOfAlphabet");
            apply = selectionOfCharsIndexClass.getDeclaredMethod("apply", Character.class);
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        }
    }

    @Test
    public void testSizeOfAlphabet() throws ReflectiveOperationException {
        assertEquals(((char[]) theChars.get(instance)).length, sizeOfAlphabet.invoke(instance), "sizeOfAlphabet() in SelectionOfCharsIndex did not return expected value");
    }

    @ParameterizedTest
    @ArgumentsSource(RandomCharProvider.class)
    public void testApply(@SuppressWarnings("unused") int i, char c) throws ReflectiveOperationException {
        assertEquals(charList.indexOf(c), apply.invoke(instance, c), "apply(Character) in SelectionOfCharsIndex did not return expected value");
    }
}

@DefinitionCheck("checkClass")
class EnumIndexTest {

    private static Class<?> enumIndexClass;
    private static Method sizeOfAlphabet, apply;
    private static Object instance;
    private static ConstructorType constructorType;

    private enum ConstructorType {EMPTY, CLASS}
    private enum TestEnum {TEST, ENUM, HELLO, WORLD}

    @BeforeAll
    public static void checkClass() throws ReflectiveOperationException {
        try {
            assumeTrue(definitionCorrect(FunctionToIntTest.class),
                    "PartialMatchLengthUpdateValuesTest requires that interface FunctionToInt is implemented correctly");

            enumIndexClass = Class.forName("h03.EnumIndex");

            // is generic
            TypeVariable<?>[] typeParameters = enumIndexClass.getTypeParameters();

            assertEquals(1, typeParameters.length, "EnumIndex is not generic");
            assertEquals("T", typeParameters[0].getName(), "Type parameter of EnumIndex is not named 'T'");
            assertEquals("java.lang.Enum<T>", typeParameters[0].getBounds()[0].getTypeName(),
                    "Type parameter of EnumIndex is not restricted to subtypes of Enum<T>");

            // implements FunctionToInt
            assertTrue(() -> {
                for (Type intf : enumIndexClass.getGenericInterfaces())
                    if (intf.getTypeName().equals("h03.FunctionToInt<T>"))
                        return true;

                return false;
            }, "EnumIndex does not implement interface FunctionToInt correctly");

            // is not abstract
            assertFalse(isAbstract(enumIndexClass.getModifiers()), "EnumIndex must not be abstract");

            // get instance
            for (Constructor<?> constructor : enumIndexClass.getDeclaredConstructors()){
                if (constructor.getGenericParameterTypes().length == 1 &&
                        constructor.getGenericParameterTypes()[0].getTypeName().equals("java.lang.Class<T>"))
                    constructorType = ConstructorType.CLASS;
                else if (constructor.getGenericParameterTypes().length == 0)
                    constructorType = ConstructorType.EMPTY;

                if (constructorType != null)
                    break;
            }

            if (constructorType == null)
                assumeTrue(false, "Class does not have a supported constructor. " +
                        "EnumIndex must either have an empty one or one with parameter type \"Class<T>\"");
            else if (constructorType == ConstructorType.EMPTY)
                instance = enumIndexClass.getDeclaredConstructor().newInstance();
            else if (constructorType == ConstructorType.CLASS)
                instance = enumIndexClass.getDeclaredConstructor(Class.class).newInstance(TestEnum.class);

            // methods
            sizeOfAlphabet = enumIndexClass.getDeclaredMethod("sizeOfAlphabet");
            apply = enumIndexClass.getDeclaredMethod("apply", Enum.class);
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        }
    }

    @Test
    public void testSizeOfAlphabet() throws ReflectiveOperationException {
        assertEquals(TestEnum.values().length, sizeOfAlphabet.invoke(instance), "sizeOfAlphabet() in EnumIndex did not return expected value");
    }

    @ParameterizedTest
    @EnumSource(TestEnum.class)
    public void testApply(TestEnum testEnum) throws ReflectiveOperationException {
        assertEquals(List.of(TestEnum.values()).indexOf(testEnum), apply.invoke(instance, testEnum), "apply(Enum) in EnumIndex did not return expected value");
    }
}
