import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamHomework {

    public static void main(String[] args) {
        List<String> names = List.of("Ivan", "Maria", "Peter", "Olena", "John", "Anna");

        // Завдання 1
        System.out.println("Завдання 1: " + getOddIndexedNames(names));

        // Завдання 2
        System.out.println("Завдання 2: " + toUpperAndSortDesc(names));

        // Завдання 3
        String[] numbersArray = {"1, 2, 0", "4, 5"};
        System.out.println("Завдання 3: " + extractAndSortNumbers(numbersArray));

        // Завдання 4
        long a = 25214903917L;
        long c = 11L;
        long m = 1L << 48; // 2^48 через бітовий зсув (швидше та точніше за Math.pow)
        long seed = 42L;

        List<Long> randomNumbers = linearCongruentialGenerator(seed, a, c, m)
                .limit(5)
                .toList();
        System.out.println("Завдання 4: " + randomNumbers);

        // Завдання 5
        Stream<String> first = Stream.of("A", "B", "C", "D");
        Stream<String> second = Stream.of("1", "2");
        List<String> zipped = zip(first, second).toList();
        System.out.println("Завдання 5: " + zipped);
    }

    /**
     * Завдання 1: Імена під непарними індексами (1, 3, 5...) у форматі "1. Maria, 3. Olena"
     */
    public static String getOddIndexedNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return "";
        }
        return IntStream.range(0, names.size())
                .filter(i -> i % 2 != 0)
                .mapToObj(i -> i + ". " + names.get(i))
                .collect(Collectors.joining(", "));
    }

    /**
     * Завдання 2: Переведення у верхній регістр та сортування від Z до A
     */
    public static List<String> toUpperAndSortDesc(List<String> strings) {
        if (strings == null) {
            return List.of();
        }
        return strings.stream()
                .map(String::toUpperCase)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /**
     * Завдання 3: Витягування чисел з масиву рядків та їх сортування
     */
    public static String extractAndSortNumbers(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        return Arrays.stream(array)
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt) // парсимо в Integer, щоб "10" не йшло перед "2"
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    /**
     * Завдання 4: Лінійний конгруентний генератор (LCG)
     */
    public static Stream<Long> linearCongruentialGenerator(long seed, long a, long c, long m) {
        BigInteger multiplier = BigInteger.valueOf(a);
        BigInteger increment = BigInteger.valueOf(c);
        BigInteger modulus = BigInteger.valueOf(m);
        return Stream.iterate(seed, x -> BigInteger.valueOf(x)
                .multiply(multiplier)
                .add(increment)
                .mod(modulus)
                .longValue());
    }

    /**
     * Завдання 5: Ліниве перемішування двох стрімів до вичерпання коротшого
     */
    public static <T> Stream<T> zip(Stream<T> first, Stream<T> second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        Iterator<T> firstIt = first.iterator();
        Iterator<T> secondIt = second.iterator();

        Spliterator<T> spliterator = new Spliterators.AbstractSpliterator<>(
                Long.MAX_VALUE,
                Spliterator.ORDERED
        ) {
            private boolean pickFromFirst = true;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if (pickFromFirst) {
                    if (!firstIt.hasNext() || !secondIt.hasNext()) {
                        return false; // зупиняємося, якщо будь-який стрім вже порожній
                    }
                    action.accept(firstIt.next());
                } else {
                    action.accept(secondIt.next());
                }
                pickFromFirst = !pickFromFirst;
                return true;
            }
        };

        return StreamSupport.stream(spliterator, false)
                .onClose(first::close)
                .onClose(second::close);
    }
}