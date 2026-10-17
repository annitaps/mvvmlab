package com.example.mvvmlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskViewModelTest {
    // A fake repository gives us control over the starting data
    private class FakeRepository : TaskRepository() {

        override fun loadInitialTasks(): List<Task> = listOf(
            Task(1, "First"),
            Task(2, "Second", done = true)
        )
    }

    private class EmptyRepository : TaskRepository() {
        override fun loadInitialTasks(): List<Task> = emptyList()
    }

    @Test
    fun togglebone_flipsTheTaskState() {
        val viewModel = TaskViewModel(FakeRepository())

        viewModel.toggleDone(1)

        assertTrue(viewModel.tasks.value.first { it.id == 1 }.done)
    }

    @Test
    fun toggleDone_twice_returnsToOriginalState() {
        val viewModel = TaskViewModel(FakeRepository())

        viewModel.toggleDone(2)
        viewModel.toggleDone(2)

        assertTrue(viewModel.tasks.value.first { it.id == 2 }.done)
    }

    @Test
    fun addTask_appendsWithNextId() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask("Third")
        assertEquals(3, viewModel.tasks.value.size)
        assertEquals(3, viewModel.tasks.value.last().id)
        assertEquals("Third", viewModel.tasks.value.last().title)
    }

    @Test
    fun addTask_ignoresBlankTitle() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask(" ")
        assertEquals(2, viewModel.tasks.value.size)
    }
    //test extension emptystate
    @Test
    fun emptyRepository_hasNoVisibleTasks() {
        val viewModel = TaskViewModel(EmptyRepository())

        assertTrue(viewModel.tasks.value.isEmpty())
        assertTrue(viewModel.visibleTasks.value.isEmpty())
    }
}