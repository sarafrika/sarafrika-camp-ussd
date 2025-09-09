package apps.sarafrika.util;

import java.util.ArrayList;
import java.util.List;

public class UssdResponseBuilder {
    
    // USSD character limits - conservative approach
    private static final int MAX_USSD_LENGTH = 160;
    private static final int SAFE_USSD_LENGTH = 150; // Leave buffer for network variations
    private static final int MAX_LINE_LENGTH = 35; // Reasonable line length for mobile screens
    
    private StringBuilder content;
    private String prefix;
    
    public UssdResponseBuilder() {
        this.content = new StringBuilder();
        this.prefix = "CON ";
    }
    
    public UssdResponseBuilder(String prefix) {
        this.content = new StringBuilder();
        this.prefix = prefix;
    }
    
    public static UssdResponseBuilder create() {
        return new UssdResponseBuilder();
    }
    
    public static UssdResponseBuilder end() {
        return new UssdResponseBuilder("END ");
    }
    
    public UssdResponseBuilder addLine(String line) {
        // Truncate long lines to prevent display issues
        String truncated = truncateText(line, MAX_LINE_LENGTH);
        
        if (content.length() > 0) {
            content.append("\n");
        }
        content.append(truncated);
        return this;
    }
    
    public UssdResponseBuilder addMenuItem(int number, String text, String detail) {
        String menuItem = String.format("%d. %s", number, text);
        if (detail != null && !detail.trim().isEmpty()) {
            // Calculate remaining space for detail
            int remainingSpace = MAX_LINE_LENGTH - menuItem.length() - 3; // 3 for " - "
            if (remainingSpace > 10) { // Only add detail if we have reasonable space
                String truncatedDetail = truncateText(detail, remainingSpace);
                menuItem += " - " + truncatedDetail;
            }
        }
        return addLine(menuItem);
    }
    
    public UssdResponseBuilder addEmptyLine() {
        return addLine("");
    }
    
    public UssdResponseBuilder addBackOption() {
        return addEmptyLine().addLine("0. Back");
    }
    
    public UssdResponseBuilder addMoreOption() {
        return addEmptyLine().addLine("99. More >>");
    }
    
    public String build() {
        String fullResponse = prefix + content.toString();
        
        // Check if response exceeds USSD limits
        if (fullResponse.length() > SAFE_USSD_LENGTH) {
            // Truncate gracefully - try to keep complete lines
            return truncateToSafeLength(fullResponse);
        }
        
        return fullResponse;
    }
    
    public boolean wouldExceedLimit(String additionalContent) {
        String testResponse = prefix + content.toString() + "\n" + additionalContent;
        return testResponse.length() > SAFE_USSD_LENGTH;
    }
    
    public int remainingCapacity() {
        String currentResponse = prefix + content.toString();
        return SAFE_USSD_LENGTH - currentResponse.length();
    }
    
    private static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        // Try to truncate at word boundary
        String truncated = text.substring(0, maxLength - 3); // Reserve space for "..."
        int lastSpace = truncated.lastIndexOf(' ');
        
        if (lastSpace > maxLength / 2) { // Only use word boundary if it's not too early
            return text.substring(0, lastSpace) + "...";
        } else {
            return text.substring(0, maxLength - 3) + "...";
        }
    }
    
    private String truncateToSafeLength(String response) {
        if (response.length() <= SAFE_USSD_LENGTH) {
            return response;
        }
        
        // Find the last complete line that fits
        String[] lines = response.split("\n");
        StringBuilder result = new StringBuilder();
        
        for (String line : lines) {
            String testResult = result.length() == 0 ? 
                prefix + line : 
                result.toString() + "\n" + line;
                
            if (testResult.length() <= SAFE_USSD_LENGTH - 10) { // Reserve space for "More>>"
                if (result.length() == 0) {
                    result.append(prefix).append(line);
                } else {
                    result.append("\n").append(line);
                }
            } else {
                break;
            }
        }
        
        return result.toString();
    }
    
    // Utility method to split long responses into multiple pages
    public static List<String> splitIntoPages(String content, String title) {
        List<String> pages = new ArrayList<>();
        String[] lines = content.split("\n");
        
        UssdResponseBuilder currentPage = create().addLine(title).addEmptyLine();
        
        for (String line : lines) {
            if (currentPage.wouldExceedLimit(line + "\n0. Back")) {
                // Finish current page
                pages.add(currentPage.addBackOption().build());
                
                // Start new page
                currentPage = create().addLine(title + " (cont'd)").addEmptyLine();
            }
            currentPage.addLine(line);
        }
        
        // Add the last page
        if (currentPage.remainingCapacity() > 10) {
            pages.add(currentPage.addBackOption().build());
        }
        
        return pages;
    }
}