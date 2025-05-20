package com.savelyev.MobileApp.Utils
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText

object PhoneMaskHelper {
    private const val MASK = "+7 (###) ###-##-##"
    private const val FIXED_PREFIX = "+7 "
    private val DIGITS = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    fun applyPhoneMask(editText: EditText) {
        // Установите начальное значение
        editText.setText(FIXED_PREFIX)
        editText.setSelection(FIXED_PREFIX.length)

        editText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var lastCursorPos = FIXED_PREFIX.length

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                lastCursorPos = editText.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val text = editable.toString()

                // Защита префикса
                if (!text.startsWith(FIXED_PREFIX)) {
                    editText.setText(FIXED_PREFIX)
                    editText.setSelection(FIXED_PREFIX.length)
                    isFormatting = false
                    return
                }

                // Обработка ввода
                val cleanText = text
                    .replace("[^0-9]".toRegex(), "")
                    .drop(1) // Убираем "7" из префикса
                    .take(10)

                val formatted = buildFormattedText(cleanText)
                setTextWithSelection(editText, formatted)

                isFormatting = false
            }

            private fun buildFormattedText(cleanText: String): String {
                val sb = StringBuilder(FIXED_PREFIX)
                var index = 0

                for (char in MASK.drop(FIXED_PREFIX.length)) {
                    if (char == '#') {
                        if (index < cleanText.length) {
                            sb.append(cleanText[index])
                            index++
                        } else {
                            break
                        }
                    } else {
                        sb.append(char)
                    }
                }
                return sb.toString()
            }

            private fun setTextWithSelection(editText: EditText, text: String) {
                editText.setText(text)
                val newPos = when {
                    lastCursorPos <= FIXED_PREFIX.length -> FIXED_PREFIX.length
                    else -> calculateNewPosition(text, lastCursorPos)
                }
                editText.setSelection(newPos)
            }

            private fun calculateNewPosition(text: String, oldPos: Int): Int {
                var pos = oldPos.coerceIn(FIXED_PREFIX.length, text.length)
                while (pos > FIXED_PREFIX.length && text[pos - 1] !in DIGITS) {
                    pos--
                }
                return pos
            }
        })

        // Блокировка удаления префикса
        editText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                editText.selectionStart <= FIXED_PREFIX.length
            ) {
                true
            } else {
                false
            }
        }
    }
}