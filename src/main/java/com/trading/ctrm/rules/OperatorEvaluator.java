package com.trading.ctrm.rules;

import org.springframework.stereotype.Component;

@Component
public class OperatorEvaluator {

    public boolean evaluate(
            String operator,
            Object left,
            String rightValue
    ) {
        if (left == null) {
            return false;
        }

        return switch (operator) {
            case ">" -> ((Number) left).doubleValue()
                       > Double.parseDouble(rightValue);

            case "<" -> ((Number) left).doubleValue()
                       < Double.parseDouble(rightValue);

            case ">=" -> ((Number) left).doubleValue()
                        >= Double.parseDouble(rightValue);

            case "<=" -> ((Number) left).doubleValue()
                        <= Double.parseDouble(rightValue);

            case "==", "=" -> {
                if (left instanceof Number) {
                    yield ((Number) left).doubleValue() == Double.parseDouble(rightValue);
                } else {
                    yield left.toString().equalsIgnoreCase(rightValue);
                }
            }

            case "!=", "<>" -> {
                if (left instanceof Number) {
                    yield ((Number) left).doubleValue() != Double.parseDouble(rightValue);
                } else {
                    yield !left.toString().equalsIgnoreCase(rightValue);
                }
            }

            case "IN", "EQUALS" -> left.toString().equalsIgnoreCase(rightValue);

            case "CONTAINS" -> left.toString().toLowerCase()
                                   .contains(rightValue.toLowerCase());

            default -> throw new IllegalArgumentException(
                "Unsupported operator: " + operator
            );
        };
    }
}

