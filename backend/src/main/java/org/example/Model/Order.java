package org.example.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Entity
@Table(name="orderBicycle")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_order")
    private Integer id;
    @Column(name = "id_user")
    @JsonProperty("userId")
    private Integer userId;
    @Column(name = "id_bicycle")
    @JsonProperty("bicycleId")
    private Integer bicycleId;
    @Column(name = "startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startDate;
    @Column(name = "endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endDate;
    @Column(name = "price")
    private Integer price;
    @Column(name = "countHours")
    private Integer countHours;
    @Column(name = "countDays")
    private Integer countDays;
    @Column(name = "id_status")
    private Integer id_status;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getBicycleId() {
        return bicycleId;
    }
    public void setBicycleId(Integer bicycleId) {
        this.bicycleId = bicycleId;
    }
    public OffsetDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }
    public OffsetDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public Integer getCountHours() {
        return countHours;
    }
    public void setCountHours(Integer countHours) {
        this.countHours = countHours;
    }
    public Integer getCountDays() {
        return countDays;
    }
    public void setCountDays(Integer countDays) {
        this.countDays = countDays;
    }
    public Integer getStatus() {
        return id_status;
    }
    public void setStatus(Integer status) {
        this.id_status = status;
    }
}
