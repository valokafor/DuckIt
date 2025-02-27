package com.valokafor.duckit.ui.addpost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.valokafor.duckit.R
import timber.log.Timber

@Composable
fun AddNewPostScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToLoginScreen: () -> Unit
) {
    val viewModel: AddNewPostViewModel = hiltViewModel()
    val uiState by viewModel.newPostState.collectAsState()
    val headline by viewModel.headline.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Redirect to login if not authenticated
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onNavigateToLoginScreen()
        }
    }

    // Only show content if logged in
    if (isLoggedIn) {
        AddNewPostScreenContent(
            uiState = uiState,
            headline = headline,
            imageUrl = imageUrl,
            onHeadlineChanged = viewModel::updateHeadline,
            onImageUrlChanged = viewModel::updateImageUrl,
            onSubmitButtonClicked = viewModel::createPost,
            onNavigateBack = onNavigateBack
        )

        LaunchedEffect(uiState) {
            if (uiState is NewPostState.Success) {
                viewModel.resetState()
                onNavigateBack()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewPostScreenContent(
    uiState: NewPostState,
    headline: String = "",
    imageUrl: String = "",
    onHeadlineChanged: (String) -> Unit,
    onImageUrlChanged: (String) -> Unit,
    onSubmitButtonClicked: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val screenTitle = stringResource(id = R.string.add_new_post_title)
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = headline,
                    onValueChange = onHeadlineChanged,
                    label = { Text("Headline") },
                    placeholder = { Text(stringResource(R.string.enter_an_eye_catching_headline)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChanged,
                    label = { Text(stringResource(R.string.image_url)) },
                    placeholder = { Text(stringResource(R.string.enter_url_for_duck_image)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (imageUrl.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Duck image preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onError = {
                                Timber.e("Failed to load image: $imageUrl")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState is NewPostState.Error) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = onSubmitButtonClicked,
                    enabled = uiState !is NewPostState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState is NewPostState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.submit_duck))
                    }
                }
            }
        }
    }
}