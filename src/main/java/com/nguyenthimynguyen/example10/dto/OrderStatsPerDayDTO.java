package com.nguyenthimynguyen.example10.dto;

import java.math.BigDecimal;
import java.util.Date;

public class OrderStatsPerDayDTO {
    private Date date;        // <-- dùng java.util.Date
    private BigDecimal totalRevenue;
    private Long totalOrders;

    public OrderStatsPerDayDTO(Date date, BigDecimal totalRevenue, Long totalOrders) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
    }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
}
