package com.example.foodxy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.foodxy.core.Resource
import com.example.foodxy.domain.home.HomeScreenRepo
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class HomeScreenViewModel(private val repo: HomeScreenRepo): ViewModel() {

    fun fetchLatestPost() = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(repo.getLatestPost())
        }catch (e: Exception){

            emit(Resource.Failure(e))
        }
    }
}

class HomeScreenViewModelFactory(private val repo: HomeScreenRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo)
    }

}