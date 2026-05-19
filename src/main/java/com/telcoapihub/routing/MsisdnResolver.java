package com.telcoapihub.routing;

import org.springframework.stereotype.Component;

/**
 * Resolves an MSISDN to the country that should serve it.
 *
 * <p>Scaffold implementation keys off the E.164 dialling prefix. Production
 * needs an HLR lookup / mobile number portability feed to identify the
 * actual host operator, since prefix alone cannot survive number porting.
 */
@Component
public class MsisdnResolver {

    public String resolveCountry(String msisdn) {
        if (msisdn == null) {
            return null;
        }
        String digits = msisdn.replaceAll("[^0-9]", "");
        if (digits.startsWith("44")) {
            return "GB";
        }
        if (digits.startsWith("34")) {
            return "ES";
        }
        if (digits.startsWith("49")) {
            return "DE";
        }
        if (digits.startsWith("33")) {
            return "FR";
        }
        if (digits.startsWith("1")) {
            return "US";
        }
        return null;
    }
}
