package com.knu.quickthink.utils

import androidx.compose.ui.text.input.TextFieldValue

private fun cursorPositionToPixelOffset(
    textFieldValue: TextFieldValue,
    cursorPosition: Int,
    lineHeightPx: Float,
    totalLineCount: Int
): Int {
    val lines = textFieldValue.text.split("\n")                         // 가로 크기 계산해서 한 줄 넘어갔을 때도 split해줄 수 있다면 정말 베스트인데 일단 패스
    var offset = 0
    var line_num = 0
    for (element in lines) {
//        Timber.tag("cardEdit").d("element : $element")
        offset += element.length
        if (offset >= cursorPosition) {
            // Subtract the length of the current line from the offset
            // to get the offset within the line itself
            offset -= element.length
            break
        }
        // Add 1 to account for the line break character
        offset++
        line_num++
    }
    // Calculate the pixel offset based on line height
    return (offset / totalLineCount * lineHeightPx).toInt()
//    return (offset / lines.size.toFloat() * lineHeightPx).toInt()
}