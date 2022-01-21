package cn.flowboot.entity;

import lombok.Data;

import java.util.List;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/05
 */
@Data
public class Customer {

    private String name;
    private List<Order> orderList;
}
