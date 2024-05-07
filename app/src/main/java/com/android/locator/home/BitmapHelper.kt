package com.android.locator.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.icu.text.SimpleDateFormat
import androidx.core.content.ContextCompat
import com.android.locator.R
import java.util.Date
import java.util.Locale

class BitmapHelper {



    companion object {

            var context: Context? = null

            fun set_Context(c:Context){
                context=c
            }
            fun addRoundedCornersToBitmap(originalBitmap: Bitmap, cornerRadius: Float): Bitmap {
                // Create a new bitmap with the same width and height as the original bitmap
                val width = originalBitmap.width
                val height = originalBitmap.height
                val roundedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                // Create a Canvas to draw on the new bitmap
                val canvas = Canvas(roundedBitmap)

                // Create a Path object to define the rounded corners
                val path = Path()
                path.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, Path.Direction.CW)

                // Create a Paint object to draw the bitmap with anti-aliasing enabled
                val paint = Paint()
                paint.isAntiAlias = true

                // Clip the canvas to the rounded corners path
                canvas.clipPath(path)

                // Draw the original bitmap onto the canvas, which will apply the rounded corners
                canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

                return roundedBitmap
            }

            fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
                // Create a matrix for resizing the bitmap
                val matrix = Matrix()

                // Calculate the scale factor for width and height
                val scaleWidth = width.toFloat() / bitmap.width
                val scaleHeight = height.toFloat() / bitmap.height

                // Set the scale factors on the matrix
                matrix.postScale(scaleWidth, scaleHeight)

                // Create a new resized bitmap using the matrix
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            fun addFrameToBitmap(originalBitmap: Bitmap, frameColor: Int, frameWidth: Int, time: Date): Bitmap {
                var color1=Color.BLUE
                var color2=Color.RED
                if(context!=null){
                    color1 = ContextCompat.getColor(context!!, R.color.light_orange)
                    color2 = ContextCompat.getColor(context!!, R.color.yellow_orange)
                }

                // Create a new bitmap with padding for the frame
                val framedWidth = originalBitmap.width + frameWidth
                val framedHeight = originalBitmap.height + frameWidth+50
                val framedBitmap = Bitmap.createBitmap(framedWidth, framedHeight+30, Bitmap.Config.ARGB_8888)

                // Create a Canvas to draw on the new bitmap
                val canvas = Canvas(framedBitmap)



                // Create a Paint object for the frame
                val paint = Paint().apply {
                    color = frameColor
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }

                // Create a Path object for the rounded rectangle
                val path = Path().apply {
                    addRoundRect(
                        0f, 0f,
                        framedWidth.toFloat(), framedHeight.toFloat(),
                        30f, 30f,
                        Path.Direction.CW
                    )
                }

                val shader = LinearGradient(
                    0f, 0f, 0f, framedHeight.toFloat(),
                    color1, color2, // Start and end colors of the gradient
                    Shader.TileMode.CLAMP // Tile mode for handling out-of-bounds values
                )

// Set the shader on the Paint object
                paint.shader = shader

                // Draw the colored rounded rectangle on the new bitmap
                canvas.drawPath(path, paint)

                val trianglePath = Path().apply {
                    moveTo(framedWidth / 2f-30, framedHeight.toFloat())
                    lineTo((framedWidth / 2f) + 30, framedHeight.toFloat())
                    lineTo((framedWidth / 2f), framedHeight.toFloat() +30)
                    close()
                }
                canvas.drawPath(trianglePath, paint)

                /*
                val ovalPath = Path().apply {
                    addOval(
                        RectF(
                            framedWidth / 2f - 30, // left
                            framedHeight.toFloat() + 30, // top
                            framedWidth / 2f + 30, // right
                            framedHeight.toFloat() + 90 // bottom
                        ),
                        Path.Direction.CW
                    )
                }

                val paint1 = Paint().apply {
                    //color = Color.argb(128, 128, 128, 128) // 128 alpha (transparency), 128 red, 128 green, 128 blue
                    color = Color.GRAY
                    style = Paint.Style.FILL // Set paint style to fill
                }

                canvas.drawPath(ovalPath, paint1)

                 */

                val textPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                    isAntiAlias = true
                    this.textSize = 50f
                    textAlign = Paint.Align.LEFT // Adjust text alignment as needed
                }

                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                // Draw the text on the bitmap at the specified coordinates
                canvas.drawText(dateFormat.format(time), 38f, 55f, textPaint)


                // Draw the original bitmap centered within the frame
                val left = frameWidth/2
                val top = frameWidth/2+50
                canvas.drawBitmap(originalBitmap, left.toFloat(), top.toFloat(), null)

                return framedBitmap
            }
        }
    }