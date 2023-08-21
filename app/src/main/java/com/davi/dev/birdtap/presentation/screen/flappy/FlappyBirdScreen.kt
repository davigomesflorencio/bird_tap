package com.davi.dev.birdtap.presentation.screen.flappy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davi.dev.birdtap.R

val skyColor = Color(0xFF74B9F5)
val lightGreen = Color(0xFF75B6E0)
val darkGreen = Color(0xFF276C3D)
val brown = Color(0xFF755B55)

private val obstacleGreenNormal = Color(0xFF75BE2F)
private val obstacleGreenDark = Color(0xFF518718)
private val obstacleGreenLight = Color(0xFF9AE456)
private val obstacleGreenExtraLight = Color(0xFFD8FF80)
private val obstacleBorder = Color(0xFF52513A)
private val obstacleBorderWidth = 1.dp

@Composable
fun FlappyBirdScreen() {
    val gameViewModel = remember { FlappyBirdViewModel() }

    Surface {
        Column(Modifier.fillMaxSize()) {
            GameArea(
                gameViewModel, modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
            )
        }
    }
}

@Composable
fun GameArea(viewModel: FlappyBirdViewModel, modifier: Modifier) {

    val state = viewModel.state.observeAsState(FlappyGameUi.NotStarted)

    BoxWithConstraints(modifier) {
        LaunchedEffect(viewModel) {
            viewModel.onGameBoundsSet(maxWidth.value, maxHeight.value)
        }

        Box(
            Modifier
                .clickable(remember { MutableInteractionSource() }, indication = null, onClick = viewModel::onTap)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_night),
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(0.dp, 3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Text(text = "SCORE: ${viewModel.scoreBoard.value!!.current}", fontSize = 10.sp, textAlign = TextAlign.Center)
                    Text(text = "  BEST: ${viewModel.scoreBoard.value!!.best}", fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }

            val currentState = state.value
            val birdAlignment = currentState.getBirdAlignment()
            Bird(currentState, Modifier.align(birdAlignment))

            currentState.getObstacles().forEach { obstacle ->
                DrawObstacle(
                    Modifier
                        .size(obstacle.widthDp.dp, obstacle.heightDp.dp)
                        .graphicsLayer {
                            rotationX = when (obstacle.orientation) {
                                ObstaclePosition.Up -> 180f
                                ObstaclePosition.Down -> 0f
                            }
                        }
                        .absoluteOffset(
                            x = obstacle.leftMargin.dp, y = obstacle.topMargin.dp
                        ))
            }

            if (currentState !is FlappyGameUi.Playing) {
                Text(
                    "Toque para começar",
                    fontSize = 10.sp,
                    modifier = Modifier.align(
                        BiasAlignment(verticalBias = -0.3f, horizontalBias = 0f)
                    )
                )
            }
        }
    }
}

private fun FlappyGameUi.getBirdAlignment() = when (this) {
    is FlappyGameUi.NotStarted -> Alignment.Center
    is FlappyGameUi.Finished -> Alignment.BottomCenter
    is FlappyGameUi.Playing -> BiasAlignment(verticalBias = bird.verticalBias, horizontalBias = 0f)
}

private fun FlappyGameUi.getObstacles(): List<Obstacle> = when (this) {
    is FlappyGameUi.Playing -> obstacles
    else -> emptyList()
}

@Composable
private fun Bird(state: FlappyGameUi, modifier: Modifier) {
    val birdRotation = when (state) {
        is FlappyGameUi.NotStarted, is FlappyGameUi.Finished -> 0f

        is FlappyGameUi.Playing -> state.bird.rotation
    }
    Image(painter = painterResource(id = R.drawable.flappy_green), contentDescription = "Bird icon", modifier = modifier
        .size(20.dp)
        .graphicsLayer { rotationZ = birdRotation })
}

@Composable
fun DrawObstacle(modifier: Modifier) {
    Box(modifier) {
        ObstacleBody(
            ObstacleBorderMode.Sides, modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxSize()
        )
        ObstacleBody(
            ObstacleBorderMode.AllBorders, modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

private enum class ObstacleBorderMode { AllBorders, Sides }

@Composable
private fun ObstacleBody(borderMode: ObstacleBorderMode, modifier: Modifier) {
    BoxWithConstraints(modifier) {
        val canvasModifier = Modifier
            .fillMaxSize()
            .composed {
                when (borderMode) {
                    ObstacleBorderMode.AllBorders -> border(
                        width = obstacleBorderWidth, color = obstacleBorder, shape = RoundedCornerShape(3.dp)
                    )

                    else -> this
                }
            }

        Canvas(canvasModifier) {
            fun line(color: Color, width: Float, offset: Float) {
                drawRect(color, size = Size(width, size.height), topLeft = Offset(offset, 0f))
            }

            drawRect(color = obstacleGreenNormal)

            line(obstacleGreenLight, width = 5f, offset = 0f)
            line(obstacleGreenNormal, width = 5f, offset = 5f)
            line(obstacleGreenExtraLight, width = 5f, offset = 10f)
//
            line(obstacleGreenDark, width = 5f, offset = size.width - 10f)
            line(obstacleGreenDark, width = 5f, offset = size.width - 10f)

            if (borderMode == ObstacleBorderMode.Sides) {
                val borderWidth = obstacleBorderWidth.toPx()
                line(obstacleBorder, borderWidth, offset = -borderWidth)
                line(obstacleBorder, borderWidth, offset = size.width)
            }
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND, showBackground = true, showSystemUi = true
)
@Composable
fun GamePreview() {
    FlappyBirdTheme {
        FlappyBirdScreen()
    }
}