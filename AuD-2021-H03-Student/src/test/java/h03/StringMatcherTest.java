package h03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static h03.Utils.*;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPrivate;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class StringMatcherTest {

    static Constructor<?> constructor;
    static Method findAllMatches;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesAsMatrixTest.class),
                "StringMatcherTest requires that class PartialMatchLengthUpdateValuesAsMatrix is implemented correctly");
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesAsAutomatonTest.class),
                "StringMatcherTest requires that class PartialMatchLengthUpdateValuesAsAutomaton is implemented correctly");

        Class<?> stringMatcherClass = getClassForName("h03.StringMatcher");

        // is generic
        assertEquals(1, stringMatcherClass.getTypeParameters().length,
                "StringMatcher must be generic");
        assertEquals("T", stringMatcherClass.getTypeParameters()[0].getName(),
                "Type parameter of class StringMatcher is not named 'T'");

        // is not abstract
        assertFalse(isAbstract(stringMatcherClass.getModifiers()), "StringMatcher must not be abstract");

        // constructor
        try {
            constructor = stringMatcherClass.getDeclaredConstructor(PartialMatchLengthUpdateValuesTest.partialMatchLengthUpdateValuesClass);
        } catch (NoSuchMethodException e) {
            fail("StringMatcher is missing a required constructor", e);
        }

        // fields
        Field partialMatchLengthUpdateValues = null;

        for (Field field : stringMatcherClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) && field.getGenericType().getTypeName().equals("h03.PartialMatchLengthUpdateValues<T>"))
                partialMatchLengthUpdateValues = field;

        assertNotNull(partialMatchLengthUpdateValues, "No field with criteria specified by the assignment found in StringMatcher");

        // methods
        try {
            findAllMatches = stringMatcherClass.getDeclaredMethod("findAllMatches", Object[].class);
        } catch (NoSuchMethodException e) {
            fail("StringMatcher is missing a required method", e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomMatcherArgumentsProvider.class)
    public void testFindAllMatchesLookupTable(List<Character> stack, List<Character> needle, List<Integer> matchIndices) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object matrixInstance = PartialMatchLengthUpdateValuesAsMatrixTest.constructor.newInstance(functionToIntInstance, needle.toArray());
        Object instance = constructor.newInstance(matrixInstance);

        //noinspection unchecked
        List<Integer> result = (List<Integer>) findAllMatches.invoke(instance, (Object) stack.toArray());

        assertEquals(matchIndices.size(), result.size());
        assertTrue(result.containsAll(matchIndices));
    }

    @ParameterizedTest
    @ArgumentsSource(RandomMatcherArgumentsProvider.class)
    public void testFindAllMatchesAutomaton(List<Character> stack, List<Character> needle, List<Integer> matchIndices) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object automatonInstance = PartialMatchLengthUpdateValuesAsAutomatonTest.constructor.newInstance(functionToIntInstance, needle.toArray());
        Object instance = constructor.newInstance(automatonInstance);

        //noinspection unchecked
        List<Integer> result = (List<Integer>) findAllMatches.invoke(instance, (Object) stack.toArray());

        assertEquals(matchIndices.size(), result.size());
        assertTrue(result.containsAll(matchIndices));
    }

    @Test
    public void additionalTests() {
        System.out.println("For more tests for StringMatcher check out the StringMatcherTests-branch: https://git.rwth-aachen.de/aud-tests/AuD-2021-H03-Student/-/tree/StringMatcherTest/");
    }
}
