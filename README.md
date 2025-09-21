Anime Showcase App
This is a simple Android application built to display a list of top anime series,
provide detailed information on each, and offer offline data access. 
The app is developed as a solution to the Seekho Android Developer Assignment, 
focusing on clean architecture, best practices, and robust error handling.

Features Implemented
Anime List Page: Fetches and displays a paginated list of top anime from the Jikan API. 
Each list item shows the anime's title, number of episodes, rating, and poster image.

Anime Detail Page: Shows comprehensive details for a selected anime, including:

Title

Synopsis

Genres

Main Cast

Number of Episodes

Rating

Video playback for trailers. The app intelligently switches between a WebView for YouTube trailers and ExoPlayer for direct video links.

Local Database with Room: All fetched anime data is stored locally using the Room persistence library, 
ensuring the app functions seamlessly in offline mode.

Offline Mode & Syncing: The app is designed to work without an active internet connection. 
It displays cached data when offline and automatically syncs with the API to get the latest data when online.

Error Handling: Robust error handling is implemented for API calls, database operations, and network connectivity changes to provide a smooth user experience.

Search Functionality: A search bar in the main activity allows users to filter the displayed list based on anime titles,
with a search performed on the cached local data for efficiency.

Back Button Navigation: A back button is integrated into the detail page's toolbar to allow for easy navigation back to the main list.

Architecture and Libraries
The app follows the MVVM (Model-View-ViewModel) architectural pattern to ensure a clean, testable, and maintainable codebase.

Retrofit: For making all API calls to the Jikan API.

Room: For local database operations, enabling offline support and data caching.

StateFlow: For reactive data handling between the ViewModel and the UI.

Glide: For efficient and performant image loading from URLs.

ExoPlayer: For playing direct video streams.

WebView: Used as a fallback to embed and play YouTube trailers, as ExoPlayer cannot play YouTube URLs directly.

View Binding: For safe and easy interaction with views.

Assumptions Made
The app assumes that the Jikan API endpoints used (/top/anime and /anime/{anime_id}) will consistently return data in the expected format.

The WebView solution for YouTube trailers is a pragmatic choice to avoid the complexity of a third-party YouTube parsing library.

The app handles only the primary trailer URL and does not account for multiple trailers or different video sources.

Known Limitations
The search functionality is limited to the data already cached in the local database. 
If an anime is not in the cached list, it cannot be found via search.

The app does not handle the case where a network request for a specific anime fails and no local data is available.