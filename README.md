# PopularMovies
An Android mobile movie database featuring an endlessly scrolling list of movies, providing rating, runtime, trailers, and reviews.

#### Endless Scroll
**MovieListFragment** makes asynchronous network requests for new pages from themoviedb.org as necessary and tracks the **RecyclerViewAdapter**'s current position, fetching the next page before the user has scrolled to the bottom of the page.

#### Favorites
Selecting a movie poster launches the **MovieDetailActivity** which loads the **MovieDetailFragment**. Selecting the star adds the movie to the Favorites SQLite database via the **MovieContentProvider**. A list of Favorites can be accessed from the **MovieListFragment** menu.

#### Tablet Support
**MovieListActivity** checks the device type at runtime and inflates the single or dual pane layouts as required. The **OnFavoritesChangeListener** and **OnMovieSelectionChangeListener** interfaces properly handle communication between the **MovieListFragment** and **MovieDetailFragment**.

#### *API_KEY REQUIRED!*
The following line must be added to your gradle.properties file:

`API_KEY="insert-your-api-key-here"`
