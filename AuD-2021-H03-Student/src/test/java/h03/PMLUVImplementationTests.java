package h03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static h03.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
class PartialMatchLengthUpdateValuesAsMatrixTest {

    static Class<?> partialMatchLengthUpdateValuesAsMatrixClass;
    static Constructor<?> constructor;
    static Method getPartialMatchLengthUpdate;
    static Field lookupTable;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(FunctionToIntTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that interface FunctionToInt is implemented correctly");
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that class PartialMatchLengthUpdateValues is implemented correctly");

        try {
            partialMatchLengthUpdateValuesAsMatrixClass = Class.forName("h03.PartialMatchLengthUpdateValuesAsMatrix");
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        }

        // is generic
        TypeVariable<?>[] typeParameters = partialMatchLengthUpdateValuesAsMatrixClass.getTypeParameters();

        assertEquals(1, typeParameters.length, "PartialMatchLengthUpdateValuesAsMatrix must be generic");
        assertEquals("T", typeParameters[0].getName(), "Type parameter of class PartialMatchLengthUpdateValuesAsMatrix is not named 'T'");

        // extends PartialMatchLengthUpdateValues<T>
        assertEquals(
                "h03.PartialMatchLengthUpdateValues<T>",
                partialMatchLengthUpdateValuesAsMatrixClass.getGenericSuperclass().getTypeName(),
                "PartialMatchLengthUpdateValuesAsMatrix must extend PartialMatchLengthUpdateValues"
        );

        // is not abstract
        assertFalse(
                isAbstract(partialMatchLengthUpdateValuesAsMatrixClass.getModifiers()),
                "PartialMatchLengthUpdateValuesAsMatrix must not be abstract"
        );

        // fields
        for (Field field : partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) && field.getType().equals(int[][].class) && lookupTable == null)
                lookupTable = field;

        assertNotNull(lookupTable, "Class PartialMatchLengthUpdateValuesAsMatrix has no field matching the criteria for the lookup table");

        lookupTable.setAccessible(true);

        // constructors
        try {
            constructor = partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredConstructor(FunctionToIntTest.functionToIntIntf, Object[].class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsMatrix is missing a required constructor", e);
        }

        // methods
        try {
            getPartialMatchLengthUpdate = partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredMethod("getPartialMatchLengthUpdate", int.class, Object.class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsMatrix is missing required method \"getPartialMatchLengthUpdate(int, T)\"", e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testLookupTable(Character[] needle, int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object instance = constructor.newInstance(functionToIntInstance, needle);
        MatrixHandler matrix = new MatrixHandler(partialMatchLengthUpdateValuesAsMatrixClass, instance, lookupTable);

        // dimensions of lookup table
        assertEquals(needle.length + 1, matrix.getNumberOfRows());
        assertEquals(FunctionToIntTest.sizeOfAlphabet.invoke(functionToIntInstance), matrix.getNumberOfColumns());

        // check that every value in column of first character is > 0
        for (int i = 0, valueOfFirstCharacter = (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[0]); i < needle.length + 1; i++)
            assertTrue(
                    matrix.get(i, valueOfFirstCharacter) > 0,
                    "Values in column apply(needle[0]) (or row if alphabet is assigned to rows) have to be at least 1"
            );

        // check that values for needle elements are correct
        for (int i = 0; i < needle.length; i++)
            assertEquals(
                    i + 1,
                    matrix.get(i, (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[i])),
                    "Value for apply(needle[i]) in row i (or column i if alphabet is assigned to rows) should be i + 1"
            );

        // check... idk how to describe it, involves computePartialMatchLengthUpdateValues
        assertEquals(
                repeatLength + 1,
                matrix.get(needle.length, (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[repeatLength])),
                "The value at index apply(needle[repeatLength]) in the last row of the lookup table " +
                        "(or vice versa if alphabet is assigned to rows) does not equal the expected value. " +
                        "Take a look at the summary for the string matching BOFA algorithm in moodle"
        );

        // check that other non-zero values are equal to lookupTable[cellValue - 1][j] (repeating segments in needle)
        int cellValue;

        for (int i = 0; i < needle.length + 1; i++)
            for (int j = 0; j < ALPHABET.size(); j++)
                if ((cellValue = matrix.get(i, j, false)) != 0 && !matrix.checkedCoordinates.contains(new MatrixHandler.Coordinates(i, j)))
                    assertEquals(cellValue, matrix.get(cellValue - 1, j, false));
    }

    @Test
    public void testGivenLookupTableEqualsGenerated() throws ReflectiveOperationException {
        Object functionToIntProxyInstance = functionToIntProxyForAlphabet(List.of('A', 'B', 'C'));
        int[][] lookupTable = new int[][] {
                {1, 0, 0},
                {1, 2, 0},
                {3, 0, 0},
                {1, 4, 0},
                {5, 0, 0},
                {1, 4, 6},
                {7, 0, 0},
                {1, 2, 0}
        };
        Object instance = constructor.newInstance(functionToIntProxyInstance, new Character[] {'A', 'B', 'A', 'B', 'A', 'C', 'A'});
        MatrixHandler matrixHandler = new MatrixHandler(partialMatchLengthUpdateValuesAsMatrixClass, instance,
                PartialMatchLengthUpdateValuesAsMatrixTest.lookupTable);

        for (int i = 0; i < lookupTable.length; i++)
            for (int j = 0; j < lookupTable[0].length; j++)
                assertEquals(lookupTable[i][j], matrixHandler.get(i, j, false));
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testGetPartialMatchLengthUpdate(Character[] needle, @SuppressWarnings("unused") int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object instance = constructor.newInstance(functionToIntInstance, needle);

        // check that method returns correct values for match
        for (int i = 0; i < needle.length; i++)
            assertEquals(
                    i + 1,
                    getPartialMatchLengthUpdate.invoke(instance, i, needle[i]),
                    "getPartialMatchLengthUpdate(i, needle[i]) should return i + 1"
            );

        // the method is rather trivial to implement, the correctness of the lookup table is more important
    }

    private static class MatrixHandler {

        private final int[][] matrix;
        private final boolean transpose;

        public final Set<Coordinates> checkedCoordinates;

        public MatrixHandler(Class<?> c, Object instance, Field field) throws ReflectiveOperationException {
            matrix = ((int[][]) field.get(instance));
            checkedCoordinates = new HashSet<>(matrix.length * matrix[0].length);

            // determine whether the lookup table has alphabet assigned to rows or columns
            List<Object> alphabet = List.of('A');
            Object cInstance = c.getDeclaredConstructor(Class.forName("h03.FunctionToInt"), Object[].class)
                    .newInstance(functionToIntProxyForAlphabet(alphabet), new Object[] {'A', 'A', 'A'});
            transpose = ((int[][]) field.get(cInstance)).length == alphabet.size();
        }

        public int get(int row, int col) {
            return get(row, col, true);
        }

        public int get(int row, int col, boolean addToCoords) {
            if (addToCoords)
                checkedCoordinates.add(new Coordinates(row, col));

            return matrix[transpose ? col : row][transpose ? row : col];
        }

        public int getNumberOfRows() {
            return transpose ? matrix[0].length : matrix.length;
        }

        public int getNumberOfColumns() {
            return transpose ? matrix.length : matrix[0].length;
        }

        static class Coordinates {

            public final int row, col;

            public Coordinates(int row, int col) {
                this.row = row;
                this.col = col;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Coordinates that = (Coordinates) o;
                return row == that.row && col == that.col;
            }

            @Override
            public int hashCode() {
                return Objects.hash(row, col);
            }
        }
    }
}

@DefinitionCheck("checkClass")
class PartialMatchLengthUpdateValuesAsAutomatonTest {

    static Constructor<?> constructor;
    static Method getPartialMatchLengthUpdate;
    static Field theStates;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(FunctionToIntTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that interface FunctionToInt is implemented correctly");
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that class PartialMatchLengthUpdateValues is implemented correctly");

        Class<?> partialMatchLengthUpdateValuesAsAutomatonClass = getClassForName("h03.PartialMatchLengthUpdateValuesAsAutomaton");

        TransitionTest.checkClass();

        // is generic
        TypeVariable<?>[] typeParameters = partialMatchLengthUpdateValuesAsAutomatonClass.getTypeParameters();

        assertEquals(1, typeParameters.length, "PartialMatchLengthUpdateValuesAsAutomaton must be generic");
        assertEquals("T", typeParameters[0].getName(), "Type parameter of class PartialMatchLengthUpdateValuesAsAutomaton is not named 'T'");

        // extends PartialMatchLengthUpdateValues<T>
        assertEquals(
                "h03.PartialMatchLengthUpdateValues<T>",
                partialMatchLengthUpdateValuesAsAutomatonClass.getGenericSuperclass().getTypeName(),
                "PartialMatchLengthUpdateValuesAsAutomaton must extend PartialMatchLengthUpdateValues"
        );

        // is not abstract
        assertFalse(
                isAbstract(partialMatchLengthUpdateValuesAsAutomatonClass.getModifiers()),
                "PartialMatchLengthUpdateValuesAsAutomaton must not be abstract"
        );

        // fields
        try {
            theStates = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredField("theStates");
        } catch (NoSuchFieldException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing required field \"theStates\"", e);
        }

        assertTrue(isPrivate(theStates.getModifiers()), "Field theStates is not private");
        assertEquals("java.util.List<" + TransitionTest.transitionClass.getName() + "<T>>[]", theStates.getGenericType().getTypeName(),
                "Field theStates does not have correct type");

        theStates.setAccessible(true);

        // constructors
        try {
            constructor = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredConstructor(FunctionToIntTest.functionToIntIntf, Object[].class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing a required constructor", e);
        }

        // methods
        try {
            getPartialMatchLengthUpdate = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredMethod("getPartialMatchLengthUpdate", int.class, Object.class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing required method \"getPartialMatchLengthUpdate(int, T)\"", e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testTheStates(Character[] needle, int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object instance = constructor.newInstance(functionToIntInstance, needle);

        //noinspection unchecked
        List<Object>[] states = ((List<Object>[]) theStates.get(instance));

        assertEquals(needle.length + 1, states.length, "theStates doesn't have correct length");

        for (int i = 0; i < needle.length; i++) {
            int index = i;

            assertTrue(states[i].stream().anyMatch(transition -> {
                try {
                    boolean indexCorrect = ((int) TransitionTest.index.get(transition)) == index + 1,
                            targetSizeCorrect = ((List<?>) TransitionTest.target.get(transition)).size() == 1,
                            targetContentCorrect = ((List<?>) TransitionTest.target.get(transition)).get(0) == needle[index];

                    return indexCorrect && targetSizeCorrect && targetContentCorrect;
                } catch (IllegalAccessException e) {
                    return false;
                }
            }));
        }

        assertTrue(states[needle.length].stream().anyMatch(transition -> {
            try {
                boolean equalsRepeatLength = TransitionTest.index.get(transition).equals(repeatLength + 1),
                        equalsTarget = ((List<?>) TransitionTest.target.get(transition)).get(0).equals(needle[repeatLength]);

                return equalsRepeatLength && equalsTarget;
            } catch (IllegalAccessException e) {
                return false;
            }
        }));
    }

    @Test
    public void testGivenAutomatonEqualsGenerated() throws ReflectiveOperationException {
        List<Object> alphabet = List.of('D', 'V', 'X');
        Object functionToIntProxyInstance = functionToIntProxyForAlphabet(alphabet);
        int[][] automatonLookupTable = new int[][] {
                {0, 1, 0},
                {0, 1, 2},
                {0, 3, 0},
                {4, 1, 2},
                {0, 1, 0}
        };
        Object instance = constructor.newInstance(functionToIntProxyInstance, new Character[] {'V', 'X', 'V', 'D'});

        //noinspection unchecked
        List<Object>[] statesArray = (List<Object>[]) theStates.get(instance);

        assertEquals(automatonLookupTable.length, statesArray.length);

        for (int i = 0; i < automatonLookupTable.length; i++)
            for (int j = 0; j < automatonLookupTable[0].length; j++) {
                int cellValue = automatonLookupTable[i][j];
                List<Object> transitions = statesArray[i];

                if (cellValue == 0)
                    for (Object transition : transitions)
                        assertNotEquals(0, TransitionTest.index.get(transition));
                else
                    assertTrue(transitions.stream().anyMatch(transition -> {
                        try {
                            return TransitionTest.index.get(transition).equals(cellValue);
                        } catch (IllegalAccessException e) {
                            return false;
                        }
                    }));
            }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testGetPartialMatchLengthUpdate(Character[] needle, @SuppressWarnings("unused") int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(ALPHABET);
        Object instance = constructor.newInstance(functionToIntInstance, needle);

        // check that method returns correct values for match
        for (int i = 0; i < needle.length; i++)
            assertEquals(i + 1, getPartialMatchLengthUpdate.invoke(instance, i, needle[i]),
                    "getPartialMatchLengthUpdate(i, needle[i]) should return i + 1");

        List<Character> needleList = Arrays.stream(needle).collect(Collectors.toUnmodifiableList());

        for (Object character : ALPHABET)
            if (!needleList.contains((Character) character))
                for (int i = 0; i < needle.length + 1; i++)
                    assertEquals(0, getPartialMatchLengthUpdate.invoke(instance, i, character),
                            "getPartialMatchLengthUpdate(...) should return 0 for values that are not in needle");
    }

    private static class TransitionTest {

        static Class<?> transitionClass;
        static Field index, target;

        public static void checkClass() {
            try {
                transitionClass = Class.forName("h03.Transition");
            } catch (ClassNotFoundException e) {
                try {
                    transitionClass = Class.forName("h03.PartialMatchLengthUpdateValuesAsAutomaton$Transition");
                } catch (ClassNotFoundException ee) {
                    assumeTrue(false, "Class " + ee.getMessage() + " not found");
                }
            }

            // is generic
            TypeVariable<?>[] typeParameters = transitionClass.getTypeParameters();

            // you can comment out the following line if you want / have to
            assertEquals(1, typeParameters.length, "Transition is not generic (not strictly required but it makes more sense)");

            // fields
            for (Field field : transitionClass.getDeclaredFields())
                if (isPublic(field.getModifiers()) && isFinal(field.getModifiers()))
                    if (field.getType().equals(int.class))
                        index = field;
                    else if (field.getType().equals(List.class))
                        target = field;

            assertTrue(index != null && target != null,
                    "Transition is missing one or more required fields or they don't have the right type, visibility, etc.");

            index.setAccessible(true);
            target.setAccessible(true);
        }
    }
}
/*
class ComputePartialMatchLengthUpdateValuesTest extends PartialMatchLengthUpdateValues<Character> {

    @SuppressWarnings("unchecked")
    public ComputePartialMatchLengthUpdateValuesTest() throws ReflectiveOperationException {
        super((FunctionToInt<Character>) functionToIntProxyForAlphabet(ALPHABET));
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testComputePartialMatchLengthUpdateValues(Character[] needle, int repeatLength) {
        assertEquals(repeatLength, computePartialMatchLengthUpdateValues(needle));
    }

    @Override
    public int getPartialMatchLengthUpdate(int i, Character character) {
        return 0;
    }
}
*/