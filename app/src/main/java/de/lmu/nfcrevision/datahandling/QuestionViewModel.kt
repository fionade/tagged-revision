package de.lmu.nfcrevision.datahandling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    var questionDao = QuestionDatabase.getDatabase(application).questionDao()

    fun insertQuestionAtLocation(question: Question){
        viewModelScope.launch {
            questionDao.insert(question)
        }
    }
}