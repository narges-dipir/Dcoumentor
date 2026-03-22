package app.narges.documentor.feature.articledetails.screen.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.narges.documentor.feature.articledetails.state.ArticleCountUiState

@Composable
fun ArticleCountScreen(
    uiState: ArticleCountUiState,
    onCountChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Count Article")
            if (uiState.articleName.isNotBlank()) {
                Text(text = "${uiState.articleName} (${uiState.articleNumber})")
            }

            OutlinedTextField(
                value = uiState.countInput,
                onValueChange = onCountChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("article_count_input"),
                label = { Text("Input for Pieces of Article") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                isError = uiState.countError != null,
                supportingText = { uiState.countError?.let { Text(it) } },
                enabled = !uiState.isLoading && !uiState.isSaving,
            )

            uiState.errorMessage?.let {
                Text(text = it)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("article_count_save_button"),
                enabled = !uiState.isLoading && !uiState.isSaving,
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("save")
                }
            }
        }
    }
}
