package com.capstone.chilifit.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chilifit.data.network.response.Article
import com.capstone.chilifit.helper.repository.Repository
import kotlinx.coroutines.launch

class ArticleViewModel(private val repository: Repository) : ViewModel() {

    private val _article = MutableLiveData<Article?>()
    val article: MutableLiveData<Article?> = _article

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchArticleById(id: Int) {
        viewModelScope.launch {
            try {
                val article = repository.getArticleById(id)
                if (article != null) {
                    _article.postValue(article)
                } else {
                    _error.postValue("Article not found")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}

