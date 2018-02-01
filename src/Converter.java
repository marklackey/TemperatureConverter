import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts between units of measurement in a fluent interface style.
 * <p>
 * new Converter().convert(24.0, "F").to("C")
 *
 * Assumptions:
 * I tried to not have too many assumptions. Added exceptions for improper use.
 *
 * Interface:
 * The instuctions stated that the example code was just an example. I thought the .execute()
 * method was unnecessary, so in this implementation, .to() returns a double.
 */
public class Converter {

    private double value;
    private String unit;
    private static Map<String, Map<String, Function<Double, Double>>> conversionFunctionMap = new HashMap<>();
    private static String validInitialUnits;
    private static MathContext mathContext;

    static {
        conversionFunctionMap.put("C", new HashMap<>());
        conversionFunctionMap.get("C").put("F", x -> (x * 9.0 / 5.0) + 32);
        conversionFunctionMap.put("F", new HashMap<>());
        conversionFunctionMap.get("F").put("K", x -> (x + 459.67) * (5.0 / 9.0));
        conversionFunctionMap.put("K", new HashMap<>());
        conversionFunctionMap.get("K").put("C", x -> x - 273.15);

        validInitialUnits = String.join(", ", conversionFunctionMap.keySet());
        mathContext = new MathContext(2, RoundingMode.HALF_UP);
    }

    public Converter convert(double initialValue, String initialUnit) {
        if (isValidInitialUnit(initialUnit)) {
            this.value = initialValue;
            this.unit = initialUnit;
            return this;
        } else {
            throw new IllegalArgumentException(
                String.format("Cannot convert from %s. Valid initial units: %s", initialUnit, validInitialUnits));
        }
    }

    public double to(String toUnit) {
        if (this.unit == null) {
            throw new IllegalStateException("Converter not properly initiated.");
        }
        Function<Double, Double> conversionFunction = conversionFunctionMap.get(unit).get(toUnit);
        if(conversionFunction == null){
            throw new IllegalArgumentException(
                String.format("Cannot convert from %s to %s. Can convert %s to: %s", unit, toUnit, unit, validToUnits())
            );
        }
        return round(conversionFunction.apply(value));
    }

    private static double round(double value) {
        return new BigDecimal(value, mathContext).doubleValue();
    }

    private static boolean isValidInitialUnit(String unit) {
        return conversionFunctionMap.keySet().contains(unit);
    }

    private String validToUnits() {
        return String.join(", ", conversionFunctionMap.get(unit).keySet());
    }

    //Example usages
    public static void main(String[] args) {
        try {
            System.out.println(new Converter().convert(1.23, "C").to("F"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(new Converter().convert(1.23, "K").to("C"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(new Converter().convert(32.23, "F").to("K"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(new Converter().convert(32.23, "F").to("C"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(new Converter().convert(32.23, "M").to("J"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(new Converter().to("J"));
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
