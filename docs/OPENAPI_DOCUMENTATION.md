# OpenAPI Documentation Guide

## Overview
The Camp Sarafrika USSD Service includes comprehensive OpenAPI 3.0 documentation using Eclipse MicroProfile OpenAPI annotations. This provides interactive API documentation accessible via Swagger UI.

## Accessing the Documentation

### Development Environment
- **Swagger UI**: `http://localhost:8080/swagger-ui`
- **OpenAPI JSON**: `http://localhost:8080/openapi`
- **Raw OpenAPI Spec**: `http://localhost:8080/q/openapi`

### Production Environment
- **Swagger UI**: `https://ussd.sarafrika.com/swagger-ui`
- **OpenAPI JSON**: `https://ussd.sarafrika.com/openapi`

## Documentation Features Implemented

### 1. API Information
- **Title**: Camp Sarafrika USSD API
- **Version**: 1.0.0
- **Description**: Comprehensive markdown description with:
  - Feature overview
  - USSD flow explanation
  - Technical architecture details
  - Session management information
  - Validation rules
  - Response format specifications

### 2. Server Configuration
Three pre-configured server environments:
- **Development**: `http://localhost:8080`
- **Staging**: `https://ussd-staging.sarafrika.com`
- **Production**: `https://ussd.sarafrika.com`

### 3. Tags Organization
- **USSD**: Main webhook and session management endpoints
- **Health**: Service monitoring and health check endpoints

### 4. Endpoint Documentation

#### POST /ussd - USSD Webhook
**Comprehensive Documentation Includes:**
- **Summary**: Handle USSD Request
- **Description**: Detailed explanation of webhook functionality
- **Parameters**: All 5 form parameters with examples:
  - `sessionId`: Session identifier with example
  - `phoneNumber`: International format phone number
  - `networkCode`: Mobile network operator code
  - `serviceCode`: USSD service code dialed
  - `text`: User input with multiple example scenarios
- **Responses**: 
  - **200 OK**: Success responses with examples for:
    - Welcome menu display
    - Registration completion
  - **500 Error**: Error response with example message
- **Content Types**: Form-encoded requests, plain text responses

#### GET /ussd/health - Health Check
**Documentation Includes:**
- **Summary**: Health Check
- **Description**: Service monitoring explanation
- **Response Examples**: JSON health status format
- **Use Case**: Load balancer and monitoring integration

### 5. Schema Documentation

#### UserSession DTO
**Comprehensive Schema Includes:**
- **Class-level Description**: Purpose and storage details
- **Complete JSON Example**: Full session object structure
- **Property-level Documentation**:
  - `session_id`: Session identifier with validation
  - `phone_number`: International phone format
  - `state_history`: Navigation stack explanation
  - `data`: Key-value session data store
  - `pagination_offset`: List pagination tracking
  - `current_menu_items`: Menu validation support

### 6. Example Scenarios

#### USSD Flow Examples
1. **Initial Request**: Empty text parameter
2. **Option Selection**: Single digit input
3. **Complex Navigation**: Multi-step asterisk-separated input
4. **Registration Flow**: Complete data collection example

#### Response Examples
1. **Menu Display**: CON format with options
2. **Final Response**: END format with reference code
3. **Error Handling**: Technical difficulty message

## Interactive Features

### Swagger UI Capabilities
- **Try It Out**: Execute API calls directly from documentation
- **Parameter Testing**: Input validation and formatting
- **Response Visualization**: Real-time response display
- **Schema Exploration**: Interactive model browsing
- **Authentication**: Support for API key testing (when implemented)

### Request/Response Testing
- **Form Data Testing**: Test USSD webhook with various inputs
- **Session Simulation**: Test different user flow scenarios
- **Error Simulation**: Test error handling paths
- **Health Monitoring**: Verify service status

## Configuration

### Quarkus OpenAPI Settings (application.yml)
```yaml
quarkus:
  smallrye-openapi:
    path: /openapi
    info-title: Camp Sarafrika USSD API
    info-version: 1.0.0
    info-description: USSD service for camp registration
    
  swagger-ui:
    always-include: true
    path: /swagger-ui
    title: Camp Sarafrika API Documentation
```

### Customization Options
- **Theme**: Swagger UI theme customization
- **Authentication**: API key/OAuth integration
- **CORS**: Cross-origin request configuration
- **Filtering**: Endpoint visibility controls

## Development Workflow

### Adding New Endpoints
1. **Add JAX-RS Annotations**: Standard REST endpoint setup
2. **Add OpenAPI Annotations**: Document with `@Operation`, `@Parameter`, `@ApiResponse`
3. **Provide Examples**: Include realistic request/response examples
4. **Test Documentation**: Verify in Swagger UI

### Documentation Standards
- **Descriptions**: Clear, concise explanations
- **Examples**: Realistic, relevant data
- **Response Codes**: All possible HTTP status codes
- **Parameters**: Complete validation and format details

### Best Practices
- **Consistency**: Use consistent terminology and formatting
- **Completeness**: Document all endpoints and parameters
- **Accuracy**: Keep examples current with actual API behavior
- **Maintenance**: Update documentation with code changes

## Security Considerations

### API Documentation Security
- **Production Access**: Limit Swagger UI access in production
- **Sensitive Data**: Avoid real credentials in examples
- **Rate Limiting**: Protect documentation endpoints
- **Authentication**: Secure admin-only endpoints

### Example Data Guidelines
- **Phone Numbers**: Use fake/test numbers (+254712345678)
- **Session IDs**: Use generic example formats
- **Reference Codes**: Use example patterns (CS-A4T9B1C7)
- **Personal Data**: Use obviously fake names and data

## Integration with Development Tools

### IDE Integration
- **IntelliJ IDEA**: OpenAPI plugin support
- **VS Code**: OpenAPI extensions for validation
- **API Testing**: Postman/Insomnia import support

### CI/CD Integration
- **Documentation Generation**: Automated spec generation
- **API Validation**: Contract testing integration
- **Deployment**: Documentation deployment with application

## Monitoring and Analytics

### Documentation Usage
- **Access Logs**: Track Swagger UI usage
- **Popular Endpoints**: Monitor frequently accessed documentation
- **Error Patterns**: Identify common integration issues

### Performance Considerations
- **Caching**: OpenAPI spec caching for performance
- **CDN**: Static asset delivery optimization
- **Compression**: Gzip compression for large specs

---

## Summary

The OpenAPI documentation provides:
- ✅ **Complete API Coverage**: All endpoints fully documented
- ✅ **Interactive Testing**: Swagger UI with try-it-out functionality  
- ✅ **Detailed Examples**: Realistic request/response scenarios
- ✅ **Schema Documentation**: Complete data model specifications
- ✅ **Developer-Friendly**: Clear descriptions and examples
- ✅ **Production-Ready**: Multi-environment server configuration
- ✅ **Standards-Compliant**: OpenAPI 3.0 specification compliance

**Access URL**: `http://localhost:8080/swagger-ui`

---
**Last Updated**: September 8, 2025  
**Documentation Version**: 1.0.0