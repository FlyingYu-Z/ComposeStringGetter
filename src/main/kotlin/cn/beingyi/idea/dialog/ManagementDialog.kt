package cn.beingyi.idea.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.beingyi.idea.manager.ProjectSwitchManager
import cn.beingyi.idea.manager.mappingList
import cn.beingyi.idea.model.MappingBean
import cn.beingyi.idea.theme.WidgetTheme
import cn.beingyi.idea.view.Clickable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

/**
 * author: zhengyu
 * date: 2021/8/4 14:53
 *
 */
class ManagementDialog(val project: Project) : DialogWrapper(project) {

    init {
        title = "i18n Management"
        init()

    }


    override fun createActions(): Array<Action> {
        super.createActions()
        //myOKAction.putValue(Action.NAME, "Add")
        return arrayOf()
    }

    override fun createLeftSideActions(): Array<Action> {
        super.createLeftSideActions()
        return arrayOf()
    }

    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setBounds(0, 0, 1000, 600)
            setContent {
                WidgetTheme(darkTheme = true) {
                    Surface(modifier = Modifier.background(Color.Transparent)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize().weight(1.0f)) {
                                MappingItemHeader()
                                val list = mappingList
                                val listState = rememberLazyListState()

                                LazyColumn(modifier = Modifier.fillMaxSize(),state = listState) {
                                    items(key = { list.get(it) }, count = list.size) { index ->
                                        val item = list.get(index)
                                        MappingItem(project, item)
                                    }
                                }
                            }
                            Row {
                                Button(modifier = Modifier.align(Alignment.CenterVertically),
                                    onClick = {
                                        if(!ProjectSwitchManager.instance.hasBuildConfiguration(project)){
                                            YesNoDialog(project,"Tip","You need to set Build Configuration first"){
                                                SetBuildConfigurationDialog(project).show()
                                            }.show()
                                            return@Button
                                        }
                                        AddDialog(project).show()
                                    }
                                ) {
                                    Text("Add")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(modifier = Modifier.align(Alignment.CenterVertically),
                                    onClick = {
                                        SetBuildConfigurationDialog(project).show()
                                    }
                                ) {
                                    Text("Set Build Configuration")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun MappingItemHeader() {
    Column {
        Row(modifier = Modifier.height(40.dp)) {
            Text(
                modifier = Modifier.weight(1.5f).fillMaxHeight(),
                text = "LanguageTag",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Text(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "defaultSrcXml",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Text(
                modifier = Modifier.weight(5f).fillMaxHeight(),
                text = "srcXmlPath",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Text(
                modifier = Modifier.weight(2.5f).fillMaxHeight(),
                text = "Operation",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White))
    }
}

@Composable
fun MappingItem(project: Project, item: MappingBean) {
    Column {
        Row(modifier = Modifier.height(30.dp)) {
            Text(
                modifier = Modifier.weight(1.5f).fillMaxHeight(),
                text = item.languageTag,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Text(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = item.defaultSrcXml.toString(),
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Text(
                modifier = Modifier.weight(5f).fillMaxHeight(),
                text = item.srcXmlPath,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White))
            Box(
                modifier = Modifier.weight(2.5f).fillMaxHeight()
            ) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Clickable(modifier = Modifier.align(Alignment.CenterVertically),
                        shape = CircleShape,
                        onClick = {
                            YesNoDialog(project, "Tip", "Do you want do delete it?") {
                                ProjectSwitchManager.instance.removeMapping(project, item)
                                ProjectSwitchManager.instance.refreshMappingList(project)
                            }.show()
                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Clickable(modifier = Modifier.align(Alignment.CenterVertically),
                        shape = CircleShape,
                        onClick = {

                        }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White))
    }
}
