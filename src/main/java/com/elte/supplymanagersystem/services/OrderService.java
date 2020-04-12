package com.elte.supplymanagersystem.services;

import com.elte.supplymanagersystem.dtos.HistoryDTO;
import com.elte.supplymanagersystem.dtos.OrderDTO;
import com.elte.supplymanagersystem.entities.History;
import com.elte.supplymanagersystem.entities.Order;
import com.elte.supplymanagersystem.entities.User;
import com.elte.supplymanagersystem.enums.Role;
import com.elte.supplymanagersystem.repositories.HistoryRepository;
import com.elte.supplymanagersystem.repositories.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.elte.supplymanagersystem.enums.ErrorMessages.FORBIDDEN;

@Service
public class OrderService {

    static final Logger logger = Logger.getLogger(OrderService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private HistoryRepository historyRepository;

    /**
     * Only ADMINS have the right to get all the Orders.
     * ADMIN: Can get ALL the Orders.
     * MANAGER, DIRECTOR, ELSE: FORBIDDEN
     *
     * @param loggedInUser The user who logged in.
     * @return Returns A ResponseEntity with All the Orders based on the Role of the User who logged in.
     */
    public ResponseEntity getAll(User loggedInUser) {
        logger.info("getAll() called");
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
            return ResponseEntity.ok(orderRepository.findAll());
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Returns the Order by ID.
     * ADMIN: Can get All the Orders.
     * DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at.
     * ELSE - FORBIDDEN
     * Non existing ID - NOT FOUND
     *
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the Order to return.
     * @return Returns a ResponseEntity of the Order.
     */
    public ResponseEntity getById(User loggedInUser, Integer id) {
        logger.info("getById() called");
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (orderToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
                return ResponseEntity.ok(orderToGet.get());
            else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToGet.get().getId()) != null) {
                    return ResponseEntity.ok(map.get(orderToGet.get().getId()));
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns the Histories of the order with the requested ID, filtered by Role,
     * Only those users can access the history who works in the same company as the Creator of the history,
     * and who works at one of the companies of the Order to which the History belongs to. (Plus ADMINS)
     * ADMIN: Can get ALL
     * DIRECTOR, MANAGER: Can get Data if works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: FORBIDDEN
     * Non existing Order: NOTFOUND
     *
     * @param loggedInUser The user who logged in.
     * @param id           The ID of the Order to get the Histories of.
     * @return Returns a ResponseEntity of the Histories.
     */
    public ResponseEntity getHistoriesByOrderId(User loggedInUser, Integer id) {
        logger.info("getHistoriesByOrderId() called");
        Optional<Order> orderToGet = orderRepository.findById(id);
        if (orderToGet.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(orderToGet.get().getHistories());
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToGet.get().getId()) != null) {
                    ArrayList<History> authorizedHistories = new ArrayList<>();
                    orderToGet.get().getHistories().stream().filter(history ->
                            history.getCreator().getWorkplace().getId().equals(loggedInUser.getWorkplace().getId())).
                            forEach(authorizedHistories::add); //KIEMELNI/HIGHLIGHT
                    return ResponseEntity.ok(authorizedHistories);
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new record of History for the Selected Order.
     * ADMIN: Can add new Histories without any regulations.
     * DIRECTOR, MANAGER: Only can add History if the user works in the same company as the Creator of the history,
     * and also works at one of the companies of the Order to which the History belongs to.
     * ELSE: FORBIDDEN
     *
     * @param idOfOrder    The ID of Order to which we want to add the history to.
     * @param historyDTO   The history Data Transfer Object with the information to save.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity of the saved History.
     */
    public ResponseEntity postHistoryForOrderById(HistoryDTO historyDTO, User loggedInUser, Integer idOfOrder) {
        logger.info("postHistoryForOrderById() called");
        Optional<Order> orderToGet = orderRepository.findById(idOfOrder);
        if (orderToGet.isPresent()) {
            History historyToSave = new History(historyDTO);
            historyToSave.setOrder(orderToGet.get());
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                if (historyToSave.getCreator() == null)
                    historyToSave.setCreator(loggedInUser);
                return ResponseEntity.ok(historyRepository.save(historyToSave));
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                historyToSave.setCreator(loggedInUser);
                historyToSave.setOrder(orderToGet.get());
                if (orderToGet.get().getBuyer().getId().equals(loggedInUser.getWorkplace().getId())
                        || orderToGet.get().getSeller().getId().equals(loggedInUser.getWorkplace().getId()))
                    return ResponseEntity.ok(historyRepository.save(historyToSave));
                else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Returns the Sales of the Company the user works at.
     * ADMIN, DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at (even admins)
     * ELSE - FORBIDDEN
     * ID NOT FOUND - NOT FOUND
     * Unemployed user - BAD REQUEST
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the Orders where the user's company is a seller.
     */
    public ResponseEntity getSalesByUser(User loggedInUser) {
        logger.info("getSalesByUser() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            if (loggedInUser.getWorkplace() != null) {
                List<Order> currentCompany = orderRepository.findSalesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Returns the Purchases of the Company the user works at.
     * ADMIN, DIRECTOR, MANAGER: Get orders if issued by, or for the company the user works at (even admins)
     * ELSE - FORBIDDEN
     * ID NOT FOUND - NOT FOUND
     * Unemployed user - BAD REQUEST
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a ResponseEntity with the Orders where the user's company is a buyer.
     */
    public ResponseEntity getPurchasesByUser(User loggedInUser) {
        logger.info("getPurchasesByUser() called");
        if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_ADMIN, Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            if (loggedInUser.getWorkplace() != null) {
                List<Order> currentCompany = orderRepository.findPurchasesByWorkplace(loggedInUser.getWorkplace());
                return ResponseEntity.ok(currentCompany);
            } else return ResponseEntity.badRequest().build();
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Updates the Order by ID.
     * ADMIN: Can save any of the Order.
     * DIRECTOR, MANAGER: Update Order if issued by, or for the company the user works at.
     * ELSE: FORBIDDEN
     * Non existing Order: NOTFOUND
     *
     * @param orderDTO     The Order Data Transfer Object with the information to update.
     * @param loggedInUser The user logged in.
     * @param id           The ID of the Order the user wants to PUT (Update).
     * @return Returns a ResponseEntity of the updated Order.
     */
    public ResponseEntity putById(OrderDTO orderDTO, User loggedInUser, Integer id) {
        logger.info("putById() called");
        Order orderToUpdate = new Order(orderDTO);
        orderToUpdate.setId(id);
        Optional<Order> orderToCheck = orderRepository.findById(id);
        if (orderToCheck.isPresent()) {
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                return ResponseEntity.ok(orderRepository.save(orderToUpdate));
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                Map<Integer, Order> map = getMap(loggedInUser);
                if (map.get(orderToUpdate.getId()) != null) {
                    return ResponseEntity.ok(orderRepository.save(orderToUpdate));
                } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new record of Order.
     * ADMIN: Can add new Order without any regulations.
     * DIRECTOR, MANAGER: Can only add Order of the Company the user works at is a seller or a buyer in the Order.
     * ELSE: FORBIDDEN
     * Already existing Order: BAD REQUEST
     *
     * @param orderDTO     The Order Data Transfer Object with the information to save.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity of the saved Order.
     */
    //Add
    public ResponseEntity addOrder(OrderDTO orderDTO, User loggedInUser) {
        logger.info("addOrder() called");
        Order orderToSave = new Order(orderDTO);
        if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN))
            return ResponseEntity.ok(orderRepository.save(orderToSave));
        else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
            //TODO throw bad request to null values
            if (orderToSave.getBuyer().getId().equals(loggedInUser.getWorkplace().getId())
                    || orderToSave.getSeller().getId().equals(loggedInUser.getWorkplace().getId())) {
                return ResponseEntity.ok(orderRepository.save(orderToSave));
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Checks if the Order has any relations to other objects.
     *
     * @param orderToDelete The Order to check
     * @return boolean
     */
    private boolean isDeletable(Order orderToDelete) {
        return orderToDelete.getHistories().isEmpty();
    }

    /**
     * Deletes an Order record by ID.
     * ADMIN: Can delete any Order without any regulations.
     * DIRECTOR, MANAGER:  Can only delete Order of the Company the user works at is a seller or a buyer in the Order.
     * ELSE: FORBIDDEN
     * Non existing Order: NOTFOUND
     *
     * @param id           The ID of the Company the user wants to DELETE.
     * @param loggedInUser The user logged in.
     * @return Returns a ResponseEntity: OK if the deletion was successful and NotFound if the record was not found.
     */
    //Remove
    public ResponseEntity deleteById(Integer id, User loggedInUser) {
        logger.info("deleteById() called");
        Optional<Order> orderToDelete = orderRepository.findById(id);
        if (orderToDelete.isPresent()) {
            //orderToDelete.get().setHistories(new ArrayList<>());
            if (userService.userHasRole(loggedInUser, Role.ROLE_ADMIN)) {
                orderRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else if (userService.userHasRole(loggedInUser, List.of(Role.ROLE_DIRECTOR, Role.ROLE_MANAGER))) {
                return deleteByDirectorOrManager(id, loggedInUser, orderToDelete.get());
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Helper method for deleteById(...)
     * In case a user wants to delete an order these checks needed to be conducted.
     * Throws FORBIDDEN if the user doesn't have permission to delete the specific Order
     *
     * @param id            The ID of the Company the user wants to DELETE.
     * @param loggedInUser  The user logged in.
     * @param orderToDelete Order to delete
     * @return Returns a ResponseEntity
     */
    private ResponseEntity deleteByDirectorOrManager(Integer id, User loggedInUser, Order orderToDelete) {
        Map<Integer, Order> map = getMap(loggedInUser);
        if (map.get(orderToDelete.getId()) != null) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN);
    }

    /**
     * Returns a Map with the orders of a company the User works at.
     * Unauthorized if the key value is null.
     *
     * @param loggedInUser The user who logged in.
     * @return Returns a Map[Integer, Order] with the ID-s of the Orders are the keys.
     */
    public Map<Integer, Order> getMap(User loggedInUser) {
        List<Order> ordersOfCompany = orderRepository.findAllOrderByWorkplace(loggedInUser.getWorkplace());
        return ordersOfCompany.stream().collect(Collectors.toMap(Order::getId, order -> order));
    }
}
