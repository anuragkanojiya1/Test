package com.example.arwebmodel

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Load3DModelScreen() {
    val context = LocalContext.current

    // Display the 3D model viewer
    Render3DModel(
        context = context,
        modelUrl = "https://modelviewer.dev/shared-assets/models/Astronaut.glb"
    )
}

@Composable
fun Render3DModel(context: Context, modelUrl: String) {
    val sceneView = remember { SceneView(context) }
    val engine = rememberEngine()
    // State to track whether the model is loaded
    var isModelLoaded by remember { mutableStateOf(false) }

    // Load the 3D model asynchronously
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val modelLoader = ModelLoader(engine, context)
                val modelInstance = modelLoader.createModelInstance(modelUrl)
                withContext(Dispatchers.Main) {
                    val modelNode = ModelNode(modelInstance = modelInstance, scaleToUnits = 0.3f)
                    sceneView.addChildNode(modelNode)
                    isModelLoaded = true
                }
            } catch (e: Exception) {
                Log.e("3DModel", "Error loading model: ${e.localizedMessage}")
            }
        }
    }

    // Render the SceneView in Jetpack Compose
    AndroidView(
        factory = { sceneView },
        modifier = Modifier.fillMaxSize(),
        update = { it }
    )
}
