package com.example.arwebmodel

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Databases
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
           ARScreen()
        }
    }
}

@Composable
fun AppwriteDatabaseScreen(context: Context) {
    val client = remember {
        Client(context = context)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("67967ef40000a54f307f")
    }

    val database = remember { Databases(client) }
    val databaseId = "67967f1500142051f871"
    val collectionId = "67967fe50031892ab5b0"

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var documents by remember { mutableStateOf(emptyList<Map<String, Any>>()) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            database.createDocument(
                                databaseId = databaseId,
                                collectionId = collectionId,
                                documentId = id,
                                data = mapOf("id" to id, "name" to name)
                            )
                            message = "Document added successfully!"
                            fetchDocuments(database, databaseId, collectionId) { fetchedDocs ->
                                documents = fetchedDocs
                            }
                        } catch (e: AppwriteException) {
                            message = "Error adding document: ${e.message}"
                        }
                    }
                }
            ) {
                Text("Add")
            }

            Button(
                onClick = {
                    scope.launch {
                        try {
                            database.updateDocument(
                                databaseId = databaseId,
                                collectionId = collectionId,
                                documentId = id,
                                data = mapOf("id" to id, "name" to name)
                            )
                            message = "Document updated successfully!"
                            fetchDocuments(database, databaseId, collectionId) { fetchedDocs ->
                                documents = fetchedDocs
                            }
                        } catch (e: AppwriteException) {
                            message = "Error updating document: ${e.message}"
                        }
                    }
                }
            ) {
                Text("Update")
            }

            Button(
                onClick = {
                    scope.launch {
                        try {
                            database.deleteDocument(
                                databaseId = databaseId,
                                collectionId = collectionId,
                                documentId = id
                            )
                            message = "Document deleted successfully!"
                            fetchDocuments(database, databaseId, collectionId) { fetchedDocs ->
                                documents = fetchedDocs
                            }
                        } catch (e: AppwriteException) {
                            message = "Error deleting document: ${e.message}"
                        }
                    }
                }
            ) {
                Text("Delete")
            }

            Button(
                onClick = {
                    scope.launch {
                        fetchDocuments(database, databaseId, collectionId) { fetchedDocs ->
                            documents = fetchedDocs
                        }
                    }
                }
            ) {
                Text("Fetch")
            }
        }

        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) { 
            items(documents.size) { index ->
                val doc = documents[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "ID: ${doc["id"]}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Name: ${doc["name"]}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

suspend fun fetchDocuments(
    database: Databases,
    databaseId: String,
    collectionId: String,
    onDocumentsFetched: (List<Map<String, Any>>) -> Unit
) {
    try {
        val response = database.listDocuments(databaseId = databaseId, collectionId = collectionId)
        val docs = response.documents.map { it.data as Map<String, Any> }
        onDocumentsFetched(docs)
    } catch (e: AppwriteException) {
        onDocumentsFetched(emptyList())
    }
}
