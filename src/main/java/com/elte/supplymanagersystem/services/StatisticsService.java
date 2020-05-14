package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.entities.Company;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.enums.Status;
import com.elte.supplymanagersystem.repositories.CompanyRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import com.elte.supplymanagersystem.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.elte.supplymanagersystem.enums.ErrorMessages.FORBIDDEN;

@Service
public class StatisticsService {

    static final Logger logger = Logger.getLogger(StatisticsService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CompanyRepository companyRepository;


    public ResponseEntity getMonthlyIncome(User loggedInUser) {
        logger.info("getMonthlyRecordedIncome() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR))) {
            ArrayList<Double> closedSaleCostPerMonth = new ArrayList<>();
            ArrayList<Double> activeSaleCostPerMonth = new ArrayList<>();

            LocalDate date = LocalDate.now();
            for (int currentMonth = 1; currentMonth <= 12; currentMonth++) {
                date = date.withMonth(currentMonth);
                double closedSaleCost = 0;
                double activeSaleCost = 0;
                if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                    for (Company company : companyRepository.findAll()) {
                        for (Order order : orderRepository.findSalesByWorkplace(company)) {
                            if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                                    && order.getCreatedAt().getMonthValue() + 1 == currentMonth) {
                                if (isClosed(order)) closedSaleCost += order.getPrice();
                                else activeSaleCost += order.getPrice();
                            }
                        }
                    }
                } else if (userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                    for (Order order : orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace())) {
                        if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                                && order.getCreatedAt().getMonthValue() + 1 == currentMonth) {
                            if (isClosed(order)) closedSaleCost += order.getPrice();
                            else activeSaleCost += order.getPrice();
                        }
                    }
                }
                activeSaleCostPerMonth.add(activeSaleCost);
                closedSaleCostPerMonth.add(closedSaleCost);
            }

            Map<String, ArrayList<Double>> results = new HashMap<>();
            results.put("activeSaleCostPerMonth", activeSaleCostPerMonth);
            results.put("closedSaleCostPerMonth", closedSaleCostPerMonth);
            return ResponseEntity.ok(results);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    public ResponseEntity getMonthlyExpense(User loggedInUser) {
        logger.info("getMonthlyApproximatedExpense() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR))) {
            ArrayList<Double> closedPurchaseCostPerMonth = new ArrayList<>();
            ArrayList<Double> activePurchaseCostPerMonth = new ArrayList<>();

            LocalDate date = LocalDate.now();
            for (int currentMonth = 1; currentMonth <= 12; currentMonth++) {
                date = date.withMonth(currentMonth);
                double closedPurchaseCost = 0;
                double activePurchaseCost = 0;
                if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                    for (Company company : companyRepository.findAll()) {
                        for (Order order : orderRepository.findPurchasesByWorkplace(company)) {
                            if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                                    && order.getCreatedAt().getMonthValue() + 1 == currentMonth) {
                                if (isClosed(order)) closedPurchaseCost += order.getPrice();
                                else activePurchaseCost += order.getPrice();
                            }
                        }
                    }
                } else if (userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                    for (Order order : orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace())) {
                        if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                                && order.getCreatedAt().getMonthValue() + 1 == currentMonth) {
                            if (isClosed(order)) closedPurchaseCost += order.getPrice();
                            else activePurchaseCost += order.getPrice();
                        }
                    }
                }
                activePurchaseCostPerMonth.add(activePurchaseCost);
                closedPurchaseCostPerMonth.add(closedPurchaseCost);
            }

            Map<String, ArrayList<Double>> results = new HashMap<>();
            results.put("activePurchaseCostPerMonth", activePurchaseCostPerMonth);
            results.put("closedPurchaseCostPerMonth", closedPurchaseCostPerMonth);
            return ResponseEntity.ok(results);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    public ResponseEntity getPartnerStatistics(User loggedInUser) {
        logger.info("getOrderCountStatistics() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_ADMIN))) {
            Map<String, Integer> partnerCount = new HashMap<>();
            for (Order order : orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace())) {
                if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                        && order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                    if (!partnerCount.containsKey(order.getBuyer().getName()))
                        partnerCount.put(order.getBuyer().getName(), 1);
                    else partnerCount.put(order.getBuyer().getName(), partnerCount.get(order.getBuyer().getName()) + 1);
                }
            }
            for (Order order : orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace())) {
                if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR)
                        && order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                    if (!partnerCount.containsKey(order.getSeller().getName()))
                        partnerCount.put(order.getSeller().getName(), 1);
                    else
                        partnerCount.put(order.getSeller().getName(), partnerCount.get(order.getSeller().getName()) + 1);
                }
            }

            return ResponseEntity.ok(partnerCount);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);

    }

    public ResponseEntity getOrderCountStatistics(User loggedInUser) {
        logger.info("getOrderCountStatistics() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR))) {
            Map<String, Integer> counts = new HashMap<>();
            int closedSales = 0;
            int activeSales = 0;
            int closedPurchases = 0;
            int activePurchases = 0;
            if (userService.userHasRole(loggedInUser, Role.ROLE_DIRECTOR)) {
                for (Order order : orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace())) {
                    if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR) &&
                            order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                        if (isClosed(order)) closedSales++;
                        else activeSales++;
                    }
                }
                for (Order order : orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace())) {
                    if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR) &&
                            order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                        if (isClosed(order)) closedPurchases++;
                        else activePurchases++;
                    }
                }
                counts.put("closedSales", closedSales);
                counts.put("activeSales", activeSales);
                counts.put("closedPurchases", closedPurchases);
                counts.put("activePurchases", activePurchases);
            } else if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                for (Order order : orderRepository.findAll()) {
                    if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR) &&
                            order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                        if (isClosed(order)) closedSales++;
                        else activeSales++;
                    }
                }
                for (Order order : orderRepository.findAll()) {
                    if (order.getCreatedAt().getYear() == Calendar.getInstance().get(Calendar.YEAR) &&
                            order.getCreatedAt().getMonthValue() == Calendar.getInstance().get(Calendar.MONTH) + 1) {
                        if (isClosed(order)) closedPurchases++;
                        else activePurchases++;
                    }
                }
                counts.put("closedSales", closedSales);
                counts.put("activeSales", activeSales);
                counts.put("closedPurchases", closedPurchases);
                counts.put("activePurchases", activePurchases);
            }
            return ResponseEntity.ok(counts);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    public ResponseEntity getUserCountByRole(User loggedInUser) {
        logger.info("getUserCountByRole() called");
        Map<String, Integer> userCount = new HashMap<>();
        int admin = 0;
        int director = 0;
        int manager = 0;

        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            for (User user : userRepository.findAll()) {
                switch (user.getRole()) {
                    case ROLE_ADMIN:
                        admin++;
                        break;
                    case ROLE_DIRECTOR:
                        director++;
                        break;
                    case ROLE_MANAGER:
                        manager++;
                        break;
                }
            }
            userCount.put("admin", admin);
            userCount.put("director", director);
            userCount.put("manager", manager);
            return ResponseEntity.ok(userCount);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    private boolean isClosed(Order order) {
        return order.getStatus() == Status.CLOSED || order.getStatus() == Status.SUCCESSFULLY_COMPLETED;
    }

}

