package org.move.psi

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.DebugUtil
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.IncorrectOperationException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.swing.JComponent


class SavePsiFile : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        var myDialogData = SaveDialogWrapper.dialogData("")
        var dialog = SaveDialogWrapper(myDialogData)
        dialog.show()
        dialog.print()
        val flag = dialog.save(e, myDialogData.savePath)
        if (flag) {
            Messages.showInfoMessage("save success", "info")
        } else {
            Messages.showInfoMessage("save failed", "info")
        }

    }
}

private class SaveDialogWrapper(var dD: dialogData) : DialogWrapper(true) {
    init {
        init()
        title = "Save Current Psi File"
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("Path:") {
                val textField = textFieldWithBrowseButton(
                    null,
                    null,
                    FileChooserDescriptorFactory.createSingleFolderDescriptor().apply { title = "Select a directory" },
                    null
                )
                    .bindText({ dD.savePath }, { dD.savePath = it })

            }
        }
    }

    fun save(e: AnActionEvent, pathStr: String): Boolean {
        var flag = false
        WriteCommandAction.runWriteCommandAction(ActionEventUtil.getProject(e)) {
            val element = ActionEventUtil.getPsiFile(e)
            if (element is PsiFile) {
                val data = StringBuilder()
                for (file in element.viewProvider.allFiles) {
                    data.append(DebugUtil.psiToString(file!!, false, true))
                }
                println(data.toString())
                try {
                    val path = Paths.get(pathStr)
                    flag = writeTest(path.resolve("PSIFile/" + element.name + ".psi").toString(), data.toString())
                } catch (ex: IOException) {
                    throw RuntimeException(ex)
                }
            } else {
                if (element != null) {
                    throw IncorrectOperationException(element.toString() + " (" + element.javaClass + ")")
                }
            }
        }
        return flag
    }

    @Throws(IOException::class)
    fun writeTest(filename: String?, data: String): Boolean {
        val path = Paths.get(filename)
        println(path.toString())
        println(path.toAbsolutePath())
        val parent = path.parent
        if (!Files.exists(parent)) {
            Files.createDirectories(parent)
        }
        if (!Files.exists(path)) {
            Files.createFile(path)
        }
        val fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)
        val buffer = ByteBuffer.allocate(data.length)
        val position = 0
        //是否操作完成
        buffer.put(data.toByteArray())
        buffer.flip()
        val operation = fileChannel.write(buffer, position.toLong())
        buffer.clear()
        while (!operation.isDone);
        return true
//        println("Write done")
    }

    fun print() = println(dD.savePath)
    data class dialogData(var savePath: String) {
    }
}
