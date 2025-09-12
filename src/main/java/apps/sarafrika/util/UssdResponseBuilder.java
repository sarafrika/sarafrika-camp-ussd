package apps.sarafrika.util;

import java.util.ArrayList;
import java.util.List;

public class UssdResponseBuilder {
    
    // USSD character limits - standard USSD supports up to 182 characters
    private static final int MAX_USSD_LENGTH = 182;
    private static final int SAFE_USSD_LENGTH = 175; // Leave buffer for network variations
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
        if (content.length() > 0) {
            content.append("\n");
        }
        content.append(line);
        return this;
    }
    
    public UssdResponseBuilder addMenuItem(int number, String text, String detail) {
        String menuItem = String.format("%d. %s", number, text);
        if (detail != null && !detail.trim().isEmpty()) {
            // Calculate remaining space for detail
            int remainingSpace = MAX_LINE_LENGTH - menuItem.length() - 3; // 3 for " - "
            if (remainingSpace > 10) { // Only add detail if we have reasonable space
                menuItem += " - " + detail;
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