package com.example.arwebmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arwebmodel.ui.theme.ARWebModelTheme
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.collision.Vector3
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.File
import java.io.FileOutputStream

private val kModelFile = listOf(
    "https://modelviewer.dev/shared-assets/models/Astronaut.glb",
    "https://modelviewer.dev/shared-assets/models/Astronaut.glb",
    "https://modelviewer.dev/shared-assets/models/Astronaut.glb"
)

class MainActivity : ComponentActivity() {
    private var modelIndex = 0 // Keeps track of the current model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Function to download the GLB model and save it locally
            fun downloadModel(url: String, outputFile: File): Boolean {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Failed to download file: $url")

                    response.body?.let { body ->
                        body.byteStream().use { input ->
                            FileOutputStream(outputFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
                return true
            }

// Usage in AR scene setup
            val modelUrl = "https://modelviewer.dev/shared-assets/models/Astronaut.glb"
            val localFile = File(context.filesDir, "Astronaut.glb")

// Download the GLB model
            val isDownloaded = downloadModel(modelUrl, localFile)

            if (isDownloaded) {
                // Now load the model from the downloaded file
                loadModelInstanceAsync(localFile.path) { modelInstance ->
                    modelInstance?.let {
                        val modelNode = Node()
                        modelNode.renderable = it
                        modelNode.worldPosition = Vector3(0f, 0f, 0f)
                        session.addChild(modelNode)
                    }
                }
            }

        }
    }
}