package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.SavedTransactionResponseDto;
import com.fullStack.expenseTracker.dto.requests.SavedTransactionRequestDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.enums.ETransactionFrequency;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.TransactionNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.SavedTransaction;
import com.fullStack.expenseTracker.models.Transaction;
import com.fullStack.expenseTracker.repository.SavedTransactionRepository;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.CategoryService;
import com.fullStack.expenseTracker.services.SavedTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class SavedTransactionServiceImpl implements SavedTransactionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavedTransactionRepository savedTransactionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryService categoryService;


    @Override
    public ResponseEntity<ApiResponseDto<?>> createSavedTransaction(SavedTransactionRequestDto requestDto)
            throws UserServiceLogicException, UserNotFoundException {
        try {
            if (!userRepository.existsById(requestDto.getUserId())) {
                throw new UserNotFoundException("User not found with id: " + requestDto.getUserId());
            }

            SavedTransaction plannedTransaction = Objects.requireNonNull(
                    savedTransactionDtoToEntity(requestDto),
                    "Failed to map saved transaction request");

            savedTransactionRepository.save(plannedTransaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Transaction has been successfully created!"
                    )
            );
        } catch (UserNotFoundException e) {
            throw e;
        } catch (CategoryNotFoundException e) {
            throw new UserServiceLogicException("Invalid category selected for transaction");
        } catch (Exception e) {
            throw new UserServiceLogicException("Failed to create transaction. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addSavedTransaction(long savedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                    .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId));

            Transaction transaction = Objects.requireNonNull(
                    savedTransactionToTransaction(plannedTransaction),
                    "Failed to convert saved transaction into transaction");
            transactionRepository.save(transaction);

            LocalDate upcomingDate = getUpcomingDate(plannedTransaction.getFrequency(), plannedTransaction.getUpcomingDate());

            plannedTransaction.setUpcomingDate(upcomingDate);
            savedTransactionRepository.save(plannedTransaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Transaction has been successfully saved!"
                    )
            );
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (CategoryNotFoundException e) {
            throw new UserServiceLogicException("Invalid category selected for transaction");
        } catch (UserNotFoundException e) {
            throw new UserServiceLogicException(e.getMessage());
        } catch (Exception e) {
            throw new UserServiceLogicException("Failed to add transaction. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editSavedTransaction(long plannedTransactionId, SavedTransactionRequestDto requestDto)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            SavedTransaction plannedTransaction = savedTransactionRepository.findById(plannedTransactionId)
                    .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + plannedTransactionId));

            plannedTransaction.setTransactionTypeId(
                    categoryService.getCategoryById(requestDto.getCategoryId()).getTransactionType().getTransactionTypeId());
            plannedTransaction.setAmount(requestDto.getAmount());
            plannedTransaction.setDescription(requestDto.getDescription());
            plannedTransaction.setFrequency(requestDto.getFrequency());
            plannedTransaction.setUpcomingDate(requestDto.getUpcomingDate());
            plannedTransaction.setCategoryId(requestDto.getCategoryId());

            savedTransactionRepository.save(plannedTransaction);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Transaction has been successfully edited!"
                    )
            );
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (CategoryNotFoundException e) {
            throw new UserServiceLogicException("Invalid category selected for transaction");
        } catch (Exception e) {
            throw new UserServiceLogicException("Failed to edit transaction. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteSavedTransaction(long plannedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (!savedTransactionRepository.existsById(plannedTransactionId)) {
                throw new TransactionNotFoundException("Transaction not found with id: " + plannedTransactionId);
            }

            savedTransactionRepository.deleteById(plannedTransactionId);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Transaction deleted successfully!"
                    )
            );
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UserServiceLogicException("Failed to delete transaction. Try again later.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> skipSavedTransaction(long savedTransactionId) throws UserServiceLogicException, TransactionNotFoundException {
        try {
            SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                    .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId));

            LocalDate upcomingDate = getUpcomingDate(plannedTransaction.getFrequency(), plannedTransaction.getUpcomingDate());

            plannedTransaction.setUpcomingDate(upcomingDate);
            savedTransactionRepository.save(plannedTransaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Transaction has been successfully skipped for period!"
                    )
            );
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UserServiceLogicException("Failed to add transaction. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllTransactionsByUser(long userId) throws UserServiceLogicException, UserNotFoundException {
        try {
            if (userRepository.existsById(userId)) {
                List<SavedTransaction> transactions = savedTransactionRepository.findByUserIdOrderByUpcomingDateAsc(userId);

                List<SavedTransactionResponseDto> response = new ArrayList<>();

                for (SavedTransaction t: transactions) {
                    response.add(savedTransactionToDto(t));
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                response
                        )
                );

            }
        } catch (CategoryNotFoundException e) {
            log.error("Failed to fetch transactions due to invalid category: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to fetch transactions. Invalid category reference.");
        } catch (RuntimeException e) {
            log.error("Unexpected error while fetching transactions: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to fetch transactions. Try again later");
        }
        throw new UserNotFoundException("User not found with id: " + userId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllTransactionsByUserAndMonth(long userId) throws UserServiceLogicException, UserNotFoundException {
        try {
            if (userRepository.existsById(userId)) {
                List<SavedTransaction> transactions = savedTransactionRepository.findByUserIdOrderByUpcomingDateAsc(userId).stream()
                        .filter(t -> t.getUpcomingDate().getMonthValue() == LocalDate.now().getMonthValue())
                        .toList();

                List<SavedTransactionResponseDto> response = new ArrayList<>();

                for (SavedTransaction t: transactions) {
                    response.add(savedTransactionToDto(t));
                }

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                response
                        )
                );

            }
        } catch (CategoryNotFoundException e) {
            throw new UserServiceLogicException("Failed to fetch transactions. Invalid category reference.");
        } catch (RuntimeException e) {
            throw new UserServiceLogicException("Failed to fetch transactions. Try again later");
        }
        throw new UserNotFoundException("User not found with id: " + userId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getSavedTransactionById(long savedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                    .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId));

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            plannedTransaction
                    )
            );
        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new UserServiceLogicException("Failed to fetch transaction. Try again later.");
        }
    }

    private SavedTransaction savedTransactionDtoToEntity(SavedTransactionRequestDto requestDto) throws CategoryNotFoundException {
        return SavedTransaction.builder()
                .transactionTypeId(categoryService.getCategoryById(requestDto.getCategoryId()).getTransactionType().getTransactionTypeId())
                .categoryId(requestDto.getCategoryId())
                .userId(requestDto.getUserId())
                .amount(requestDto.getAmount())
                .description(requestDto.getDescription())
                .upcomingDate(requestDto.getUpcomingDate())
                .frequency(requestDto.getFrequency())
                .build();
    }

    private Transaction savedTransactionToTransaction(SavedTransaction savedTransaction)
            throws CategoryNotFoundException, UserNotFoundException {
        return new Transaction(
                userRepository.findById(savedTransaction.getUserId())
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + savedTransaction.getUserId())),
                categoryService.getCategoryById(savedTransaction.getCategoryId()),
                savedTransaction.getDescription(),
                savedTransaction.getAmount(),
                savedTransaction.getUpcomingDate()
        );
    }

    private LocalDate getUpcomingDate(ETransactionFrequency frequency, LocalDate currentDate) {
        if (frequency == ETransactionFrequency.DAILY) {
            return currentDate.plusDays(1);
        }
        if (frequency == ETransactionFrequency.MONTHLY) {
            return currentDate.plusMonths(1);
        }
        return null;
    }

    private SavedTransactionResponseDto savedTransactionToDto(SavedTransaction savedTransaction)
            throws CategoryNotFoundException {
        return new SavedTransactionResponseDto(
                savedTransaction.getPlanId(),
                savedTransaction.getTransactionTypeId(),
                categoryService.getCategoryById(savedTransaction.getCategoryId()).getCategoryName(),
                savedTransaction.getAmount(),
                savedTransaction.getDescription(),
                savedTransaction.getFrequency(),
                getDueInformation(savedTransaction)
        );
    }

    private String getDueInformation(SavedTransaction transaction) {
        if (transaction.getUpcomingDate() == null) return null;
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now()))
            return "Due on Today";
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().plusDays(1)))
            return "Due on Tomorrow";
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().plusDays(2)))
            return "Due on a day after tomorrow";
        if (transaction.getFrequency() == ETransactionFrequency.MONTHLY && transaction.getUpcomingDate().isBefore(LocalDate.now())) {
            Period period = Period.between(transaction.getUpcomingDate(), LocalDate.now());
            if (period.getMonths() >= 0 && period.getDays() > 0)
                return period.getMonths() + 1 + " Months over due";
            return period.getMonths() + " Months over due";
        }
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().minusDays(1)))
            return "1 day overdue";
        if (transaction.getUpcomingDate().isBefore(LocalDate.now())){
            Period period = Period.between(transaction.getUpcomingDate(), LocalDate.now());
            long days =period.getYears()* 365L + period.getMonths()* 30L + period.getDays();
            return days + " days overdue";
        }
        return "Due on " + transaction.getUpcomingDate();

    }
}
