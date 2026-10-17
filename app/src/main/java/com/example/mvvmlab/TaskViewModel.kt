package com.example.mvvmlab

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn



class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
): ViewModel() {

// Private mutable state: only the ViewModel can change it
    private val _tasks = MutableStateFlow(repository.loadInitialTasks())

// Public read-only state: the UI can observe it but not modify it
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // Events from the UI come in as functions
    fun toggleDone(taskId: Int) {
        _tasks.update{ list ->
            list.map { task ->
                if (task.id == taskId) task.copy(done = !task.done) else task
            }
        }
    }
    fun addTask(title: String) {
        if (title.isBlank()) return
        _tasks.update { list ->
            val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list + Task(id = nextId, title = title.trim())
        }
    }

    // untuk filter
    private val _showOnlyOpen = MutableStateFlow(false)
    val showOnlyOpen = _showOnlyOpen.asStateFlow()

    fun setShowOnlyOpen(show: Boolean) {
        _showOnlyOpen.value = show
    }

    val visibleTasks = combine(_tasks, _showOnlyOpen) { tasks, showOnlyOpen ->
        if (showOnlyOpen) {
            tasks.filter { !it.done }
        } else {
            tasks
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _tasks.value
    )
}