package com.app.JobData.util;

import com.app.JobData.model.JobEntry;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DataCleanerUtil {
    private static final String DECIMAL_REGEX = "^\\\\d+(\\\\.\\\\d+)?$";
    private static final Pattern DECIMAL_PATTERN = Pattern.compile(DECIMAL_REGEX);
    private static final Pattern SALARY_CLEAN_PATTERN = Pattern.compile("[^\\d.]");
    private static final Pattern EXTRACT_NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)");

    public static JobEntry cleanAndMapToJobEntry(Map<String, String> rawData) {
        if (rawData == null) {
            return null;
        }
        JobEntry jobEntry = new JobEntry();
        jobEntry.setTimestamp(rawData.get("Timestamp"));
        jobEntry.setEmployer(rawData.get("Employer"));
        jobEntry.setLocation(rawData.get("Location"));
        jobEntry.setJobTitle(rawData.get("Job Title"));
        jobEntry.setYearsAtEmployer(rawData.get("Years at Employer"));
        jobEntry.setYearsOfExperience(rawData.get("Years of Experience"));
        jobEntry.setAnnualStockValueBonus(rawData.get("Annual Stock Value/Bonus"));
        jobEntry.setGender(rawData.get("Gender"));
        jobEntry.setAdditionalComments(rawData.get("Additional Comments"));

        jobEntry.setSalary(parseCurrency(rawData.get("Salary")));
        jobEntry.setSigningBonus(rawData.get("Signing Bonus"));
        jobEntry.setAnnualBonus(rawData.get("Annual Bonus"));

        return jobEntry;
    }

    private static BigDecimal parseBigDecimalOrElseNull(String val) {
        if (StringUtils.isBlank(val)) {
            return null;
        } else if (DECIMAL_PATTERN.matcher(val).matches()) {
            return new BigDecimal(val);
        } else {
            return null;
        }
    }
    public static BigDecimal parseCurrency(String currencyString) {
        if (currencyString == null || currencyString.trim().isEmpty()) {
            return null;
        }

        String cleanedString = currencyString.replace("$", "")
                .replace("€", "")
                .replace("£", "")
                .replace("₹", "")
                .replace("¥", "")
                .replace("₽", "")
                .replace("SEK", "")
                .replace("DKK", "")
                .replace("R$", "")
                .replace("BRL", "")
                .replace("AUD", "")
                .replace("CAD", "")
                .replace("NZD", "")
                .replace("USD", "")
                .replace("HKD", "")
                .replace("PLN", "")
                .replace("RUB", "")
                .replace("SGD", "")
                .replace("CHF", "")
                .replace("IDR", "")
                .replace("KHR", "")
                .replace("LAK", "")
                .replace("MYR", "")
                .replace("PHP", "")
                .replace("THB", "")
                .replace("VND", "")
                .replace("CNY", "")
                .replace("원", "")
                .replace("INR", "")
                .replace("JYP", "")
                .replace("EUR", "")
                .replace("yen", "")
                .replace(" ", "")
                .trim();
        BigDecimal multiplier = BigDecimal.ONE;
        if (cleanedString.toLowerCase().endsWith("k")) {
            cleanedString = cleanedString.substring(0, cleanedString.length() - 1);
            multiplier = new BigDecimal("1000");
        } else if (cleanedString.toLowerCase().endsWith("m")) {
            cleanedString = cleanedString.substring(0, cleanedString.length() - 1);
            multiplier = new BigDecimal("1000000");
        }

        if (cleanedString.contains(",") && !cleanedString.contains(".")) {
            cleanedString = cleanedString.replace(",", ".");
        } else if (countOccurrences(cleanedString, '.') > 1) {
            Matcher matcher = EXTRACT_NUMBER_PATTERN.matcher(cleanedString);
            if (matcher.find()) {
                cleanedString = matcher.group(1).replace(".", "");
            } else {
                return null;
            }
        }


        try {
            BigDecimal value = new BigDecimal(cleanedString);
            return value.multiply(multiplier);
        } catch (NumberFormatException e) {
            log.error("Could not parse currency string: '{}' after cleaning to '{}'", currencyString, cleanedString);
            return null;
        }
    }

    private static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }
}
