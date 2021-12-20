package org.example.repositories;

import org.example.model.ReceivedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReceivedTaskRepository extends JpaRepository<ReceivedTask, Integer> {
}
