package com.freeankit.rxkotlinoperators.ui.RxBinding

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.freeankit.rxkotlinoperators.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rx_login_screen.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class RxLoginScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_login_screen)

        RxTextView.afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map {
                    emailWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(lengthGreaterThanSix)
                .compose(verifyEmailPattern)
                .compose(retryWhenError {
                    emailWrapper.error = it.message
                })
                .subscribe()

        val s = RxTextView.afterTextChangeEvents(editTextPassword)
                .skipInitialValue()
                .map {
                    passwordWrapper.error = null
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.just(it).map { it.trim() } // - abcdefg - |
                            .filter { it.length > 6 }
                            .singleOrError()
                            .onErrorResumeNext {
                                if (it is NoSuchElementException) {
                                    Single.error(Exception("Length must be greater than 9"))
                                } else {
                                    Single.error(it)
                                }
                            }
                            .toObservable()
                }
                .retryWhen { errors ->
                    errors.flatMap {
                        passwordWrapper.error = it.message
                        Observable.just(true)
                    }

                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(lengthGreaterThanSix)
//                .compose(retryWhenError {
//                    passwordWrapper.error = it.message
//                })
                .subscribe(
                        {
                            Log.d("OK", "$it")
                        },
                        {
                            Log.d("OK", "$it")
                        }
                )
    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }

        }
    }

    private val lengthGreaterThanSix = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() } // - abcdefg - |
                    .filter { it.length > 6 }
                    .singleOrError()
                    .onErrorResumeNext {
                        if (it is NoSuchElementException) {
                            Single.error(Exception("Length must be greater than 6"))
                        } else {
                            Single.error(it)
                        }
                    }
                    .toObservable()


        }
    }

    private val verifyEmailPattern = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() }
                    .filter {
                        Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    }
                    .singleOrError()
                    .onErrorResumeNext {
                        if (it is NoSuchElementException) {
                            Single.error(Exception("Email not valid"))
                        } else {
                            Single.error(it)
                        }
                    }
                    .toObservable()
        }
    }


}
