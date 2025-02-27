package com.valokafor.duckit.ui.postlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valokafor.duckit.R
import com.valokafor.duckit.domain.Post
import com.valokafor.duckit.ui.theme.DuckItTheme

@Composable
fun PostListScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddPost: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    val postListViewModel: PostListViewModel = hiltViewModel()
    val postState by postListViewModel.postsState.collectAsState()
    val isLoggedIn by postListViewModel.isLoggedIn.collectAsState()

    PostListScreenContent(
        uiState = postState,
        isLoggedIn = isLoggedIn,
        upVotePost = { postId -> postListViewModel.upVotePost(postId) },
        downVotePost = { postId -> postListViewModel.downVotePost(postId) },
        fetchPosts = { postListViewModel.fetchPosts() },
        onBackPressed = onNavigateBack,
        onAddPostClick = onNavigateToAddPost,
        onLoginClick = onNavigateToLoginScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreenContent(
    uiState: PostsState,
    isLoggedIn: Boolean,
    upVotePost: (String) -> Unit,
    downVotePost: (String) -> Unit,
    fetchPosts: () -> Unit,
    onBackPressed: () -> Unit,
    onAddPostClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val screenTitle = stringResource(id = R.string.post_list_screen_title)
    var showLoginDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Login dialog
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Login Required") },
            text = { Text("You need to be logged in to vote on posts.") },
            confirmButton = {
                Button(onClick = {
                    showLoginDialog = false
                    onLoginClick()
                }) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle) },
                actions = {
                    if (!isLoggedIn) {
                        TextButton(onClick = onLoginClick) {
                            Text("Login", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPostClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new post"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is PostsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PostsState.Success -> {
                    if (uiState.posts.isEmpty()) {
                        Text(
                            text = "No posts available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.posts) { post ->
                                PostItem(
                                    post = post,
                                    onUpvote = {
                                        if (isLoggedIn) {
                                            upVotePost(post.id)
                                        } else {
                                            pendingAction = { upVotePost(post.id) }
                                            showLoginDialog = true
                                        }
                                    },
                                    onDownVote = {
                                        if (isLoggedIn) {
                                            downVotePost(post.id)
                                        } else {
                                            pendingAction = { downVotePost(post.id) }
                                            showLoginDialog = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is PostsState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { fetchPosts() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PostListScreenPreview() {
    DuckItTheme {
        PostListScreenContent(
            uiState = PostsState.Success(
                listOf(
                    Post(
                        id = "1",
                        headline = "Majestic Mallard Swimming in a Crystal Clear Lake",
                        image = "https://unsplash.com/photos/yellow-and-brown-duckling-JDzoTGfoogA",
                        upvotes = 245,
                        isUpvoted = true,
                        isDownvoted = false
                    ),
                    Post(
                        id = "2",
                        headline = "Baby Ducklings Following Their Mother in a Perfect Line",
                        image = "https://unsplash.com/photos/mallard-duck-swimming-on-body-of-water-LJSH3NOTLwc",
                        upvotes = 512,
                        isUpvoted = false,
                        isDownvoted = false
                    ),
                    Post(
                        id = "3",
                        headline = "Incredible Wood Duck with Its Vibrant Colorful Plumage",
                        image = "https://unsplash.com/photos/white-duck-in-a-body-of-water-during-daytime-K8lVYS_u0UE",
                        upvotes = 178,
                        isUpvoted = false,
                        isDownvoted = true
                    ),
                    Post(
                        id = "4",
                        headline = "Duck Enjoying Bread at the Local Park",
                        image = "https://unsplash.com/photos/mallard-ducks-YU4rnz81TV4",
                        upvotes = 89,
                        isUpvoted = false,
                        isDownvoted = false
                    ),
                    Post(
                        id = "5",
                        headline = "Rare Mandarin Duck Spotted in Central Park",
                        image = "https://unsplash.com/photos/a-couple-of-ducks-standing-next-to-a-body-of-water-9fww5E--GM4",
                        upvotes = 732,
                        isUpvoted = true,
                        isDownvoted = false
                    )
                )
            ),
            upVotePost = {},
            downVotePost = {},
            fetchPosts = {},
            onBackPressed = {},
            onAddPostClick = {},
            onLoginClick = {},
            isLoggedIn = false
        )
    }
}