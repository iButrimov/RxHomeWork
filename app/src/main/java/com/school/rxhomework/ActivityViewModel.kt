package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class ActivityViewModel : ViewModel() {
    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    private val postsPublishSubject = PublishSubject.create<Unit>()

    val observer : Observer<Unit> = postsPublishSubject

    init {
        refreshData()
    }

    private fun refreshData() {
        postsPublishSubject
                .switchMap {
                    Repository.getPosts().toObservable()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    _state.value = State.Loaded(it)
                                }
                            }
                        },
                        {
                            _state.value = State.Loaded(emptyList())
                        }
                )
    }
}
