package com.example.data.telemetry

import android.view.Choreographer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FpsMonitor {
    private val _fps = MutableStateFlow(60.0f)
    val fps: StateFlow<Float> = _fps.asStateFlow()

    private var isRunning = false
    private var lastFrameTimeNanos: Long = 0L
    private var frameCount = 0
    private var lastFpsCalculationTime = 0L

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning) return

            if (lastFrameTimeNanos != 0L) {
                val frameDurationMs = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000.0
                frameCount++

                val now = System.currentTimeMillis()
                if (now - lastFpsCalculationTime >= 500) {
                    val seconds = (now - lastFpsCalculationTime) / 1000.0
                    val currentFps = (frameCount / seconds).toFloat().coerceIn(10.0f, 165.0f)
                    _fps.value = (Math.round(currentFps * 10) / 10.0f)
                    frameCount = 0
                    lastFpsCalculationTime = now
                }
            } else {
                lastFpsCalculationTime = System.currentTimeMillis()
            }
            lastFrameTimeNanos = frameTimeNanos

            if (isRunning) {
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        lastFrameTimeNanos = 0L
        frameCount = 0
        lastFpsCalculationTime = System.currentTimeMillis()
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stop() {
        isRunning = false
        lastFrameTimeNanos = 0L
    }
}
