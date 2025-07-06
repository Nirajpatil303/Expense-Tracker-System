package com.expensetracker.user_service.Respository;

import com.expensetracker.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
