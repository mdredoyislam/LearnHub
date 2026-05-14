package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.entity.WithdrawRequest;
import com.redoy.FirstSpringBoot.repository.WithdrawRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawRequestRepository withdrawRequestRepository;
    private final PaymentService paymentService;

    public void requestWithdraw(UserAccount teacher, BigDecimal amount, String details) {
        BigDecimal balance = paymentService.getTeacherEarnings(teacher);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: ৳" + balance);
        }
        
        WithdrawRequest request = WithdrawRequest.builder()
                .teacher(teacher)
                .amount(amount)
                .paymentDetails(details)
                .status("PENDING")
                .build();
        withdrawRequestRepository.save(request);
    }

    public List<WithdrawRequest> getTeacherWithdrawals(UserAccount teacher) {
        return withdrawRequestRepository.findByTeacher(teacher);
    }

    public List<WithdrawRequest> getAllPendingWithdrawals() {
        return withdrawRequestRepository.findByStatus("PENDING");
    }

    public void approveWithdraw(Long id) {
        WithdrawRequest request = withdrawRequestRepository.findById(id).orElseThrow();
        request.setStatus("APPROVED");
        request.setProcessedAt(java.time.LocalDateTime.now());
        withdrawRequestRepository.save(request);
    }

    public void rejectWithdraw(Long id) {
        WithdrawRequest request = withdrawRequestRepository.findById(id).orElseThrow();
        request.setStatus("REJECTED");
        request.setProcessedAt(java.time.LocalDateTime.now());
        withdrawRequestRepository.save(request);
    }
}
