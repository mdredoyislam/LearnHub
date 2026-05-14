package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.WithdrawRequest;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {
    List<WithdrawRequest> findByTeacher(UserAccount teacher);
    List<WithdrawRequest> findByStatus(String status);
}
