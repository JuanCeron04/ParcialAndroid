package com.example.parcial.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parcial.data.model.Character
import com.example.parcial.ui.MainViewModel
import com.example.parcial.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    viewModel: MainViewModel,
    onCharacterClick: (Int) -> Unit
) {
    val state by viewModel.charactersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rick & Morty Characters") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    val characters = (state as UiState.Success<List<Character>>).data
                    LazyColumn {
                        items(characters) { character ->
                            CharacterItem(character = character, onClick = { onCharacterClick(character.id) })
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = (state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchCharacters() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterItem(character: Character, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = character.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${character.species} - ${character.status}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}