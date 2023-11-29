import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class SaveAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(PlatformDataKeys.EDITOR)
        val selectedText = editor?.selectionModel?.selectedText

        if (selectedText != null) {
            val project = event.getData(PlatformDataKeys.PROJECT)
            if (project == null) {
                Messages.showErrorDialog("No project found", "Error")
                return
            }

            val basePath = project.basePath
            if (basePath == null) {
                Messages.showErrorDialog("Project base path not found", "Error")
                return
            }

            val filePath = "$basePath/presets/codes.txt"

            try {
                val file = File(filePath)
                if (!file.exists()) {
                    file.createNewFile()
                }

                BufferedWriter(FileWriter(file, true)).use { writer ->
                    val lines = selectedText.split("\n")
                    if (lines.isNotEmpty()) {
                        val firstLine = lines[0]
                        writer.write("# $firstLine\n")
                    }

                    writer.write(selectedText + "\n")
                }

                Messages.showMessageDialog("Text saved to $filePath", "Save", Messages.getInformationIcon())
            } catch (e: Exception) {
                e.printStackTrace()
                Messages.showErrorDialog("Error saving text to file", "Error")
            }
        } else {
            Messages.showMessageDialog("No text selected, nothing to save", "Save", Messages.getInformationIcon())
        }
    }
}
