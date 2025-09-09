package apps.sarafrika.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Camp Sarafrika USSD API",
        version = "1.0.0",
        description = """
            # Camp Sarafrika USSD Service API
            
            A robust USSD application for camp registration and management built with Quarkus.
            
            ## Features
            - **Camp Registration**: Complete flow for registering participants for various camps
            - **Session Management**: Redis-based stateful session handling with 5-minute TTL
            - **Category Selection**: Dynamic camp categories loaded from database
            - **Pagination**: Support for paginated lists (3 items per page)
            - **Conditional Logic**: Guardian phone required for participants under 18
            - **Reference Tracking**: Unique reference code generation (CS-XXXXXXXX format)
            - **Booking Management**: View existing bookings by phone number
            - **Help System**: Built-in help and support information
            
            ## USSD Flow
            1. **Welcome Menu**: Register, My Bookings, Help, Exit
            2. **Category Selection**: Choose from available camp categories
            3. **Camp Selection**: Browse camps with pagination support
            4. **Data Collection**: Name, age, phone numbers (conditional guardian phone)
            5. **Confirmation**: Review and confirm registration
            6. **Payment**: Integration with M-Pesa (via Africa's Talking)
            7. **Confirmation**: SMS confirmation with reference code
            
            ## Technical Architecture
            - **Backend**: Quarkus with Java 21
            - **Database**: PostgreSQL with Flyway migrations
            - **Session Store**: Redis with automatic expiration
            - **API Integration**: Africa's Talking for USSD, SMS, and Payments
            - **Documentation**: OpenAPI 3.0 with Swagger UI
            
            ## Session Management
            Sessions are stored in Redis with the following structure:
            - **Key Format**: `ussd:session:{sessionId}`
            - **TTL**: 5 minutes (300 seconds)
            - **Data**: JSON serialized UserSession object
            - **State Tracking**: Stack-based navigation history
            - **Pagination**: Offset tracking for large lists
            
            ## Validation Rules
            - **Phone Numbers**: Kenyan format (+254XXXXXXXXX, 254XXXXXXXXX, 0XXXXXXXXX)
            - **Age Range**: 5-25 years
            - **Guardian Phone**: Required for participants under 18
            - **Name Length**: Minimum 2 characters
            
            ## Response Format
            All USSD responses follow Africa's Talking format:
            - **CON {message}**: Continue session (show menu/prompt)
            - **END {message}**: End session (final response)
            
            ## Environment Configuration
            The service can be configured using environment variables for different deployment environments (development, staging, production).
            """,
        contact = @Contact(
            name = "Camp Sarafrika Development Team",
            url = "https://sarafrika.com",
            email = "dev@sarafrika.com"
        ),
        license = @License(
            name = "MIT",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Development Server"),
        @Server(url = "https://ussd-staging.sarafrika.com", description = "Staging Server"),
        @Server(url = "https://camp-ussd.sarafrika.com", description = "Production Server")
    },
    tags = {
        @Tag(name = "USSD", description = "USSD webhook and session management endpoints"),
        @Tag(name = "Health", description = "Service health and monitoring endpoints")
    }
)
public class OpenApiConfig extends Application {
}