package onestep.repository;

import onestep.model.Goal;
import onestep.model.Status;
import onestep.model.Task;
import java.util.List;

/**
 * Interface penyimpanan data.
 * GUI hanya mengenal interface ini, tidak perlu tahu detail SQLite.
 */
public interface TaskRepository {
    void initDatabase();
    List<Task> findAllTasksWithGoals();
    Task findTaskById(int taskId);
    Task saveTask(Task task);
    void deleteTask(int taskId);
    Goal saveGoal(Goal goal);
    void updateGoalStatus(int goalId, Status status);
    void deleteGoal(int goalId);
    void swapGoalPositions(Goal g1, Goal g2);
}
