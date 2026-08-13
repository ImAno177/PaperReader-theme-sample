package dev.paperreader.extensions.sample.theme

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.content.pm.PackageManager
import dev.paperreader.extensions.api.CommunityTheme
import dev.paperreader.extensions.api.ExtensionFailure
import dev.paperreader.extensions.api.ExtensionFailureCode
import dev.paperreader.extensions.api.IPaperThemeCallback
import dev.paperreader.extensions.api.IPaperThemeService
import dev.paperreader.extensions.api.PaperExtensionContract
import dev.paperreader.extensions.api.ThemeDecoration
import dev.paperreader.extensions.api.ThemeExtensionDescriptor
import dev.paperreader.extensions.api.ThemeFontFamily
import dev.paperreader.extensions.api.ThemePalette
import dev.paperreader.extensions.api.ThemeSemanticIcon
import dev.paperreader.extensions.api.requireValidIconPathData
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class BlueprintThemeService : Service() {
    private val executor = Executors.newFixedThreadPool(2)
    private val requests = ConcurrentHashMap<String, Future<*>>()

    private val binder = object : IPaperThemeService.Stub() {
        override fun getDescriptor(): Bundle {
            requirePaperReaderCaller()
            return THEME_DESCRIPTOR.toBundle()
        }

        override fun getTheme(requestId: String, themeId: String, callback: IPaperThemeCallback) {
            requirePaperReaderCaller()
            submit(requestId, callback) {
                require(themeId == THEME_ID)
                callback.onTheme(theme(requestId).toBundle())
            }
        }

        override fun openIcon(
            requestId: String,
            themeId: String,
            semanticKey: String,
            callback: IPaperThemeCallback,
        ) {
            requirePaperReaderCaller()
            submit(requestId, callback) {
                require(themeId == THEME_ID)
                val icon = requireNotNull(ThemeSemanticIcon.entries.firstOrNull { it.wireValue == semanticKey })
                val vectorXml = assets.open("icons/ic_tabler_${icon.wireValue}.xml")
                    .bufferedReader()
                    .use { it.readText() }
                val pathData = requireNotNull(PATH_DATA.find(vectorXml)?.groupValues?.get(1))
                val bytes = pathData.encodeToByteArray()
                requireValidIconPathData(bytes)
                val pipe = ParcelFileDescriptor.createPipe()
                executor.execute {
                    ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]).use { it.write(bytes) }
                }
                pipe[0].use { callback.onIcon(requestId, it) }
            }
        }

        override fun cancel(requestId: String) {
            requirePaperReaderCaller()
            requests.remove(requestId)?.cancel(true)
        }
    }

    override fun onBind(intent: Intent?): IBinder? =
        binder.takeIf { intent?.action == PaperExtensionContract.THEME_SERVICE_ACTION }

    override fun onDestroy() {
        requests.values.forEach { it.cancel(true) }
        executor.shutdownNow()
        super.onDestroy()
    }

    private fun submit(requestId: String, callback: IPaperThemeCallback, block: () -> Unit) {
        require(requestId.isNotBlank() && requestId.length <= PaperExtensionContract.MAX_REQUEST_ID_CHARACTERS)
        val task = FutureTask {
            try {
                block()
            } catch (error: Exception) {
                if (!Thread.currentThread().isInterrupted) {
                    callback.onFailure(
                        ExtensionFailure(
                            requestId = requestId,
                            code = ExtensionFailureCode.INVALID_RESPONSE,
                            message = error.message?.take(512) ?: "Theme response failed",
                        ).toBundle(),
                    )
                }
            } finally {
                requests.remove(requestId)
            }
        }
        require(requests.putIfAbsent(requestId, task) == null) { "Duplicate request ID" }
        executor.execute(task)
    }

    private fun theme(requestId: String) = CommunityTheme(
        requestId = requestId,
        themeId = THEME_ID,
        displayName = "Blueprint",
        lightPalette = ThemePalette(
            canvas = color(0xFFF4F8FB),
            surface = color(0xFFFFFFFF),
            surfaceMuted = color(0xFFDCEAF4),
            ink = color(0xFF102A43),
            inkMuted = color(0xFF486581),
            border = color(0xFF1F5F8B),
            primary = color(0xFF147DAD),
            onPrimary = color(0xFFFFFFFF),
            primaryContainer = color(0xFFCDEDFC),
            onPrimaryContainer = color(0xFF063A54),
            secondary = color(0xFF7C3AED),
            onSecondary = color(0xFFFFFFFF),
            secondaryContainer = color(0xFFEDE9FE),
            onSecondaryContainer = color(0xFF3B1678),
            success = color(0xFF15803D),
            warning = color(0xFFB45309),
            danger = color(0xFFB91C1C),
            emptyStateAccent = color(0xFF147DAD),
            selection = color(0xFFBAE6FD),
            hardShadow = color(0xFF102A43),
        ),
        darkPalette = ThemePalette(
            canvas = color(0xFF0B1721),
            surface = color(0xFF102636),
            surfaceMuted = color(0xFF183B50),
            ink = color(0xFFF0F9FF),
            inkMuted = color(0xFFB6D2E3),
            border = color(0xFF7DD3FC),
            primary = color(0xFF7DD3FC),
            onPrimary = color(0xFF082433),
            primaryContainer = color(0xFF164E63),
            onPrimaryContainer = color(0xFFCFFAFE),
            secondary = color(0xFFC4B5FD),
            onSecondary = color(0xFF2E1065),
            secondaryContainer = color(0xFF4C1D95),
            onSecondaryContainer = color(0xFFEDE9FE),
            success = color(0xFF86EFAC),
            warning = color(0xFFFCD34D),
            danger = color(0xFFFCA5A5),
            emptyStateAccent = color(0xFF7DD3FC),
            selection = color(0xFF155E75),
            hardShadow = color(0xFF020617),
        ),
        cornerRadiusDp = 6f,
        borderWidthDp = 1f,
        shadowOffsetDp = 0f,
        titleFont = ThemeFontFamily.SYSTEM_SERIF,
        bodyFont = ThemeFontFamily.SYSTEM_SANS,
        labelFont = ThemeFontFamily.SYSTEM_MONOSPACE,
        decoration = ThemeDecoration.NONE,
        iconKeys = ThemeSemanticIcon.entries.toSet(),
    )

    private fun requirePaperReaderCaller() {
        val packages = packageManager.getPackagesForUid(Binder.getCallingUid()).orEmpty()
        require(packages.contains(PAPERREADER_PACKAGE)) { "Caller is not PaperReader" }
        require(
            packageManager.hasSigningCertificate(
                PAPERREADER_PACKAGE,
                BuildConfig.PAPERREADER_HOST_SIGNER_SHA256.hexToBytes(),
                PackageManager.CERT_INPUT_SHA256,
            ),
        ) { "PaperReader signer is not trusted" }
    }

    private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    private fun color(value: Long): Int = value.toInt()

    private companion object {
        const val PAPERREADER_PACKAGE = "dev.paperreader.app"
        const val THEME_ID = "sample.blueprint"
        val PATH_DATA = Regex("android:pathData=\\\"([^\\\"]+)\\\"")

        val THEME_DESCRIPTOR = ThemeExtensionDescriptor(
            packageName = "dev.paperreader.extensions.sample.theme",
            displayName = "Blueprint sample theme",
            themeIds = setOf(THEME_ID),
        )
    }
}
