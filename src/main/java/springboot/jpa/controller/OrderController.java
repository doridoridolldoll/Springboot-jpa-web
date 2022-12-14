package springboot.jpa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot.jpa.dto.OrderDto;
import springboot.jpa.entity.Member;
import springboot.jpa.entity.Order;
import springboot.jpa.entity.OrderSearch;
import springboot.jpa.entity.item.Item;
import springboot.jpa.repository.ItemRepository;
import springboot.jpa.repository.MemberRepository;
import springboot.jpa.repository.OrderRepository;
import springboot.jpa.service.OrderService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    @GetMapping("/order")
    public String OrderForm(Model model) {
        List<Member> members = memberRepository.findAll();
        List<Item> items = itemRepository.findAllItem();

        model.addAttribute("members", members);
        model.addAttribute("items", items);
        model.addAttribute("form", new OrderDto());
        return "order/orderForm";
    }

    @PostMapping("/order")
    public String Order(Long memberId, Long itemId, int count) {
        orderService.Order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @DeleteMapping("/orders/cancel/{orderId}")
    public String cancel(@PathVariable("orderId") Long orderId) {
        log.info("orderId ={}", orderId);
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
