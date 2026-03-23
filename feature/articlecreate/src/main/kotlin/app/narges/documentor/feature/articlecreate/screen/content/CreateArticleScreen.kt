package app.narges.documentor.feature.articlecreate.screen.content

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.narges.documentor.feature.articlecreate.state.CreateArticleUiState

@Composable
fun CreateArticleScreen(
    uiState: CreateArticleUiState,
    onArticleNameChanged: (String) -> Unit,
    onArticleNumberChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val articleNumberFocusRequester = remember { FocusRequester() }

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
            Text(text = "Create a new Article")

            OutlinedTextField(
                value = uiState.articleName,
                onValueChange = onArticleNameChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_article_name_input"),
                label = { Text("Input for Article Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { articleNumberFocusRequester.requestFocus() },
                ),
                isError = uiState.articleNameError != null,
                supportingText = { uiState.articleNameError?.let { Text(it) } },
                enabled = !uiState.isSaving,
            )

            OutlinedTextField(
                value = uiState.articleNumberInput,
                onValueChange = onArticleNumberChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(articleNumberFocusRequester)
                    .testTag("create_article_number_input"),
                label = { Text("Input for Article Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                isError = uiState.articleNumberError != null,
                supportingText = { uiState.articleNumberError?.let { Text(it) } },
                enabled = !uiState.isSaving,
            )

            uiState.errorMessage?.let {
                Text(text = it)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSaveClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_article_save_button"),
                enabled = !uiState.isSaving,
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
