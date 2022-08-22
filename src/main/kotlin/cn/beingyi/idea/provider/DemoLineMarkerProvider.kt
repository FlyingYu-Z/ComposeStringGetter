package cn.beingyi.idea.provider

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.json.JsonElementTypes
import com.intellij.navigation.GotoRelatedItem
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.NotNullFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.ColorPicker
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon

internal val REGX_COLOR_LINE = "val(.*?)=\\s*?Color\\((\\s+)?(.*?)(\\s+)?\\)"
internal val REGX_COLOR_RGBA = "(\\d+{3}),\\s*?(\\d+{3}),\\s*?(\\d+{3}),\\s*?(\\d+{3})"
internal val REGX_COLOR_RGB = "(\\d+{3}),\\s*?(\\d+{3}),\\s*?(\\d+{3})"
internal val REGX_Color_VALUE = "Color\\((.*?)\\)"

class DemoLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val text = element.text
        if (!element.node.elementType.toString().equals(JsonElementTypes::PROPERTY.name)) return
        if (REGX_COLOR_LINE.toRegex().containsMatchIn(text)) {
            val colorHex: String = REGX_COLOR_LINE.toRegex().find(text)?.groups?.get(3)?.value ?: return
            // Support for Color Hex(0xFFFFFFFF)
            if (colorHex.length == 10 && colorHex.startsWith("0XFF", true)) {
                val color = Color.decode("#" + colorHex.substring(4))
                result.add(getLineMarker(element, color, true, ColorType.Hex))
            }
            // Support for Color RGBA(red,blue,green,alpha,...)
            else if (colorHex.matches(REGX_COLOR_RGBA.toRegex())) {
                val matches = REGX_COLOR_RGBA.toRegex().find(colorHex)?.groups!!
                val red = matches[1]?.value?.toInt() ?: 0
                val green = matches[2]?.value?.toInt() ?: 0
                val blue = matches[3]?.value?.toInt() ?: 0
                val alpha = matches[4]?.value?.toInt() ?: 255
                val color = Color(red, green, blue, alpha)
                result.add(getLineMarker(element, color, true, ColorType.RGBA))
            }
            // Support for Color RGB(red,blue,green)
            else if (colorHex.matches(REGX_COLOR_RGB.toRegex())) {
                val matches = REGX_COLOR_RGB.toRegex().find(colorHex)?.groups!!
                val red = matches[1]?.value?.toInt() ?: 0
                val green = matches[2]?.value?.toInt() ?: 0
                val blue = matches[3]?.value?.toInt() ?: 0
                val color = Color(red, green, blue)
                result.add(getLineMarker(element, color, true, ColorType.RGB))
            }
        }

    }

    private fun getColorIcon(color: Color): Icon {
        val image = UIUtil.createImage(7, 7, BufferedImage.TYPE_INT_RGB)
        image.graphics.apply {
            setColor(color)
            fillRect(0, 0, 7, 7)
        }
        return ImageIcon(image)
    }

    private fun getLineMarker(
        element: PsiElement,
        originColor: Color,
        runAction: Boolean = false,
        colorType: ColorType
    ): RelatedItemLineMarkerInfo<PsiElement> {
        val navHandler = if (runAction) GutterIconNavigationHandler { _, elt: PsiElement ->
            if (elt.containingFile.isWritable) {
                ColorPicker.showColorPickerPopup(elt.project, originColor) { newColor: Color, _ ->
                    if (newColor.rgb == originColor.rgb) return@showColorPickerPopup
                    runWriteAction {
                        val text = element.text
                        val originValue = REGX_Color_VALUE.toRegex().find(text)?.groups!![1]?.value
                        val new = with(newColor) {
                            when (colorType) {
                                ColorType.Hex -> "Color(0x${Integer.toHexString(rgb).uppercase()})"
                                ColorType.RGB -> "Color(${red}, ${green}, ${blue})"
                                ColorType.RGBA -> "Color(${red}, ${green}, ${blue}, ${alpha})"
                            }
                        }
                        val psiFile =
                            PsiFileFactory.getInstance(element.project).createFileFromText(element.language, new)
                        val newElement = element.children.find {
                            it.text.equals("Color(${originValue})", ignoreCase = true)
                        }
                        newElement?.replace(psiFile.children[2])
                        CodeStyleManager.getInstance(element.project).reformat(element)

                    }
                }
            }
        } else null

        return object : RelatedItemLineMarkerInfo<PsiElement>(
            element,
            element.textRange,
            getColorIcon(originColor),
            null,
            navHandler,
            GutterIconRenderer.Alignment.CENTER,
            NotNullFactory<Collection<GotoRelatedItem>> { emptyList() }
        ) {}
    }
}

enum class ColorType {
    Hex,
    RGB,
    RGBA
}