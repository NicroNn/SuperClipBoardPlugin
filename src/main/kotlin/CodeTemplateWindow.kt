import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.GridLayout
import java.io.File
import java.nio.charset.Charset
import javax.swing.JButton
import javax.swing.JPanel

class CodeTemplateWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val panel = SimpleToolWindowPanel(true, true)
        val basePath = project.basePath
        val presetsFolder = File("$basePath/presets")

        val buttonPanel = JPanel(GridLayout(0, 1))

        presetsFolder.listFiles()?.forEach { file ->
            if (file.isFile) {
                val fileContent = file.readText(Charset.defaultCharset())
                val entries = fileContent.split("#").map { it.trim() }.filter { it.isNotEmpty() }

                entries.forEachIndexed { _, entryText ->
                    val label = entryText.lines().firstOrNull()
                    val button = JButton(label)
                    button.addActionListener {
                        insertText(entryText, project)
                    }
                    buttonPanel.add(button)
                }
            }
        }

        val scrollPane = JBScrollPane(buttonPanel)
        panel.add(scrollPane)
        toolWindow.contentManager.addContent(contentFactory.createContent(panel, "", false))
    }

    private fun insertText(text: String, project: Project) {
        SimpleDataContext.getProjectContext(project)
        val editor = EditorFactory.getInstance().allEditors.firstOrNull()

        if (editor != null) {
            WriteCommandAction.runWriteCommandAction(project) {
                val lines = text.lines()
                val textToInsert = lines.subList(1, lines.size).joinToString("\n")

                editor.document.insertString(editor.caretModel.offset, textToInsert)
            }
        }
    }
}
