package com.udacity

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import androidx.core.content.ContextCompat
import com.udacity.MainActivity.Companion.DEFAULT_CIRCLE_RADIUS
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private val animator = ObjectAnimator()
    private var text: String = ""
    private var progress = 0
    private var radius = 0
    private var loading = false
    private val paint = Paint()
    private val loadingPaint = Paint()
    private val paintText = Paint()
    private val paintCircle = Paint()
    private var rectF: RectF

    private var buttonStringResId: Int

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            R.style.AppTheme
        )

        text = a.getString(R.styleable.LoadingButton_text) ?: ""
        paintText.color = a.getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        paintText.textSize = a.getDimension(R.styleable.LoadingButton_textSize, R.dimen.default_text_size.toFloat())
        paintText.textAlign = Paint.Align.CENTER
        loadingPaint.color = context.getColor(R.color.colorPrimaryDark)
        paint.color = a.getColor(R.styleable.LoadingButton_color, ContextCompat.getColor(context, R.color.colorPrimary ))
        paintCircle.color = Color.YELLOW

        buttonStringResId = R.string.download


        val text = context.getString(buttonStringResId)
        val centerX = (measuredWidth / 2f + loadingPaint.measureText(text))
        val centerY = measuredHeight / 2f+loadingPaint.measureText(text)

        rectF = RectF(
            centerX - 40f,
            centerY - 40f,
            centerX - 40f + DEFAULT_CIRCLE_RADIUS*2,
            centerY  - 40f + DEFAULT_CIRCLE_RADIUS*2
        )
        a.recycle()
    }

    private fun initAnimation() {
        animator.setValues(
            PropertyValuesHolder.ofInt("BACKGROUND_COLOR", 0, widthSize),
            PropertyValuesHolder.ofInt("RADIUS", 0, 360)
        )

        animator.duration = 3000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animator ->
            if (animator != null) {
                buttonState = ButtonState.Loading
                progress = animator.getAnimatedValue("BACKGROUND_COLOR") as Int
                radius = animator.getAnimatedValue("RADIUS") as Int
            }
            invalidate()
        }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                initAnimation()
                buttonStringResId = R.string.button_loading
                setDownloadButtonText()
                animator.start()
            }
            ButtonState.Loading -> {
                loading = true
                buttonStringResId = R.string.button_loading
                setDownloadButtonText()
                custom_button.isClickable = false
                invalidate()
            }
            ButtonState.Completed -> {
                animator.end()
                loading = false
                buttonStringResId = R.string.download
                setDownloadButtonText()
                custom_button.isClickable = true
                invalidate()
            }
        }
    }

    fun setDownloadButtonState(buttonState: ButtonState) {
        this.buttonState = buttonState
    }

    fun setDownloadButtonText() {
       text = resources.getString(buttonStringResId)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            paint
        )

        when {
            loading -> {
                canvas.drawRect(0f, 0f, progress.toFloat(), heightSize.toFloat(), loadingPaint)
                canvas.drawArc(rectF, 360f, radius.toFloat(), true, paintCircle)
            }
        }
        canvas.drawText(text, widthSize / 2f, heightSize / 2f, paintText)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}