import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DCFPricingCalculator {

    // Market assumptions for 2025
    private static final double DISCOUNT_RATE = 0.045; // 4.5% discount rate
    private static final double VOLATILITY = 0.25; // 25% volatility for options

    // Forward curve prices for 2025 (€/MWh)
    private static final double JAN25_PRICE = 68.50;
    private static final double FEB25_PRICE = 69.25;
    private static final double Q1_25_AVERAGE = 68.75; // Average of Jan-Feb
    private static final double BASELOAD_2025 = 65.00; // Annual baseload

    public static void main(String[] args) {
        System.out.println("DCF Pricing for Power Instruments (2025)");
        System.out.println("==========================================");

        // PWR-JAN25-DA (Day Ahead - more volatile, short dated)
        double jan25DCF = calculateDCFForSpot(JAN25_PRICE, 0.08); // 8 days to delivery
        System.out.printf("PWR-JAN25-DA: €%.2f/MWh (DCF: €%.2f/MWh)%n", JAN25_PRICE, jan25DCF);

        // PWR-FEB25-DA (Day Ahead)
        double feb25DCF = calculateDCFForSpot(FEB25_PRICE, 0.23); // ~23 days to delivery
        System.out.printf("PWR-FEB25-DA: €%.2f/MWh (DCF: €%.2f/MWh)%n", FEB25_PRICE, feb25DCF);

        // PWR-Q1-25 (Quarterly forward)
        double q1DCF = calculateDCFForForward(Q1_25_AVERAGE, 0.25); // ~3 months to delivery
        System.out.printf("PWR-Q1-25: €%.2f/MWh (DCF: €%.2f/MWh)%n", Q1_25_AVERAGE, q1DCF);

        // PWR-CALL-JAN25-60 (Call option, strike 60)
        double callDCF = calculateCallOption(JAN25_PRICE, 60.0, 0.08);
        System.out.printf("PWR-CALL-JAN25-60: €%.2f/MWh (DCF: €%.2f/MWh)%n", JAN25_PRICE, callDCF);

        // PWR-PUT-JAN25-50 (Put option, strike 50)
        double putDCF = calculatePutOption(JAN25_PRICE, 50.0, 0.08);
        System.out.printf("PWR-PUT-JAN25-50: €%.2f/MWh (DCF: €%.2f/MWh)%n", JAN25_PRICE, putDCF);

        // PWR-BASELOAD-2025 (Annual baseload contract)
        double baseloadDCF = calculateBaseloadForward(BASELOAD_2025, 0.5); // ~6 months average
        System.out.printf("PWR-BASELOAD-2025: €%.2f/MWh (DCF: €%.2f/MWh)%n", BASELOAD_2025, baseloadDCF);

        System.out.println("\nNotes:");
        System.out.println("- DCF prices account for time value of money");
        System.out.println("- Day-ahead prices have minimal discounting due to short timeframes");
        System.out.println("- Forward contracts show more significant discounting");
        System.out.println("- Options include time value based on Black-76 model");
        System.out.println("- Baseload contracts are discounted over the delivery period");
    }

    private static double calculateDCFForSpot(double spotPrice, double timeToDelivery) {
        return spotPrice * Math.exp(-DISCOUNT_RATE * timeToDelivery);
    }

    private static double calculateDCFForForward(double forwardPrice, double timeToDelivery) {
        return forwardPrice * Math.exp(-DISCOUNT_RATE * timeToDelivery);
    }

    private static double calculateCallOption(double spotPrice, double strike, double timeToExpiry) {
        // Simplified Black-76 call option pricing
        double d1 = (Math.log(spotPrice / strike) + (DISCOUNT_RATE + VOLATILITY * VOLATILITY / 2) * timeToExpiry) /
                   (VOLATILITY * Math.sqrt(timeToExpiry));
        double d2 = d1 - VOLATILITY * Math.sqrt(timeToExpiry);

        double callPrice = spotPrice * Math.exp(-DISCOUNT_RATE * timeToExpiry) * normCDF(d1) -
                          strike * Math.exp(-DISCOUNT_RATE * timeToExpiry) * normCDF(d2);

        return callPrice;
    }

    private static double calculatePutOption(double spotPrice, double strike, double timeToExpiry) {
        // Simplified Black-76 put option pricing
        double d1 = (Math.log(spotPrice / strike) + (DISCOUNT_RATE + VOLATILITY * VOLATILITY / 2) * timeToExpiry) /
                   (VOLATILITY * Math.sqrt(timeToExpiry));
        double d2 = d1 - VOLATILITY * Math.sqrt(timeToExpiry);

        double putPrice = strike * Math.exp(-DISCOUNT_RATE * timeToExpiry) * normCDF(-d2) -
                         spotPrice * Math.exp(-DISCOUNT_RATE * timeToExpiry) * normCDF(-d1);

        return putPrice;
    }

    private static double calculateBaseloadForward(double annualPrice, double averageTimeToDelivery) {
        // Baseload contracts are discounted over the delivery period
        return annualPrice * Math.exp(-DISCOUNT_RATE * averageTimeToDelivery);
    }

    // Approximation of normal cumulative distribution function
    private static double normCDF(double x) {
        return 0.5 * (1 + Math.signum(x) * Math.sqrt(1 - Math.exp(-2 * x * x / Math.PI)));
    }
}