package com.example.tinkoff.ui.fragments.people

import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.User

class PeopleViewModel : ViewModel() {
    var list: List<User>? = null
}