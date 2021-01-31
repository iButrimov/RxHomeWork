package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class ActivityViewModel : ViewModel() {
    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    private val postsPublishSubject = PublishSubject.create<List<MainActivity.Adapter.Item>>()

    init {
        refreshData()
        postsPublishSubject.subscribe {
            _state.value = State.Loaded(it)
        }
    }

    private fun refreshData() {
        Repository.getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    postsPublishSubject.onNext(it)
                                }
                            }
                        },
                        {
                            postsPublishSubject.onNext(emptyList())
                        }
                )
    }

    fun processAction(action: Action) {
        when (action) {
            Action.RefreshData -> refreshData()
        }
    }
}
