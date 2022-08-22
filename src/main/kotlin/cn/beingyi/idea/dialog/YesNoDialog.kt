package cn.beingyi.idea.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.beingyi.idea.manager.ProjectSwitchManager
import cn.beingyi.idea.theme.WidgetTheme
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modifyModules
import com.intellij.openapi.ui.DialogWrapper
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import javax.swing.Action
import javax.swing.JComponent

/**
 * author: zhengyu
 * date: 2021/8/4 14:53
 *
 */
class YesNoDialog(val project: Project, title: String, val msg: String, val callback: () -> Unit) :
    DialogWrapper(project) {

    init {
        setTitle(title)
        init()
    }


    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setBounds(0, 0, 400, 500)
            setContent {
                WidgetTheme(darkTheme = true) {
                    Surface(modifier = Modifier.background(Color.Transparent)) {
                        Column {
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    override fun doOKAction() {
        callback.invoke()
        close(CLOSE_EXIT_CODE)
    }

    override fun doCancelAction() {
        super.doCancelAction()
    }

}