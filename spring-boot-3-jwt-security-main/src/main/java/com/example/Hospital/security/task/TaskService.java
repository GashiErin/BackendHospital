package com.example.Hospital.security.task;

import org.springframework.stereotype.Service;
import java.util.List;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task createTask(Task task) {
        System.out.println("Saving task: " + task); // log for backend
        return taskRepository.save(task);
    }


    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTask(id);
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
//a
