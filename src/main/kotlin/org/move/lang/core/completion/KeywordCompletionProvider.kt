package org.move.lang.core.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase.DUMMY_BLOCK
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import org.move.lang.MoveElementTypes.SCRIPT_BLOCK
import org.move.lang.core.psi.ext.getNextNonCommentSibling

class KeywordCompletionProvider(private vararg val keywords: String) : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        for (keyword in keywords) {
            val builder = LookupElementBuilder.create(keyword).bold()
            val element = addInsertionHandler(keyword, builder, parameters)
            result.addElement(PrioritizedLookupElement.withPriority(element, KEYWORD_PRIORITY))
        }
    }
}

private fun addInsertionHandler(
    keyword: String,
    builder: LookupElementBuilder,
    parameters: CompletionParameters,
): LookupElementBuilder {
    val suffix = when (keyword) {
        "script" -> {
            val nextSibling = parameters.position.parent.getNextNonCommentSibling()
            val aheadOfBlock =
                nextSibling.elementType == DUMMY_BLOCK || nextSibling.elementType == SCRIPT_BLOCK;
            if (aheadOfBlock) "" else " "
        }
        else -> " "
    }
    return builder.withInsertHandler { ctx, _ -> ctx.addSuffix(suffix) }
}