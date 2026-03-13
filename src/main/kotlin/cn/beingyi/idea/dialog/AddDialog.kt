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
import cn.beingyi.idea.manager.ProjectPool
import cn.beingyi.idea.model.MappingBean
import cn.beingyi.idea.theme.WidgetTheme
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFileManager
import java.io.File
import javax.swing.JComponent

/**
 * author: zhengyu
 * date: 2021/8/4 14:53
 *
 */
class AddDialog(val project: Project) : DialogWrapper(project) {

    private val loadedProject = ProjectPool.getLoadedProjectOrCreate(project)

    val languageTag = mutableStateOf("")
    val stringXmlFile = mutableStateOf("")

    val errMsg = mutableStateOf("")

    init {
        title = "Add locate"
        init()

    }


    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setBounds(0, 0, 600, 400)
            setContent {

                WidgetTheme(darkTheme = true) {
                    Surface(modifier = Modifier.background(Color.Transparent)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize().weight(1.0f)) {

                                inputItem("languageTag", languageTag.value.lowercase()) {
                                    languageTag.value = it.lowercase()
                                }
                                //radioItem("defaultSrcXml", defaultSrcXml)
                                pathInputItem("stringXmlFile", stringXmlFile, true, false)

                            }
                            Text(
                                text = errMsg.value,
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun inputItem(itemName: String, value: String, onValueChange: (String) -> Unit) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.Start) {
            Text(
                modifier = Modifier.weight(0.3f).align(Alignment.CenterVertically),
                text = itemName,
                fontSize = 12.sp,
                color = Color.White
            )
            BasicTextField(
                modifier = Modifier.fillMaxHeight().padding(10.dp, 5.dp, 10.dp, 5.dp).weight(0.7f)
                    .align(Alignment.CenterVertically)
                    .background(Color(0xff45494a))
                    .border(1.dp, Color.White),
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, textAlign = TextAlign.Justify),
                cursorBrush = SolidColor(Color.White),
                value = value,
                onValueChange = onValueChange
            )

        }
    }


    @Composable
    private fun pathInputItem(
        itemName: String,
        value: MutableState<String>,
        chooseFile: Boolean,
        chooseFolder: Boolean
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.Start) {
            Text(
                modifier = Modifier.weight(0.3f).align(Alignment.CenterVertically),
                text = itemName,
                fontSize = 12.sp,
                color = Color.White
            )
            Row(modifier = Modifier.weight(0.7f).align(Alignment.CenterVertically)) {
                BasicTextField(modifier = Modifier.fillMaxHeight().padding(10.dp, 5.dp, 10.dp, 5.dp).weight(1.0f)
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
                Button(onClick = {
                    FileChooser.chooseFile(
                        FileChooserDescriptor(chooseFile, chooseFolder, false, false, false, false),
                        project,
                        VirtualFileManager.getInstance().findFileByNioPath(File(project.basePath).toPath())
                    ) {
                        value.value = it.path
                    }
                }) {
                    Text("Select")
                }
            }

        }
    }


    @Composable
    private fun radioItem(itemName: String, value: MutableState<Boolean>) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.Start) {
            Text(
                modifier = Modifier.weight(0.3f).align(Alignment.CenterVertically),
                text = itemName,
                fontSize = 12.sp,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxHeight()
                    .padding(10.dp, 5.dp, 10.dp, 5.dp).weight(0.7f)
                    .align(Alignment.CenterVertically)
            ) {

                RadioLayout("true", value, value.value)
                Spacer(modifier = Modifier.width(10.dp))
                RadioLayout("false", value, !value.value)

            }

        }
    }

    @Composable
    private fun RowScope.RadioLayout(label: String, value: MutableState<Boolean>, selected: Boolean) {
        Row(modifier = Modifier.align(Alignment.CenterVertically)) {
            RadioButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                selected = selected,
                onClick = {
                    if (selected) {
                        return@RadioButton
                    }
                    value.value = !value.value
                }
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 12.sp,
                text = label
            )
        }
    }

    override fun doOKAction() {
        if (languageTag.value.isEmpty()) {
            errMsg.value = "languageTag can not be empty"
            return
        }
        if (stringXmlFile.value.isEmpty()) {
            errMsg.value = "srcXmlPath can not be empty"
            return
        }
        val srcXmlPathFile = File(stringXmlFile.value)
        if (!srcXmlPathFile.exists()) {
            errMsg.value = "file does not exist：${srcXmlPathFile.absolutePath}"
            return
        }
        val mappingBean = MappingBean()
        mappingBean.languageTag = languageTag.value
        mappingBean.stringXmlFile = stringXmlFile.value

        if (loadedProject.isExistMapping(mappingBean)) {
            errMsg.value = "already exist the mapping"
            return
        }
        loadedProject.putMapping(mappingBean)
        super.doOKAction()
    }

}
