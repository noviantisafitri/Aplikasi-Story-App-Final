package com.dicoding.noviantisafitri.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.dicoding.noviantisafitri.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class TextInput : TextInputEditText {
    private var errorAlertBG: Drawable? = null
    private var defaultAlertBG: Drawable? = null
    private var errorReport: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (errorReport) {
            errorAlertBG
        } else {
            defaultAlertBG
        }
    }

    private fun init() {
        errorAlertBG = ContextCompat.getDrawable(context, R.drawable.bg_edt_error)
        defaultAlertBG = ContextCompat.getDrawable(context, R.drawable.bg_edt_default)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateInput(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                validateInput(p0.toString())
            }

            private fun validateInput(input: String) {
                when (inputType) {
                    NAME -> {
                        if (input.trim().isEmpty() || !input.matches("[a-zA-Z0-9\\s]+".toRegex())) {
                            error = context.getString(R.string.name_validation)
                            errorReport = true
                        } else {
                            errorReport = false
                        }
                    }
                    EMAIL -> {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            error = context.getString(R.string.email_validation)
                            errorReport = true
                        } else {
                            errorReport = false
                        }
                    }
                    PASSWORD -> {
                        errorReport = if (input.length < 8) {
                            setError(context.getString(R.string.password_length), null)
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        })
    }

    companion object {
        const val NAME = 0x00000001
        const val EMAIL = 0x00000021
        const val PASSWORD = 0x00000081
    }
}