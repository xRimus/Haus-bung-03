package h03;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.opentest4j.TestAbortedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {

    public static final long SEED = new Random().nextLong();
    public static final Random RANDOM = new Random(SEED);
    public static final IntFunction<Character> CAST_TO_CHARACTER = n -> (char) n;
    public static final List<Object> ALPHABET = Stream.concat(
            IntStream.range('A', 'Z' + 1).mapToObj(CAST_TO_CHARACTER),
            IntStream.range('a', 'z' + 1).mapToObj(CAST_TO_CHARACTER)
    ).collect(Collectors.toUnmodifiableList());

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestAbortedException("Class " + e.getMessage() + " not found", e);
        }
    }

    public static Object functionToIntProxyForAlphabet(List<Object> alphabet) throws ReflectiveOperationException {
        Class<?> functionToIntClass = Class.forName("h03.FunctionToInt");
        InvocationHandler handler = new InvocationHandler() {
            @Override
            @SuppressWarnings("SuspiciousInvocationHandlerImplementation")
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.equals(functionToIntClass.getMethod("apply", Object.class)))
                    return alphabet.indexOf(args[0]);
                else if (method.equals(functionToIntClass.getMethod("sizeOfAlphabet")))
                    return alphabet.size();

                throw new NoSuchMethodException(method.toString());
            }
        };

        return Proxy.newProxyInstance(functionToIntClass.getClassLoader(), new Class[] {functionToIntClass}, handler);
    }

    public static Stream<Character> randomLowercaseCharStream(int size) {
        return IntStream.generate(() -> 'a' + RANDOM.nextInt(26))
                .mapToObj(CAST_TO_CHARACTER)
                .distinct()
                .limit(size);
    }

    public static Stream<Character> randomUppercaseCharStream(int size) {
        return IntStream.generate(() -> 'A' + RANDOM.nextInt(26))
                        .mapToObj(CAST_TO_CHARACTER)
                        .distinct()
                        .limit(size);
    }

    @Test
    public void printSeed() {
        System.out.println("Seed: " + SEED);
    }

    private static final Map<Class<?>, Boolean> CLASS_CORRECT_LOOKUP = new HashMap<>();

    public static boolean definitionCorrect(Class<?> c) {
        if (CLASS_CORRECT_LOOKUP.containsKey(c))
            return CLASS_CORRECT_LOOKUP.get(c);

        try {
            c.getDeclaredMethod(c.getDeclaredAnnotation(DefinitionCheck.class).value()).invoke(null);
            CLASS_CORRECT_LOOKUP.put(c, true);
        } catch (Exception e) {
            CLASS_CORRECT_LOOKUP.put(c, false);
        }

        return CLASS_CORRECT_LOOKUP.get(c);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface DefinitionCheck {
        String value();
    }
}

class RandomCharProvider implements ArgumentsProvider {

    private static final int MAX_STREAM_SIZE = 3;

    /**
     * Returns a stream of arguments with format {@code (char as integer, char)}.
     * The stream will always contain arguments for the lower and upper bound of {@link Character}.
     * If {@link RandomCharProvider#MAX_STREAM_SIZE} is greater than zero then the stream will
     * contain that amount of arguments with random characters. So the total amount of elements
     * in the stream will be 2 + numberOfRandomArguments
     * @param context the context supplied by JUnit, may be null when invoking directly
     * @return the stream of arguments
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                Stream.of(Arguments.of(0, '\u0000'), Arguments.of(65535, '\uFFFF')),
                new Random(Utils.SEED)
                        .ints(MAX_STREAM_SIZE, 1, 65535)
                        .mapToObj(i -> Arguments.of(i, (char) i))
        );
    }
}

class RandomNeedleProvider implements ArgumentsProvider {

    private static final int MAX_STREAM_SIZE = 5;
    private static final int MAX_NEEDLE_LENGTH = 10;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.generate(() -> Utils.randomUppercaseCharStream(MAX_NEEDLE_LENGTH))
                     .limit(MAX_STREAM_SIZE)
                     .map(stream -> {
                         List<Character> needle = stream.collect(Collectors.toList());
                         int repeatLength = Utils.RANDOM.nextInt(MAX_NEEDLE_LENGTH);

                         needle.addAll(needle.subList(0, repeatLength));

                         return Arguments.of(
                                 needle.toArray(Character[]::new),
                                 repeatLength
                         );
                     });
    }
}

class RandomMatcherArgumentsProvider implements ArgumentsProvider {

    private static final int MAX_STREAM_SIZE = 5;
    private static final int MAX_NEEDLE_LENGTH = 10;
    private static final int STACK_SIZE = 20;
    private static final double NEEDLE_FREQUENCY = 0.1;
    private static final double OVERLAP_FREQUENCY = 0.2;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.generate(() -> {
            List<Character> needle = Utils.randomUppercaseCharStream(MAX_NEEDLE_LENGTH).collect(Collectors.toList()),
                            stack = Utils.randomLowercaseCharStream(STACK_SIZE)
                                           .map(character -> {
                                               if (Utils.RANDOM.nextDouble() < NEEDLE_FREQUENCY)
                                                   return Utils.RANDOM.nextDouble() < OVERLAP_FREQUENCY ? '#' : '$';
                                               else
                                                   return character;
                                           })
                                           .collect(Collectors.toList());
            List<Integer> matchIndices = new ArrayList<>();
            int repeatLength = Utils.RANDOM.nextInt(MAX_NEEDLE_LENGTH);

            needle.addAll(needle.subList(0, repeatLength));

            for (int i = 0; i < stack.size(); i++)
                switch (stack.get(i)) {
                    case '$':
                        stack.set(i, needle.get(0));
                        stack.addAll(i + 1, new ArrayList<>(needle.subList(1, needle.size())));

                        matchIndices.add(i + 1); // + 1 because "convention"
                        break;

                    case '#':
                        List<Character> overlappingNeedle = new ArrayList<>(needle.subList(1, needle.size() - repeatLength));
                        overlappingNeedle.addAll(new ArrayList<>(needle));

                        stack.set(i, needle.get(0));
                        stack.addAll(i + 1, overlappingNeedle);

                        matchIndices.add(i + 1); // + 1 because "convention"
                        matchIndices.add(i + needle.size() - repeatLength + 1); // and here as well
                }

            return Arguments.of(stack, needle, matchIndices);
        }).limit(MAX_STREAM_SIZE);
    }
}
