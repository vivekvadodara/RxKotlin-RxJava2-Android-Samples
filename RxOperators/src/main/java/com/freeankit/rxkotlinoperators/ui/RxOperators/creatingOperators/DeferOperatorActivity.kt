package com.freeankit.rxkotlinoperators.ui.RxOperators.creatingOperators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.freeankit.rxkotlinoperators.R
import com.freeankit.rxkotlinoperators.model.Bike
import com.freeankit.rxkotlinoperators.utils.Constant
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_example_operator.*

/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 11/01/2018 (MM/DD/YYYY )
 */
class DeferOperatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_operator)

        btn.setOnClickListener({ executeDeferOperator() })
    }


    /*
   * Defer used for Deferring Observable code until subscription in RxJava
   */
    private fun executeDeferOperator() {
        val bike = Bike()
        bike.setBrand("Bicycle")

        val brandDeferObservable = bike.brandDeferObservable()

        bike.setBrand("Harley Davidson")  // Even if we are setting the brand after creating Observable
        // we will get the brand as Harley Davidson.
        // If we had not used defer, we would have got null as the brand.

        brandDeferObservable.subscribe(getObserver())

    }

    private fun getObserver(): Observer<String> {
        return object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d(Constant().TAG, " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                textView.append(" onNext : value : " + value)
                textView.append(Constant().LINE_SEPARATOR)
                Log.d(Constant().TAG, " onNext : value : " + value)
            }

            override fun onError(e: Throwable) {
                textView.append(" onError : " + e.message)
                textView.append(Constant().LINE_SEPARATOR)
                Log.d(Constant().TAG, " onError : " + e.message)
            }

            override fun onComplete() {
                textView.append(" onComplete")
                textView.append(Constant().LINE_SEPARATOR)
                Log.d(Constant().TAG, " onComplete")
            }
        }
    }
}