package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nguyenthimynguyen.example10.cafe.entity.TableEntity;
import com.nguyenthimynguyen.example10.cafe.entity.enums.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "WHERE o.table = :table AND o.status = :status")
    Optional<Order> findByTableAndStatusWithItems(@Param("table") TableEntity table, @Param("status") OrderStatus status);

    // nếu cần dùng cho findAllWithDetails used in controller:
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.table t " +
           "LEFT JOIN FETCH o.employee e " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "LEFT JOIN FETCH o.promotion pr " )
    List<Order> findAllWithDetails();
   // Lấy order hiện tại của bàn theo trạng thái cụ thể
    Optional<Order> findByTableAndStatus(TableEntity table, OrderStatus status);

    // Lấy tất cả order theo trạng thái
    List<Order> findByStatus(OrderStatus status);

    // Lấy order gần nhất của bàn, bất kể trạng thái
    Optional<Order> findTopByTableOrderByCreatedAtDesc(TableEntity table);

    // Lấy order hiện tại của bàn, trừ trạng thái PAID và CANCELLED
    Optional<Order> findFirstByTableAndStatusNotInOrderByCreatedAtDesc(
        TableEntity table, List<OrderStatus> excludedStatuses
    );

    Optional<Order> findFirstByTableAndStatusOrderByCreatedAtDesc(TableEntity table, OrderStatus status);

    // Lấy tất cả order của một bàn theo trạng thái (dùng bởi service)
    List<Order> findAllByTableAndStatus(TableEntity table, OrderStatus status);

    /**
     * Trả về Object[] các row cho các truy vấn thống kê
     */
    @Query("SELECT p.id, p.name, SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "WHERE o.status = :status " +
           "GROUP BY p.id, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT c.name, SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "LEFT JOIN p.category c " +
           "WHERE o.status = :status " +
           "GROUP BY c.name")
    List<Object[]> findRevenueByCategory(@Param("status") OrderStatus status);

    @Query("SELECT FUNCTION('DATE', o.createdAt), SUM(o.totalAmount) " +
           "FROM Order o " +
           "WHERE o.status = :status " +
           "GROUP BY FUNCTION('DATE', o.createdAt) " +
           "ORDER BY FUNCTION('DATE', o.createdAt)")
    List<Object[]> findRevenueByDay(@Param("status") OrderStatus status);

List<Order> findAllByTableAndStatusNotIn(TableEntity table, List<OrderStatus> excludedStatuses);

}


