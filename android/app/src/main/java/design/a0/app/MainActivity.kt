package design.a0.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import design.a0.app.ui.comp.A0Background
import design.a0.app.ui.comp.ToastHost
import design.a0.app.ui.screens.ApiKeysScreen
import design.a0.app.ui.screens.ChatScreen
import design.a0.app.ui.screens.ExploreScreen
import design.a0.app.ui.screens.PreviewScreen
import design.a0.app.ui.screens.ProjectsScreen
import design.a0.app.ui.screens.SettingsScreen
import design.a0.app.ui.screens.UsageScreen
import design.a0.app.ui.sheets.A0BottomNav
import design.a0.app.ui.sheets.A0Drawer
import design.a0.app.ui.sheets.A0SheetHost
import design.a0.app.ui.sheets.MiniDesignPill
import design.a0.app.ui.theme.A0
import design.a0.app.ui.theme.A0Theme
import design.a0.app.vm.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: AppViewModel = viewModel()
            A0Theme {
                AppRoot(vm)
            }
        }
    }
}

@Composable
private fun AppRoot(vm: AppViewModel) {
    A0Background {
        Box(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            // ---- screen router ----------------------------------------
            AnimatedContent(
                targetState = vm.screen,
                transitionSpec = {
                    (
                        fadeIn(tween(420, easing = A0.Ease)) +
                            slideInHorizontally(tween(480, easing = A0.Ease)) { it / 14 } +
                            scaleIn(tween(480, easing = A0.Ease), initialScale = 0.985f)
                        ).togetherWith(
                        fadeOut(tween(220)) +
                            slideOutHorizontally(tween(220)) { -it / 20 } +
                            scaleOut(tween(220), targetScale = 0.99f)
                    )
                },
                label = "screen"
            ) { screen ->
                when (screen) {
                    "prev" -> PreviewScreen(vm)
                    "prj" -> ProjectsScreen(vm)
                    "tpl" -> ExploreScreen(vm)
                    "use" -> UsageScreen(vm)
                    "set" -> SettingsScreen(vm)
                    "key" -> ApiKeysScreen(vm)
                    else -> ChatScreen(vm)
                }
            }

            // ---- floating chrome --------------------------------------
            val showNav = vm.screen in listOf("prj", "tpl", "set")
            val showMini = vm.screen in listOf("prj", "tpl")
            MiniDesignPill(
                vm,
                visible = showMini,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
                    .fillMaxWidth()
                    .height(84.dp)
            )

            A0BottomNav(
                vm, visible = showNav,
                modifier = Modifier.fillMaxSize()
            )

            ToastHost(vm.toast, Modifier.align(Alignment.TopCenter))

            A0Drawer(vm)
            A0SheetHost(vm)
        }
    }
}
