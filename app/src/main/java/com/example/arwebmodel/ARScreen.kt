package com.example.arwebmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger
import kotlin.random.Random


private val kModelFile = listOf(
    "models/forest2.glb",
    "models/forest2.glb"
)
@Composable
fun ARScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val childNodes = rememberNodes()

        var trackingFailureReason by remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }

        var frame by remember { mutableStateOf<Frame?>(null) }
        var modelNode by remember { mutableStateOf<ModelNode?>(null) }
        var modelNode2 by remember { mutableStateOf<ModelNode?>(null) }

        val context = LocalContext.current
        var score by remember { mutableStateOf(0) } // Initialize score
        var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

//        DisposableEffect(context) {
//            mediaPlayer = MediaPlayer.create(context, R.raw.ufo_sound)
//
//            onDispose {
//                mediaPlayer?.release()
//            }
//        }

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            modelLoader = modelLoader,
            sessionConfiguration = { session, config ->
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode =
                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            planeRenderer = false,
            onSessionUpdated = { _, updatedFrame ->
                frame = updatedFrame

                if (childNodes.isEmpty()) {

//                    updatedFrame.getUpdatedPlanes()
//                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
//                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
//                            val giftBoxNode = AnchorNode(engine, anchor).apply {
//                                modelNode2 = ModelNode(
//                                    modelInstance = modelLoader.createModelInstance(kModelFile[0]),
//                                    scaleToUnits = 1f
//                                ).apply {
//                                    isRotationEditable = true
//                                    isScaleEditable = true
//                                    isPositionEditable = true
//                                    isEditable = true
//                                    isTouchable = true
//                                    name = "giftbox"
//                                }
//                                modelNode2?.position = Float3(
//                                    Random.nextFloat(), 0f,
//                                    Random.nextFloat()
//                                )
//
//                                addChildNode(modelNode2!!)
//                            }
//                            childNodes += giftBoxNode
//                            childNodes.indexOf(giftBoxNode)
//                            Log.d("gift Node:", childNodes.indexOf(giftBoxNode).toString())
//                        }

                    updatedFrame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                            val helmetNode = AnchorNode(engine, anchor).apply {
                                modelNode = ModelNode(
                                    modelInstance = modelLoader.createModelInstance(kModelFile[0]),
                                    scaleToUnits = 1f
                                ).apply {
                                    isRotationEditable = true
                                    isEditable = true
                                    name = "helmet"
                                }
                                addChildNode(modelNode!!)
                            }
                            childNodes += helmetNode
                        }
                }
            }
        )
    }
}