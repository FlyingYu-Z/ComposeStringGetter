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
import javax.swing.JComponent

/**
 * author: zhengyu
 * date: 2021/8/4 14:53
 *
 */
class StringPropertyDialog(project: Project?, editor: Editor) : DialogWrapper(project) {

    private val moduleList = ArrayList<Module>()
    private val project: Project

    private val selectionModel: SelectionModel
    private val document: Document

    val resNameState = mutableStateOf("")
    val resValueState = mutableStateOf("")

    init {
        title = "Extra Resource"
        init()
        this.project = project!!

        project?.modifyModules {
            modules.forEach { module ->
                moduleList.add(module)
            }
        }

        selectionModel = editor.selectionModel
        document = editor.document
        val selectedText = selectionModel.selectedText
        if (!StringUtils.isEmpty(selectedText)) {
            resValueState.value = selectedText!!
        }

    }

    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setBounds(0, 0, 300, 500)
            setContent {

                WidgetTheme(darkTheme = true) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.background(Color.Transparent).fillMaxSize()) {
                            inputItem("Resource name", resNameState)
                            inputItem("Resource value", resValueState)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun inputItem(itemName: String, value: MutableState<String>) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.Start) {
            Text(
                modifier = Modifier.weight(0.3f).align(Alignment.CenterVertically),
                text = itemName,
                fontSize = 12.sp,
                color = Color.White
            )
            BasicTextField(modifier = Modifier.fillMaxHeight().padding(10.dp, 5.dp, 10.dp, 5.dp).weight(0.7f)
                .align(Alignment.CenterVertically)
                .background(Color(0xff45494a))
                .border(1.dp, Color.White),
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, textAlign = TextAlign.Justify),
                cursorBrush = SolidColor(Color.White),
                value = value.value,
                onValueChange = {
                    value.value = it
                }
            )

        }
    }

    override fun doOKAction() {
        if (resNameState.value.isEmpty()) {
            return
        }
//        val projectInfo = ProjectSwitchManager.instance.getProjectInfo(project)
//        projectInfo?.mainLanguageFile?.checkValue(resNameState.value, resValueState.value)
//
//        val app = ApplicationManager.getApplication()
//        WriteCommandAction.runWriteCommandAction(project) {
//            app.runWriteAction {
//                document.replaceString(selectionModel.selectionStart, selectionModel.selectionEnd, resNameState.value)
//            }
//        }

        close(CLOSE_EXIT_CODE)
    }

    override fun doCancelAction() {
        super.doCancelAction()
    }

}