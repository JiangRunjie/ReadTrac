# Network Integration Documentation

This document provides details about the network module implementation for fetching external book data in the ReadTrac application.

## Architecture Overview

The network integration follows a clean architecture approach:

1. **API Service** (`BookApiService`): Defines the API endpoints using Retrofit annotations
2. **Data Models** (`BookApiResponse`, etc.): Represents the structure of API responses
3. **Network Client** (`NetworkClient`): Handles making API requests with error handling
4. **Repository** (`BookRepository`): Coordinates between local and remote data sources
5. **Mapper** (`BookApiMapper`): Converts between API models and domain models

## API Configuration

The application uses the Google Books API to fetch book data. The base URL and endpoints are configured in `NetworkModule.kt`:

- **Base URL**: `https://www.googleapis.com/books/v1/`
- **Volumes Endpoint**: Used for searching books and getting recommendations

## Error Handling

Network errors are handled through a sealed class `NetworkResult<T>` which has three states:

- `Success`: Contains the API response data
- `Error`: Contains error message and optional error code
- `Loading`: Represents the loading state

The `NetworkClient` wraps all API calls with proper error handling for:

- Network connectivity issues
- API errors
- Parsing errors

## Offline Support

The application implements a fallback strategy when network calls fail:

1. First attempts to fetch data from the external API
2. If that fails, falls back to local recommendation engine
3. For search operations, falls back to empty results if external search fails

## Updating API Endpoints

To update or modify API endpoints:

1. **Change Base URL**:

   - Open `NetworkModule.kt`
   - Modify the `BASE_URL` constant to point to the new API endpoint

2. **Add New Endpoints**:

   - Open `BookApiService.kt`
   - Add new methods with appropriate Retrofit annotations
   - Example for adding a new endpoint:

   ```kotlin
   @GET("newEndpoint")
   suspend fun newApiMethod(
       @Query("param") paramValue: String
   ): Response<ResponseType>
   ```

3. **Update Response Models**:

   - If the API response format changes, update the data models in `BookApiResponse.kt`

4. **Update Mapper**:
   - If changes to the models are made, update the `BookApiMapper.kt` to correctly map between API models and domain entities

## Maintaining API Keys

If the API requires authentication:

1. Create a `local.properties` file (if not exists) in the project root
2. Add the API key: `api.key=your_api_key_here`
3. Access it in code through the BuildConfig:

   ```kotlin
   val apiKey = BuildConfig.API_KEY
   ```

4. Add it as a query parameter in the API service:
   ```kotlin
   @GET("volumes")
   suspend fun searchBooks(
       @Query("q") query: String,
       @Query("key") apiKey: String
   ): Response<BookSearchResponse>
   ```

## Testing the Network Module

The network module can be tested using:

1. Unit tests with MockWebServer
2. Integration tests with actual API (be mindful of rate limits)
3. UI tests with mocked network responses

## Performance Considerations

1. Responses are cached when possible
2. Image loading is handled by Coil library with built-in caching
3. Network timeouts are configured to 15 seconds to balance user experience
