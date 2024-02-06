package com.example.griptrainerapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface UserDao {
    // Insert a new user
    @Insert
    void insert(User user);

    // Update an existing user
    @Update
    void update(User user);

    // Get a user by username and password (for login)
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUser(String username, String password);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(int userId);

    // Training-related methods
    @Insert
    void insertTraining(Training training);

    @Query("SELECT * FROM trainings WHERE user_id = :userId")
    LiveData<List<Training>> getTrainingsForUser(int userId);

    // Section-related methods
    @Insert
    void insertSection(Section section);

    @Query("SELECT * FROM sections WHERE training_id = :trainingId")
    LiveData<List<Section>> getSectionsForTraining(int trainingId);

    // Insert a new manual training
    @Insert
    void insertManualTraining(Training training);

    // Get the count of manual trainings for a user
    @Query("SELECT COUNT(*) FROM trainings WHERE user_id = :userId AND training_name LIKE 'M%'")
    int getManualTrainingCount(int userId);

    // Insert a new manual training with the appropriate ID
    @Transaction
    public default void insertManualTrainingWithCustomId(Training training) {
        int userId = training.userId;
        int manualTrainingCount = getManualTrainingCount(userId);
        String manualTrainingId = "M" + (manualTrainingCount + 1); // Construct the new training ID
        training.trainingName = manualTrainingId;
        insertManualTraining(training);
    }
}
