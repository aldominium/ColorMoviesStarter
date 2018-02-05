/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package aldominium.com.colormovies

import aldominium.com.colormovies.models.MovieResponse
import aldominium.com.colormovies.models.Review
import aldominium.com.colormovies.service.NYTimesReviews
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

  private val displayTitle = "display_title"
  private val summaryShort = "summary_short"
  private val byline = "byline"
  private val color = "color"
  private val TAG = this.javaClass.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)

    with(savedInstanceState) {
      val color = getInt(color)

      titleTextView.text = getString(displayTitle)
      titleTextView.setTextColor(color)

      summaryTextView.text = getString(summaryShort)
      summaryTextView.setTextColor(color)

      bylineTextView.text = getString(byline)
      bylineTextView.setTextColor(color)

      movieButton.setBackgroundColor(color)

      window.statusBarColor = color
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)

    outState.putInt(color, titleTextView.currentTextColor)
    outState.putString(byline, bylineTextView.text.toString())
    outState.putString(summaryShort, summaryTextView.text.toString())
    outState.putString(displayTitle, titleTextView.text.toString())
  }

  fun buildUI(review: Review) {
    val colors: IntArray = resources.getIntArray(R.array.phrasesColors)
    val color = colors.getRandomElement()

    window.statusBarColor = color

    summaryTextView.apply {
      text = review.summary_short
      setTextColor(color)
    }

    bylineTextView.apply {
      text = review.byline
      setTextColor(color)
    }

    titleTextView.apply {
      text = review.display_title
      setTextColor(color)
    }

    movieButton.apply {
      setBackgroundColor(color)
    }
  }

  fun getReviews(view: View) {
    progressBar.visibility = View.VISIBLE

    NYTimesReviews.getReviews().enqueue(object : Callback<MovieResponse> {

      override fun onFailure(call: Call<MovieResponse>?, t: Throwable?) {
        Snackbar.make(mainLayout, R.string.api_error, Snackbar.LENGTH_LONG).show()
        Log.d(TAG, "Problems getting reviews with error: ${t?.message}")
        progressBar.visibility = View.GONE
      }

      override fun onResponse(call: Call<MovieResponse>?, response: retrofit2.Response<MovieResponse>?) {
        response?.let { movieReponse ->
          if (movieReponse.isSuccessful) {
            val body = movieReponse.body()
            val review = body?.getRandomElement()
            if (review != null) {
              buildUI(review)
            }
          }
        }
        progressBar.visibility = View.GONE
      }
    })
  }
}
