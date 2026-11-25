package com.example.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasks.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val dateEpochDay: Long,
    val timeMinutes: Int
)

fun TaskEntity.toDomain() = Task(
    id = id,
    title = title,
    description = description,
    dateEpochDay = dateEpochDay,
    timeMinutes = timeMinutes
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    dateEpochDay = dateEpochDay,
    timeMinutes = timeMinutes
)
