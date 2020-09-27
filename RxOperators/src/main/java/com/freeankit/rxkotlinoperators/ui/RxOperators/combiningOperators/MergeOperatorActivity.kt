package com.freeankit.rxkotlinoperators.ui.RxOperators.combiningOperators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.freeankit.rxkotlinoperators.R
import com.freeankit.rxkotlinoperators.utils.Constant
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_example_operator.*
import java.util.concurrent.TimeUnit

/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 10/01/2018 (MM/DD/YYYY )
 */
class MergeOperatorActivity : AppCompatActivity() {

    val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_operator)

        btn.setOnClickListener { executeMerge() }
    }

    private fun executeMerge() {
//TODO ask students to find the root cause
        disposable.add(Observable.merge(
                source1(), source2()
        ).subscribe(
                { value ->
                    textView.append(" onNext : value : " + value)
                    textView.append(Constant().LINE_SEPARATOR)
                    Log.d(Constant().TAG, " onNext : value : " + value)
                },
                {

                },
                {
                    textView.append(" onComplete")
                    textView.append(Constant().LINE_SEPARATOR)
                    Log.d(Constant().TAG, " onComplete")
                },
                {

                }

        ))

    }

    private fun source1(): Observable<Long> {

        return Observable.create { emitter ->

            Observable.intervalRange(1, 10, 0, 1, TimeUnit.SECONDS).subscribe(
                    {
                        emitter.onNext(it)
                    },
                    {

                    },
                    {
                        emitter.onComplete()
                    }
            )
        }
    }

    private fun source2(): Observable<Long> {

        return Observable.create { emitter ->

            Observable.intervalRange(101, 5, 0, 1, TimeUnit.SECONDS).subscribe(
                    {
                        emitter.onNext(it)
                    },
                    {
                    },
                    {
                        emitter.onComplete()
                    }
            )
        }
    }

    /*
    * Using merge operator to combine Observable : merge does not maintain
    * the order of Observable.
    * It will emit all the 7 values may not be in order
    * Ex - "A1", "B1", "A2", "A3", "A4", "B2", "B3" - may be anything
    */

    private fun executeMergeOperator() {
        val aStrings = arrayOf("A1", "A2", "A3", "A4")
        val bStrings = arrayOf("B1", "B2", "B3")

        val aObservable = Observable.fromArray(*aStrings)
        val bObservable = Observable.fromArray(*bStrings)

        Observable.merge(aObservable, bObservable)
                .subscribe(getObserver())

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

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}