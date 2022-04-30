package com.github.netkex.mindmap

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.JComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.IconLoader

@Composable
fun logList(modifier: Modifier = Modifier, log: List<String>) {
    LazyColumn(modifier = modifier) {
        items(log.toList()) { message ->
            Text(message)
        }
    }
}

@Composable
fun countButton(
    modifier: Modifier = Modifier,
    count: Int,
    updateCount: (Int) -> Unit,
    text: String
) {
    Button(modifier = modifier,
        onClick = {
            updateCount(count + 1)
        }) {
        Text(text)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun keyBoardWindow(name: String, endTextEvent: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = name)
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().onKeyEvent {
                when {
                    (it.key == Key.Enter) -> {
                        endTextEvent(text.dropLast(1))
                        text = ""
                        true
                    }
                    else -> false
                }
            }
        )
    }
}

@Composable
fun myApp(content: @Composable () -> Unit) {
    Surface {
        content()
    }
}


@Composable
fun mindMapApp(context: Context) {
    println("minMapApp intro")
    var countAdd by remember { context.countAdd }
    var countDelete by remember { context.countDelete }
    var keyboardFlag by remember { context.keyboardFlag }
    var keyboardAction by remember { context.keyboardAction }
    var keyboardActionName by remember { context.keyboardActionName }
    var texts by remember { context.texts }
    myApp {
        if (!keyboardFlag) {
            Column(modifier = Modifier.fillMaxSize().fillMaxHeight()) {
                Column(modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally)) {
                    logList(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        log = texts
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                    countButton(
                        modifier = Modifier,
                        count = countAdd,
                        updateCount = { countNew ->
                            countAdd = countNew
                            keyboardFlag = true
                            keyboardAction = { text ->
                                texts.add(text)
                                keyboardFlag = false
                            }
                            keyboardActionName = "Write log to add"
                        },
                        "Add Count: $countAdd"
                    )

                    countButton(
                        modifier = Modifier,
                        count = countDelete,
                        updateCount = { countNew ->
                            countDelete = countNew
                            keyboardFlag = true
                            keyboardAction = { text ->
                                texts = texts.filter { it != text }.toMutableList()
                                keyboardFlag = false
                            }
                            keyboardActionName = "Write log to delete"
                        },
                        "Delete Count: $countDelete"
                    )
                }
            }
        } else {
            keyBoardWindow(keyboardActionName, keyboardAction)
        }
    }
}
//IconLoader.getIcon("META-INF/pluginIcon.svg")
//DumbAwareAction(null, null, null)

class PanelAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val p = e.project
        println("I was activated")
        println("Project is: $p")
        if (p == null) {
            return
        }

        ToolWindowManager.getInstance(p).getToolWindow("MindMap")?.show()
    }

    override fun update(e: AnActionEvent) {
        println("WAS ACTION WITH UPDATE $e")
        super.update(e)
    }

    companion object {
        fun createPanel(context: Context): JComponent {
            println("I was called")
            return ComposePanel().apply {
                setBounds(0, 0, 1600, 1000)
                setContent {
                    println("Maybe me")
                    mindMapApp(context)
                }
            }
        }
    }
}